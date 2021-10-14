package esoterum.world.blocks.binary;

import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.graphics.*;
import mindustry.logic.LAccess;

public class Toggler extends BinaryBlock{
    public Toggler(String name){
        super(name);
        rotate = true;
        rotatedBase = true;
        baseType = 1;
        emits = true;
        outputs = new boolean[]{true, false, false, false};
        inputs = new boolean[]{false, true, true, true};
    }

    public class TogglerBuild extends BinaryBuild {
        @Override
        public void updateSignal(int source) {
            try {
                super.updateSignal(source);
                signal[4] = getSignal(nb.get(1), this) | getSignal(nb.get(2), this) | getSignal(nb.get(3), this);
                if(signal[0] != signal[4]){
                    signal[0] = signal[4];
                    propagateSignal(true, false, false, false);
                }
                if(front() != null){
                    front().control(LAccess.enabled, signal() ? 1d : 0d, 0d, 0d, 0d);
                }
            } catch(Exception e){}
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            if(front() != null){
                Draw.color(signal() ? Pal.accent : Color.white);
                Lines.square(front().x, front().y, 4 * front().block.size);
            }
        }
    }
}
