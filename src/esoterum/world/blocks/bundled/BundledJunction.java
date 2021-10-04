package esoterum.world.blocks.bundled;

import arc.*;
import arc.graphics.g2d.*;

// too similar to BinaryRouter?
public class BundledJunction extends BundledBlock{
    public TextureRegion[] directionRegions = new TextureRegion[2];

    public BundledJunction(String name){
        super(name);
        emits = true;

        inputs = new boolean[]{true, true, true, true};
        outputs = new boolean[]{true, true, true, true};
    }

    @Override
    public void load(){
        super.load();
        topRegion = Core.atlas.find("esoterum-bundled-junction-top");
        for(int i = 0; i < 2; i++){
            directionRegions[i] = Core.atlas.find("bundled-junction-direction-" + i);
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

    public class BundledJunctionBuild extends BundledBuild{
        @Override
        public void updateTile(){
            super.updateTile();
            lastSignal = 0;
            for(BundledBuild b : nb){
                lastSignal |= getSignal(b, this);
            };
        }

        @Override
        public void drawConnections(){
            Draw.rect(directionRegions[0], x, y);
            Draw.rect(directionRegions[1], x, y);
        }

        @Override
        public short signalFront(){
            return getSignal(nb.get(2), this);
        }

        @Override
        public short signalBack(){
            return getSignal(nb.get(0), this);
        }

        @Override
        public short signalLeft(){
            return getSignal(nb.get(3), this);
        }

        @Override
        public short signalRight(){
            return getSignal(nb.get(1), this);
        }
    }
}
