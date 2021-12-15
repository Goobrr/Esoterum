package esoterum.world.blocks.binary.basis;

import arc.struct.*;
import esoterum.world.blocks.binary.transmission.BinaryWire.*;
import mindustry.gen.*;

public class BinarySink extends BinarySource{
    public BinarySink(String name){
        super(name);

        rotate = true;
    }

    public class BinarySinkBuild extends BinarySourceBuild{
        public Seq<WireGraph> connections = new Seq<>(4);

        public float getConnections(){
            for(int i = 0; i < 4; i++){
                Building build = proximity.get(i);
                if(build instanceof BinaryWireBuild w){
                    connections.set(i, w.signal);
                }else{
                    connections.set(i, null);
                }
            }
        }
    }
}