package esoterum.world.blocks.binary;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import esoterum.content.*;
import mindustry.entities.units.*;
import mindustry.graphics.*;
import mindustry.world.*;

public class BinaryWire extends BinaryBlock{
    public Block junctionReplacement;

    public BinaryWire(String name){
        super(name);
        outputs = new boolean[]{true, false, false, false};
        inputs = new boolean[]{false, true, true, true};
        emits = true;
        rotate = true;
        drawArrow = true;

        drawConnectionArrows = true;
    }

    @Override
    public void load(){
        super.load();
        connectionRegion = Core.atlas.find("esoterum-connection-large");
        topRegion = Core.atlas.find("esoterum-wire-top");
    }

    @Override
    public void init(){
        super.init();

        if(junctionReplacement == null) junctionReplacement = EsoBlocks.esoJunction;
    }

    @Override
    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        return (other != this || rotate) && other instanceof BinaryBlock && size == other.size;
    }

    @Override
    public Block getReplacement(BuildPlan req, Seq<BuildPlan> requests){
        if(junctionReplacement == null) return this;

        Boolf<Point2> cont = p -> requests.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && (req.block instanceof BinaryWire || req.block instanceof BinaryJunction));
        return cont.get(Geometry.d4(req.rotation)) &&
            cont.get(Geometry.d4(req.rotation - 2)) &&
            req.tile() != null &&
            req.tile().block() instanceof BinaryWire &&
            Mathf.mod(req.tile().build.rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }

    public class BinaryWireBuild extends BinaryBuild{
        @Override
        public void updateTile(){
            super.updateTile();
            lastSignal = nextSignal | getSignal(nb.get(2), this);
            nextSignal = signal();
        }

        @Override
        public boolean signal() {
            return getSignal(nb.get(1), this) | getSignal(nb.get(3), this);
        }

        public boolean signalFront(){
            boolean s = signal();
            return (nb.get(2) != null ?
                nb.get(2).rotation == rotation || !nb.get(2).block.rotate ?
                    getSignal(nb.get(2), this) :
                    s
                : s) | s;
        }
    }
}
