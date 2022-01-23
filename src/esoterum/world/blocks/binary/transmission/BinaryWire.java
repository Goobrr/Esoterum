package esoterum.world.blocks.binary.transmission;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import esoterum.content.*;
import esoterum.world.blocks.binary.*;
import mindustry.entities.units.*;
import mindustry.world.*;

public class BinaryWire extends BinaryBlock {
    public Block junctionReplacement;

    public BinaryWire(String name){
        super(name);
        outputs = new boolean[]{true, true, true, true};
        inputs = new boolean[]{true, true, true, true};
        emits = true;
        rotate = false;
        drawArrow = true;
        undirected = true;

        drawConnectionArrows = true;
    }

    @Override
    public void load(){
        super.load();
        connectionRegion = Core.atlas.find("esoterum-connection-large");
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

    public class BinaryWireBuild extends BinaryBuild {
        @Override
        public boolean signal(){
            return signal[0] = signal[1] = signal[2] = signal[3] = (int)SignalGraph.graph.getComponentAugmentation(v[0]) > 0;
        }

        @Override
        public void drawConnections(){
            for(int i = 0; i < 4; i++){
                if(relnb[i] != null){
                    Draw.color(Color.white, team.color, Mathf.num(getSignal(relnb[i], this) || getSignal(this, relnb[i])));
                    Draw.rect(connectionRegion, x, y, rotdeg() + 90 * i);
                }
            }
        }
    }
}