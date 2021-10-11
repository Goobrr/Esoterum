package esoterum.world.blocks.binary;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import mindustry.graphics.Pal;
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
        transmits = false;

    }

    public class TogglerBuild extends BinaryBuild {
        @Override
        public void updateTile() {
            super.updateTile();
            signal[0] = getSignal(nb.get(1), this) | getSignal(nb.get(2), this) | getSignal(nb.get(3), this);

            if(front() != null){
                front().control(LAccess.enabled, signal() ? 1d : 0d, 0d, 0d, 0d);
            }
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
