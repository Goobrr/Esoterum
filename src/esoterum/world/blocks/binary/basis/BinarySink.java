package esoterum.world.blocks.binary.basis;

import arc.math.*;
import arc.struct.*;
import arc.util.io.*;
import esoterum.world.blocks.binary.transmission.BinaryRouter.*;
import mindustry.gen.*;

public class BinarySink extends BinarySource{
    public BinarySink(String name){
        super(name);

        rotate = true;
    }

    public class BinarySinkBuild extends BinarySourceBuild{
        public boolean active;
        /** Connected wire graphs.
         * [Front, Left, Back, Right] */
        public Seq<WireGraph> connections = new Seq<>(4);

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            getConnections();
        }

        public void getConnections(){
            left();
            for(int i = 0; i < 4; i++){
                Building build = nearby(Mathf.mod(i + rotation, 4));
                if(build instanceof BinaryRouterBuild w){
                    connections.set(i, w.signal);
                }else{
                    connections.set(i, null);
                }
            }
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.bool(active);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 2) active = read.bool();
        }

        @Override
        public byte version(){
            return 2;
        }
    }
}