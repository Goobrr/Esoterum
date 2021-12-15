package esoterum.world.blocks.binary.transmission;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.io.*;
import esoterum.world.blocks.binary.basis.*;

public class BinaryFlipflop extends BinarySink{
    public TextureRegion flipRegion, flopRegion;

    public BinaryFlipflop(String name){
        super(name);
        rotate = true;
        rotatedBase = true;
        drawArrow = true;
        baseHighlight = "gold";
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

    public class FlipflopBuild extends BinarySinkBuild{
        public boolean canFlip, active;

        @Override
        public void updateTile(){
            boolean a = false;
            for(int i = 1; i < 4; i++){
                WireGraph g = connections.get(i);
                if(g != null && g.active) a = true;
            }
            if(a && canFlip){
                active = !active;
                canFlip = false;
            }else{
                canFlip = !a;
            }
        }

        @Override
        public boolean isActive(WireGraph graph){
            return graph == connections.first() && active;
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
        public void write(Writes write) {
            super.write(write);

            write.bool(canFlip);
            write.bool(active);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if(revision >= 3){
                canFlip = read.bool();
                active = read.bool();
            }
        }

        @Override
        public byte version() {
            return 3;
        }
    }
}