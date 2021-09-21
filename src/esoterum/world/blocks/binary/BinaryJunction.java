package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.graphics.*;

// too similar to BinaryRouter?
public class BinaryJunction extends BinaryBlock{
    public TextureRegion[] directionRegions = new TextureRegion[2];

    public BinaryJunction(String name){
        super(name);
        emits = true;

        inputs = new boolean[]{true, true, true, true};
        outputs = new boolean[]{true, true, true, true};
    }

    @Override
    public void load(){
        super.load();

        for(int i = 0; i < 2; i++){
            directionRegions[i] = Core.atlas.find(name + "-direction-" + i);
        }
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{
            region,
            topRegion,
            directionRegions[0],
            directionRegions[1]
        };
    }

    public class BinaryJunctionBuild extends BinaryBuild{
        @Override
        public void updateTile(){
            super.updateTile();
            lastSignal = false;
            for(BinaryBuild b : nb){
                lastSignal |= getSignal(b, this);
            };
        }

        @Override
        public void drawConnections(){

            Draw.color(Color.white, Pal.accent, signalFront() || signalBack() ? 1f : 0f);
            Draw.rect(directionRegions[0], x, y);
            Draw.color(Color.white, Pal.accent, signalLeft() || signalRight() ? 1f : 0f);
            Draw.rect(directionRegions[1], x, y);
            Draw.color(Color.white, Pal.accent, lastSignal ? 1f : 0f);
        }

        @Override
        public boolean signalFront(){
            return getSignal(nb.get(2), this);
        }

        @Override
        public boolean signalBack(){
            return getSignal(nb.get(0), this);
        }

        @Override
        public boolean signalLeft(){
            return getSignal(nb.get(3), this);
        }

        @Override
        public boolean signalRight(){
            return getSignal(nb.get(1), this);
        }
    }
}
