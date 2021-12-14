package esoterum.world.blocks.binary.transmission;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.io.*;
import esoterum.world.blocks.binary.basis.*;

public class BinaryFlipflop extends BinaryBlock{
    public TextureRegion flipRegion, flopRegion;

    public BinaryFlipflop(String name){
        super(name);
        outputs = new boolean[]{true, false, false, false};
        inputs = new boolean[]{false, true, true, true};
        emits = true;
        rotate = true;
        rotatedBase = true;
        drawArrow = true;
        baseHighlight = "gold";
        propagates = true;
    }

    @Override
    public void load() {
        super.load();
        flipRegion = Core.atlas.find(name + "-flip");
        flopRegion = Core.atlas.find(name + "-flop");
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{
            baseRegion,
            topRegion,
            flipRegion
        };
    }

    public class FlipflopBuild extends BinaryBuild {
        @Override
        public boolean updateSignal() {
            signal[5] = signal[0];
            if(!signal[4] && (getSignal(relnb[1], this) || getSignal(relnb[2], this) || getSignal(relnb[3], this)))
                signal[0] = !signal[0];
            signal[4] = (getSignal(relnb[1], this) || getSignal(relnb[2], this) || getSignal(relnb[3], this));
            return signal[5] != signal[0];
        }

        @Override
        public void draw() {
            drawBase();
            drawConnections();
            Draw.color(Color.white, team.color, Mathf.num(getSignal(relnb[1], this) | getSignal(relnb[2], this) | getSignal(relnb[3], this)));
            Draw.rect(topRegion, x, y, (rotate && drawRot) ? rotdeg() : 0f);

            Draw.color(signal[0] ? team.color : Color.white);
            Draw.rect(signal[0] ? flipRegion : flopRegion, x, y);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, (byte)(revision + 1));

            if(revision >= 2){
                signal[0] = read.bool();
            }
            if(revision >= 3){
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