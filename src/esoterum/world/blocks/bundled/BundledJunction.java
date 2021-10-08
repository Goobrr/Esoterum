package esoterum.world.blocks.bundled;

import arc.*;
import arc.graphics.g2d.*;
import esoterum.util.*;
import esoterum.world.blocks.binary.*;

// too similar to BinaryRouter?
public class BundledJunction extends BinaryJunction{
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
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{
            region,
            topRegion,
        };
    }

    public class BundledJunctionBuild extends BinaryJunctionBuild{
        @Override
        public void drawConnections(){
            return;
        }
        @Override
        public int getSignalRelativeTo(BinaryBlock.BinaryBuild from, BinaryBlock.BinaryBuild to){
            if(!from.emits()) return 0;
            if(from instanceof ColorWire.ColorWireBuild b)
                return switch(EsoUtil.relativeDirection(b, to)){
                    case 0 -> (from.signalFront()<<b.channel);
                    case 1 -> (from.signalLeft()<<b.channel);
                    case 2 -> (from.signalBack()<<b.channel);
                    case 3 -> (from.signalRight()<<b.channel);
                    default -> 0;
                };
            if(from instanceof BundledWire.BundledWireBuild b)
                return switch(EsoUtil.relativeDirection(b, to)) {
                    case 0 -> b.signalFront(); //front
                    case 1 -> b.signalLeft(); //left
                    case 2 -> b.signalBack(); //back
                    case 3 -> b.signalRight(); //right
                    default -> 0;
                };
            if(from instanceof BundledJunction.BundledJunctionBuild b)
                return switch(EsoUtil.relativeDirection(b, to)) {
                    case 0 -> b.signalFront(); //front
                    case 1 -> b.signalLeft(); //left
                    case 2 -> b.signalBack(); //back
                    case 3 -> b.signalRight(); //right
                    default -> 0;
                };
            return 0;
        }
    }
}