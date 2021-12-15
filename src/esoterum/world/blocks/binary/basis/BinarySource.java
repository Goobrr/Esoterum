package esoterum.world.blocks.binary.basis;

import arc.graphics.g2d.*;
import mindustry.gen.*;

public class BinarySource extends BinaryBlock{
    public BinarySource(String name){
        super(name);
    }

    public class BinarySourceBuild extends BinaryBuild{
        /** Weather or not this source is seen as active to graphs. */
        public boolean isActive(WireGraph graph){
            return false;
        }

        public void drawConnections(){
            for(int i = 0; i < 4; i++){
                Building build = nearby(i);
                if(build instanceof BinaryBuild b) Draw.rect(connectionRegion, x, y, 90f * i);
            }
        }
    }
}