package esoterum.world.blocks.binary.transmission;

import arc.*;

public class BinaryWire extends BinaryRouter{
    public BinaryWire(String name){
        super(name);
    }

    @Override
    public void load(){
        super.load();
        connectionRegion = Core.atlas.find("esoterum-connection-large");
    }

    public class BinaryWireBuild extends BinaryRouterBuild{
        @Override
        public void draw(){
            drawBase();
            drawConnections();
        }
    }
}