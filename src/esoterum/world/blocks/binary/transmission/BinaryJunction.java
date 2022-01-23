package esoterum.world.blocks.binary.transmission;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import esoterum.world.blocks.binary.*;

// too similar to BinaryRouter?
public class BinaryJunction extends BinaryBlock{
    public TextureRegion[][] directionRegions = new TextureRegion[2][4];

    public BinaryJunction(String name){
        super(name);
        emits = true;
        inputs = new boolean[]{true, true, true, true};
        outputs = new boolean[]{true, true, true, true};
        undirected = true;
    }

    @Override
    public void load(){
        super.load();

        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 4; j++)
            directionRegions[i][j] = Core.atlas.find(name + "-direction-" + i + "-" + j);
        }
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{
            baseRegion,
            topRegion,
            directionRegions[0][0],
            directionRegions[1][0]
        };
    }

    public class BinaryJunctionBuild extends BinaryBuild{
        public int variant = 0;

        @Override
        public int inV(int dir){
            return dir % 2;
        }

        @Override
        public void created(){
            super.created();
            updateVariants();
        }

        @Override
        public boolean updateSignal(){
            signal[0] = getSignal(relnb[2], this);
            signal[1] = getSignal(relnb[3], this);
            signal[2] = getSignal(relnb[0], this);
            signal[3] = getSignal(relnb[1], this);
            return true;
        }

        @Override
        public void updateTile(){
            return;
        }

        @Override
        public void drawConnections(){
            updateSignal();
            Draw.color(Color.white, team.color, Mathf.num((int)SignalGraph.graph.getComponentAugmentation(v[0]) > 0));
            Draw.rect(directionRegions[0][variant], x, y);
            Draw.color(Color.white, team.color, Mathf.num((int)SignalGraph.graph.getComponentAugmentation(v[1]) > 0));
            Draw.rect(directionRegions[1][variant], x, y);
        }

        public void updateVariants(){
            if(Core.settings.getBool("eso-junction-variation")){
                variant = Mathf.randomSeed(tile.pos(), 0, directionRegions[0].length - 1);
            }else{
                variant = 0;
            }
        }
    }
}