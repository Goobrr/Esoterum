package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.graphics.*;

public class LatchBlock extends BinaryBlock{
    public TextureRegion latchRegion;

    public LatchBlock(String name){
        super(name);
        outputs = new boolean[]{true, false, false, false};
        inputs = new boolean[]{false, true, true, true};
        emits = true;
        rotate = true;
        rotatedBase = true;
        drawArrow = true;

        baseType = 1;
        
        config(Boolean.class, (LatchBuild l, Boolean b) -> {
            l.signal[0] = b;
        });
    }

    @Override
    public void load() {
        super.load();
        latchRegion = Core.atlas.find(name + "-latch");
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{
            region,
            topRegion,
            latchRegion
        };
    }

    public class LatchBuild extends BinaryBuild {
        @Override
        public void updateSignal(int depth) {
            if(depth < depthLimit){
                if(nb.get(1) != null && connectionCheck(nb.get(1), this))
                    nb.get(1).updateSignal(depth + 1);
                if(nb.get(2) != null && connectionCheck(nb.get(2), this))
                    nb.get(2).updateSignal(depth + 1);
                if(nb.get(3) != null && connectionCheck(nb.get(3), this))
                    nb.get(3).updateSignal(depth + 1);
            }
            if(getSignal(nb.get(2), this)){
                configure(getSignal(nb.get(1), this) | getSignal(nb.get(3), this));
            }
        }

        @Override
        public void draw() {
            super.draw();

            Draw.color(signal[0] ? Pal.accent : Color.white);
            Draw.rect(latchRegion, x, y);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if(revision >= 2){
                signal[0] = read.bool();
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.bool(signal[0]);
        }

        @Override
        public byte version() {
            return 2;
        }
    }
}
