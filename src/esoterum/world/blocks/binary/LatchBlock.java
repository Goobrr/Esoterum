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
        public void updateSignal(int source) {
            try {
                super.updateSignal(source);
                if(getSignal(nb.get(2), this)){
                    signal[4] = getSignal(nb.get(1), this) | getSignal(nb.get(3), this);
                }
                if(signal[0] != signal[4]){
                    configure(signal[4]);
                    propagateSignal(true, false, false, false);
                }
            } catch(StackOverflowError e){}
        }

        @Override
        public void draw() {
            if(!rotate || !rotatedBase){
                Draw.rect(region, x, y);
            } else {
                Draw.rect(baseRegions[rotation], x, y);
            }

            drawConnections();
            Draw.color(Color.white, Pal.accent, getSignal(nb.get(1), this) | getSignal(nb.get(2), this) | getSignal(nb.get(3), this) ? 1f : 0f);
            Draw.rect(topRegion, x, y, (rotate && drawRot) ? rotdeg() : 0f);

            Draw.color(signal[0] ? Pal.accent : Color.white);
            Draw.rect(latchRegion, x, y);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, (byte)(revision + 1));

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
