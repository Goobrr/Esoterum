package esoterum.world.blocks.binary.transmission;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import esoterum.world.blocks.binary.basis.*;

import java.util.*;

public class BinaryCJunction extends BinarySink{
    public TextureRegion[][] directionRegions = new TextureRegion[2][2];

    public BinaryCJunction(String name){
        super(name);
        rotate = true;
    }

    @Override
    public void load(){
        super.load();
        
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 2; j++)
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

    public class BinaryCJunctionBuild extends BinarySinkBuild{
        public boolean[] signal = new boolean[4];
        public int variant = 0;

        @Override
        public void created(){
            super.created();
            updateVariants();
        }

        @Override
        public boolean isActive(SignalGraph graph){
            Arrays.fill(signal, false);
            for(int i = 0; i < 4; i++){
                SignalGraph g = connections.get(i);
                if(g != null) signal[i] = g.active;
            }
            for(int i = 0; i < 4; i++){
                if(graph == connections.get(i)){
                    return switch(i){
                        case 0 -> signal[1];
                        case 1 -> signal[0];
                        case 2 -> signal[3];
                        case 3 -> signal[2];
                        default -> false;
                    };
                }
            }
            return false;
        }

        @Override
        public void drawConnections(){
            Draw.color(Color.white, team.color, Mathf.num(signal[2] || signal[3]));
            Draw.rect(directionRegions[0][variant], x, y, rotdeg());
            Draw.color(Color.white, team.color, Mathf.num(signal[0] || signal[1]));
            Draw.rect(directionRegions[1][variant], x, y, rotdeg());
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