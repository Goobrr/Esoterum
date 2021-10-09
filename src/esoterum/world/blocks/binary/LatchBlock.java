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
        drawArrow = true;
        
        config(Integer.class, (LatchBuild l, Integer b) -> {
            l.store = b;
        });

        config(Boolean.class, (LatchBuild l, Boolean b) -> {
            l.store = b ? 1 : 0;
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
        public int store;
        @Override
        public void updateTile() {
            super.updateTile();
            lastSignal = signal();
            if(getSignal(nb.get(2), this) != 0){
                configure(getSignal(nb.get(1), this) | getSignal(nb.get(3), this));
            }
        }

        @Override
        public int signal() {
            return getSignal(nb.get(1), this) | getSignal(nb.get(2), this) | getSignal(nb.get(3), this);
        }

        @Override
        public void draw() {
            super.draw();

            Draw.color(store != 0 ? Pal.accent : Color.white);
            Draw.rect(latchRegion, x, y);
        }

        @Override
        public int signalFront() {
            return store;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, (byte)(revision + 1));
            if(revision >= 3){
                store = read.i();
            } else if(revision >= 2){
                store = read.bool() ? 1: 0;
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(store);
        }

        @Override
        public byte version() {
            return 3;
        }
    }
}
