package esoterum.world.blocks.binary.basis;

import arc.struct.*;
import esoterum.util.*;
import esoterum.world.blocks.binary.basis.BinaryBlock.*;
import esoterum.world.blocks.binary.transmission.BinaryRouter.*;
import esoterum.world.blocks.binary.basis.BinarySink.*;
import esoterum.world.blocks.binary.basis.BinarySource.*;
import esoterum.world.blocks.binary.transmission.BinaryWire.*;
import mindustry.gen.*;

public class WireGraph{
    protected final static Queue<BinaryBuild> wireQueue = new Queue<>();

    public boolean active;
    public Seq<BinaryBuild> all = new Seq<>();
    public Seq<BinarySourceBuild> sources = new Seq<>();
    public Seq<BinarySinkBuild> sinks = new Seq<>();

    public void update(){
        active = sources.contains(s -> s.isActive(this));
        sinks.each(Building::updateTile);
    }

    public void updateConnected(BinaryBuild build){
        all = new Seq<>();
        sources = new Seq<>();
        sinks = new Seq<>();
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
}