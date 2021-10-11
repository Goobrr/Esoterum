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
        transmits = true;
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
        public void updateSignal(int depth){
            if(depth < depthLimit){
                if(nb.get(0) != null && connectionCheck(nb.get(1), this))
                    nb.get(0).updateSignal(depth + 1);
                if(nb.get(1) != null && connectionCheck(nb.get(1), this))
                    nb.get(1).updateSignal(depth + 1);
                if(nb.get(2) != null && connectionCheck(nb.get(2), this))
                    nb.get(2).updateSignal(depth + 1);
                if(nb.get(3) != null && connectionCheck(nb.get(3), this))
                    nb.get(3).updateSignal(depth + 1);
            }
            signal[0] = getSignal(nb.get(2), this);
            signal[1] = getSignal(nb.get(3), this);
            signal[2] = getSignal(nb.get(0), this);
            signal[3] = getSignal(nb.get(1), this);
        }

        @Override
        public void drawConnections(){
            Draw.color(Color.white, Pal.accent, signal[0] || signal[2] ? 1f : 0f);
            Draw.rect(directionRegions[0], x, y);
            Draw.color(Color.white, Pal.accent, signal[1] || signal[3] ? 1f : 0f);
            Draw.rect(directionRegions[1], x, y);
        }
    }
}
