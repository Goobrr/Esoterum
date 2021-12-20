package esoterum.world.blocks.binary.basis;

import arc.struct.*;
import arc.struct.Queue;
import esoterum.util.*;
import esoterum.world.blocks.binary.basis.BinaryBlock.*;
import esoterum.world.blocks.binary.transmission.BinaryRouter.*;
import esoterum.world.blocks.binary.basis.BinarySink.*;
import esoterum.world.blocks.binary.basis.BinarySource.*;
import esoterum.world.blocks.binary.transmission.BinaryWire.*;
import mindustry.gen.*;

import java.util.*;

public class SignalGraph{
    protected final static Queue<BinaryBuild> wireQueue = new Queue<>();

    public boolean active;
    public Seq<BinaryBuild> all = new Seq<>();
    public Seq<BinarySourceBuild> sources = new Seq<>();
    public Seq<BinarySinkBuild> sinks = new Seq<>();

    public void update(){
        clearNull();
        active = sources.contains(s -> s.isActive(this));
        sinks.each(BinarySinkBuild::signalUpdate);
    }

    public void updateConnected(BinaryBuild build){
        if(build == null || build.signal == null) return;

        all = new Seq<>();
        sources = new Seq<>();
        sinks = new Seq<>();

        if(build instanceof BinarySinkBuild s){
            all.add(s);
            sources.add(s);
            return;
        }

        wireQueue.clear();
        wireQueue.add(build);

        while(!wireQueue.isEmpty()){
            BinaryBuild next = wireQueue.removeLast();

            for(Building b : next.proximity){
                if(b instanceof BinaryWireBuild w && w.signal.all != all){
                    int dirTo = EsoUtil.relativeDirection(next, w);
                    int dirFrom = EsoUtil.relativeDirection(w, next);
                    if(dirTo == 0 || dirFrom == 0){
                        addWire(w);
                    }
                }else if(b instanceof BinaryRouterBuild r && r.signal.all != all){
                    //always add router, regardless of rotation
                    addWire(r);
                }else if(b instanceof BinarySourceBuild s){
                    //don't add sources that aren't pointed at the wire
                    boolean isSink = s instanceof BinarySinkBuild;
                    int dirFrom = EsoUtil.relativeDirection(s, next);
                    if(s.block.rotate && dirFrom != 0 && !isSink) continue;
                    all.add(s);
                    sources.add(s);
                    if(isSink) sinks.add((BinarySinkBuild)s);
                }
            }
        }

        update();
    }

    public void addWire(BinaryRouterBuild wire){
        wire.signal.all = all;
        wire.signal.sources = sources;
        wire.signal.sinks = sinks;
        all.add(wire);
        wireQueue.addFirst(wire);
    }

    public void removeConnected(BinaryBuild tile){
        for(Building build : tile.proximity){
            if(build instanceof BinaryRouterBuild b){
                b.signal.updateConnected(b);
            }
        }
    }

    public void clearNull(){
        all.removeAll(Objects::isNull);
        sources.removeAll(Objects::isNull);
        sinks.removeAll(Objects::isNull);
    }
}