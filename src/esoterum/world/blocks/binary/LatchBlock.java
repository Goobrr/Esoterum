package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
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
        
        config(Boolean.class, (LatchBuild l, Boolean b) -> {
            l.store = b;
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
        public boolean store;
        @Override
        public void updateTile() {
            super.updateTile();
            lastSignal = signal();
            if(getSignal(nb.get(2), this)){
                configure(getSignal(nb.get(1), this) | getSignal(nb.get(3), this));
            }
        }

        @Override
        public boolean signal() {
            return getSignal(nb.get(1), this) | getSignal(nb.get(2), this) | getSignal(nb.get(3), this);
        }

        @Override
        public void draw() {
            super.draw();

            Draw.color(store ? Pal.accent : Color.white);
            Draw.rect(latchRegion, x, y);
        }

        @Override
        public boolean signalFront() {
            return store;
        }
    }
}
