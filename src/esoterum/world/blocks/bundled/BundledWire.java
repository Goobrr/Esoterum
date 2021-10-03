package esoterum.world.blocks.bundled;

import arc.*;
import arc.func.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.Log;
import esoterum.content.*;
import mindustry.entities.units.*;
import mindustry.world.*;

public class BundledWire extends BundledBlock{
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

        if(junctionReplacement == null) junctionReplacement = EsoBlocks.bundledJunction;
    }

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        return (other != this || rotate) && other instanceof BundledBlock && size == other.size;
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

    public class BundledWireBuild extends BundledBuild{
        @Override
        public void updateTile(){
            super.updateTile();
            lastSignal = (short) (nextSignal | getSignal(nb.get(2), this));
            nextSignal = signal();
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
        public short signal() {
            return (short)((getSignal(nb.get(1), this) | getSignal(nb.get(3), this)) | (getSignal(nbb.get(1), this) | getSignal(nbb.get(3), this)));
        }

        public short signalFront(){
            return (short) (((nb.get(2) != null ?
                nb.get(2).rotation == rotation || !nb.get(2).block.rotate || nb.get(2).allOutputs() ?
                    getSignal(nb.get(2), this) :
                    nextSignal
                : nextSignal )

                | nextSignal) |
                ((nbb.get(2) != null ?
                nbb.get(2).rotation == rotation || !nbb.get(2).block.rotate || nbb.get(2).allOutputs() ?
                    getSignal(nbb.get(2), this) :
                    nextSignal
                : nextSignal )

                | nextSignal));
        }
        public short signalLeft(){
            return (short) (((nb.get(2) != null ?
                nb.get(2).rotation == rotation || !nb.get(2).block.rotate || nb.get(2).allOutputs() ?
                    getSignal(nb.get(2), this) :
                    nextSignal
                : nextSignal )

                | nextSignal) |
                ((nbb.get(2) != null ?
                nbb.get(2).rotation == rotation || !nbb.get(2).block.rotate || nbb.get(2).allOutputs() ?
                    getSignal(nbb.get(2), this) :
                    nextSignal
                : nextSignal )

                | nextSignal));
        }
        public short signalRight(){
            return (short) (((nb.get(2) != null ?
                nb.get(2).rotation == rotation || !nb.get(2).block.rotate || nb.get(2).allOutputs() ?
                    getSignal(nb.get(2), this) :
                    nextSignal
                : nextSignal )

                | nextSignal) |
                ((nbb.get(2) != null ?
                nbb.get(2).rotation == rotation || !nbb.get(2).block.rotate || nbb.get(2).allOutputs() ?
                    getSignal(nbb.get(2), this) :
                    nextSignal
                : nextSignal )

                | nextSignal));
        }
    }
}
