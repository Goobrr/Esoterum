package esoterum.world.blocks.binary.basis;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import esoterum.world.blocks.binary.transmission.BinaryWire.*;
import mindustry.gen.*;

import java.util.*;

public class BinarySource extends BinaryBlock{
    public BinarySource(String name){
        super(name);
    }

    public class BinarySourceBuild extends BinaryBuild{
        /** Connected wire graphs.
         * [Front, Left, Back, Right] */
        public Seq<SignalGraph> connections = new Seq<>(4);

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            getConnections();
        }

        public void getConnections(){
            left();
            for(int i = 0; i < 4; i++){
                Building build = nearby(Mathf.mod(i + rotation, 4));
                if(build instanceof BinaryBuild w){
                    connections.set(i, w.signal);
                }else{
                    connections.set(i, null);
                }
            }
        }

        @Override
        public void updateTile(){
            connections.each(Objects::nonNull, SignalGraph::update);
        }

        /** Weather or not this source is seen as active to graphs. */
        public boolean isActive(SignalGraph graph){
            return false;
        }

        public void drawConnections(){
            for(int i = 0; i < 4; i++){
                Building build = nearby(i);
                if(build instanceof BinaryWireBuild) Draw.rect(connectionRegion, x, y, 90f * i);
            }
        }
    }
}