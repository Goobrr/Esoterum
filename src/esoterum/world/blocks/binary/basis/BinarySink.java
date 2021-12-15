package esoterum.world.blocks.binary.basis;

import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import esoterum.world.blocks.binary.transmission.BinaryWire.*;
import mindustry.gen.*;
import mindustry.world.*;

public class BinarySink extends BinarySource{
    public BinarySink(String name){
        super(name);

        rotate = true;
    }

    public class BinarySinkBuild extends BinarySourceBuild{
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
                if(build instanceof BinaryWireBuild w){
                    connections.set(i, w.signal);
                }else{
                    connections.set(i, null);
                }
            }
        }
    }
}