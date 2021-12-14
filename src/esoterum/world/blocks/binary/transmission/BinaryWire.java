package esoterum.world.blocks.binary.transmission;

import esoterum.world.blocks.binary.basis.*;
import mindustry.logic.*;

public class BinaryWire extends BinaryBlock{
    public BinaryWire(String name){
        super(name);
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
    }
}