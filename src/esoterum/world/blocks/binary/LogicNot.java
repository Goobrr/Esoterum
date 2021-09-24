package esoterum.world.blocks.binary;

import arc.*;

public class LogicNot extends BinaryBlock{
    public LogicNot(String name){
        super(name);
        inputs = new boolean[]{false, false, true, false};
        outputs = new boolean[]{true, false, false, false};
        emits = true;
        rotate = true;
        drawArrow = true;
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find("esoterum-gate-base");
    }

    public class LogicNotBuild extends BinaryBuild{
        @Override
        public void updateTile(){
            super.updateTile();
            lastSignal = signal();
        }

        @Override
        public boolean signal(){
            return !getSignal(nb.get(2), this);
        }

        @Override
        public boolean signalFront(){
            return signal();
        }
    }
}
