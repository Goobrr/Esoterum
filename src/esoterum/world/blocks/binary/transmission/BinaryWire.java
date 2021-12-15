package esoterum.world.blocks.binary.transmission;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import esoterum.world.blocks.binary.basis.*;
import mindustry.gen.*;
import mindustry.logic.*;

public class BinaryWire extends BinaryBlock{
    public BinaryWire(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        connectionRegion = Core.atlas.find("esoterum-connection-large");
    }

    public class BinaryWireBuild extends BinaryBuild{
        public WireGraph signal;

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();

            signal.updateConnected(this);
        }

        @Override
        public void onProximityRemoved(){
            super.onProximityRemoved();

            signal.removeConnected(this);
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.enabled) return signal.active ? 1 : 0;
            return super.sense(sensor);
        }

        @Override
        public void draw(){
            drawBase();
            drawConnections();
        }

        public void drawConnections(){
            for(int i = 0; i < 4; i++){
                Building build = nearby(i);
                if(build instanceof BinaryBuild b && signal.all.contains(b)) Draw.rect(connectionRegion, x, y, 90f * i);
            }
        }
    }
}