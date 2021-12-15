package esoterum.world.blocks.binary.transmission;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import esoterum.world.blocks.binary.basis.*;

import java.util.*;

public class BinaryJunction extends BinarySink{
    public TextureRegion[][] directionRegions = new TextureRegion[2][4];

    public BinaryJunction(String name){
        super(name);
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

    public class BinaryJunctionBuild extends BinarySinkBuild{
        public boolean[] signal = new boolean[4];
        public int variant = 0;

        @Override
        public void created(){
            super.created();
            updateVariants();
        }

        @Override
        public boolean isActive(WireGraph graph){
            Arrays.fill(signal, false);
            for(int i = 0; i < 4; i++){
                WireGraph g = connections.get(i);
                if(g != null) signal[i] = g.active;
            }
            for(int i = 0; i < 4; i++){
                if(graph == connections.get(i)) return signal[Mathf.mod(i + 2, 4)];
            }
            return false;
        }

        @Override
        public void drawConnections(){
            Draw.color(Color.white, team.color, Mathf.num(signal[0] || signal[2]));
            Draw.rect(directionRegions[0][variant], x, y);
            Draw.color(Color.white, team.color, Mathf.num(signal[1] || signal[3]));
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