package esoterum.world.blocks.binary;

import arc.graphics.g2d.*;

public class ConfigWireO extends ConfigWireI{
    public ConfigWireO(String name){
        super(name);
        letters[1] = "F";
        inputs = new boolean[]{false, false, true, false};
        outputs = new boolean[]{true, true, false, true};
    }

    public class ConfigWireOBuild extends ConfigWireIBuild{
        @Override
        public void drawConnections(){
            for(int i = 0; i < 3; i++){
                if(!getBool(sides[i]))continue;
                int ii = i == 1 ? 3 : i;
                Draw.rect(connectionRegion, x, y, (90f + 90f * ii) + rotdeg());
            }
            Draw.rect(connectionRegion, x, y, rotdeg() + 180f);
        }

        @Override
        public boolean signal(){
            return getSignal(nb.get(2), this);
        }

        @Override
        public boolean signalLeft(){
            return signal() && getBool(sides[0]);
        }

        @Override
        public boolean signalFront(){
            return signal() && getBool(sides[1]);
        }

        @Override
        public boolean signalRight(){
            return signal() && getBool(sides[2]);
        }
    }
}