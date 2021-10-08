package esoterum.world.blocks.bundled;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import esoterum.content.*;
import esoterum.util.EsoUtil;
import esoterum.world.blocks.binary.*;
import mindustry.entities.units.*;
import mindustry.world.*;

public class BundledWire extends BinaryWire{
    public Block junctionReplacement;

    public BundledWire(String name){
        super(name);
        outputs = new boolean[]{true, true, false, true};
        inputs = new boolean[]{false, true, true, true};
        emits = true;
        rotate = true;
        drawArrow = true;

        drawConnectionArrows = true;
    }

    @Override
    public void load(){
        super.load();
        connectionRegion = Core.atlas.find("esoterum-bundled-connection");
        topRegion = Core.atlas.find("esoterum-bundled-top");
    }

    @Override
    public void init(){
        super.init();

        if(junctionReplacement == null) junctionReplacement = EsoBlocks.esoBundledJunction;
    }

    @Override
    public Block getReplacement(BuildPlan req, Seq<BuildPlan> requests){
        if(junctionReplacement == null) return this;

        Boolf<Point2> cont = p -> requests.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && (req.block instanceof BundledWire || req.block instanceof BundledJunction));
        return cont.get(Geometry.d4(req.rotation)) &&
            cont.get(Geometry.d4(req.rotation - 2)) &&
            req.tile() != null &&
            req.tile().block() instanceof BundledWire &&
            Mathf.mod(req.tile().build.rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }

    public class BundledWireBuild extends BinaryWireBuild{
        @Override
        public void updateTile(){
            super.updateTile();
            lastSignal = (short) (nextSignal | getSignal(nb.get(2), this));
            nextSignal = signal();
        }

        @Override
        public void draw(){
            super.draw();

            drawConnections();
            Draw.rect(topRegion, x, y, (rotate && drawRot) ? rotdeg() : 0f);
        }

        @Override
        public void drawConnections(){
            for(int i = 1; i < 4; i++){
                if(connections[i]){
                    Draw.rect(connectionRegion, x, y, rotdeg() + 90 * i);
                }
            }
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

        @Override
        public int signal() {
            return (getSignal(nb.get(1), this) | getSignal(nb.get(3), this));
        }

        public int signalFront(){
            return (nb.get(2) != null ?
                nb.get(2).rotation == rotation || !nb.get(2).block.rotate || nb.get(2).allOutputs() ?
                    getSignal(nb.get(2), this) :
                    nextSignal
                : nextSignal )

                | nextSignal;
        }
        public int signalLeft(){
            return (nb.get(2) != null ?
                nb.get(2).rotation == rotation || !nb.get(2).block.rotate || nb.get(2).allOutputs() ?
                    getSignal(nb.get(2), this) :
                    nextSignal
                : nextSignal )

                | nextSignal;
        }
        public int signalRight(){
            return (nb.get(2) != null ?
                nb.get(2).rotation == rotation || !nb.get(2).block.rotate || nb.get(2).allOutputs() ?
                    getSignal(nb.get(2), this) :
                    nextSignal
                : nextSignal )

                | nextSignal;
        }
    }
}