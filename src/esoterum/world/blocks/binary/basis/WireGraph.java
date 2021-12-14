package esoterum.world.blocks.binary.basis;

import arc.struct.*;
import esoterum.world.blocks.binary.basis.BinaryBlock.*;
import esoterum.world.blocks.binary.basis.BinarySink.*;
import esoterum.world.blocks.binary.basis.BinarySource.*;
import esoterum.world.blocks.binary.transmission.BinaryWire.*;
import mindustry.gen.*;

public class WireGraph{
    protected final static Queue<BinaryBuild> wireQueue = new Queue<>();

    public boolean active;
    public Seq<BinarySourceBuild> sources = new Seq<>();
    public Seq<BinarySinkBuild> sinks = new Seq<>();

    public void update(){
        active = sources.contains(s -> s.isActive(this));
        sinks.each(Building::updateTile);
    }

    public void updateConnected(BinaryBuild build){
        sources = new Seq<>();
        sinks = new Seq<>();
        wireQueue.clear();
        wireQueue.add(build);

        while(!wireQueue.isEmpty()){
            BinaryBuild next = wireQueue.removeLast();

            for(Building b : next.proximity){
                if(b instanceof BinaryWireBuild w && !wireQueue.contains(w)){
                    w.signal.sources = sources;
                    w.signal.sinks = sinks;
                    wireQueue.addFirst(w);
                }else if(b instanceof BinarySinkBuild s){
                    sinks.add(s);
                }else if(b instanceof BinarySourceBuild s){
                    sources.add(s);
                }
            }
        }
    }

    public void removeConnected(BinaryBuild tile){
        for(Building build : tile.proximity){
            if(build instanceof BinaryWireBuild b){
                b.signal.updateConnected(b);
            }
        }
    }
}