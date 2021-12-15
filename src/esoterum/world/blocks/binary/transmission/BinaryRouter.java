package esoterum.world.blocks.binary.transmission;

import arc.*;
import arc.graphics.g2d.*;
import esoterum.world.blocks.binary.basis.*;
import mindustry.gen.*;
import mindustry.logic.*;

public class BinaryRouter extends BinaryBlock{
    public BinaryRouter(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        connectionRegion = Core.atlas.find("esoterum-connection-large");
    }

    public class BinaryRouterBuild extends BinaryBuild{
        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.enabled) return signal.active ? 1 : 0;
            return super.sense(sensor);
        }

        @Override
        public void draw(){
            drawBase();
            drawConnections();
            drawTop();
        }

        public void drawConnections(){
            for(int i = 0; i < 4; i++){
                Building build = nearby(i);
                if(build instanceof BinaryBuild b && signal.all.contains(b)) Draw.rect(connectionRegion, x, y, 90f * i);
            }
        }
    }
}