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

public class BinaryDiode extends BinaryBlock {
    public Block junctionReplacement;

    public BinaryDiode(String name){
        super(name);
        outputs = new boolean[]{true, false, false, false};
        inputs = new boolean[]{false, true, true, true};
        emits = true;
        rotate = true;
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

        Boolf<Point2> cont = p -> requests.contains(o -> o.x == req.x + p.x && o.y == req.y + p.y && (req.block instanceof BinaryDiode || req.block instanceof BinaryJunction));
        return cont.get(Geometry.d4(req.rotation)) &&
            cont.get(Geometry.d4(req.rotation - 2)) &&
            req.tile() != null &&
            req.tile().block() instanceof BinaryDiode &&
            Mathf.mod(req.tile().build.rotation - req.rotation, 2) == 1 ? junctionReplacement : this;
    }

    public class BinaryDiodeBuild extends BinaryBuild {
        @Override
        public int inV(int dir){
            return dir;
        }

        @Override
        public boolean updateSignal(){
            signal[5] = signal[0];
            signal[0] = getSignal(relnb[1], this) | getSignal(relnb[2], this) | getSignal(relnb[3], this);
            SignalGraph.graph.setVertexAugmentation(v[0], signal()?1:0);
            return signal[5] != signal[0];
        }

        @Override
        public void drawConnections(){
            for(int i = 1; i < 4; i++){
                if(connections[i]){
                    Draw.color(Color.white, team.color, Mathf.num(getSignal(relnb[i], this)));
                    Draw.rect(connectionRegion, x, y, rotdeg() + 90 * i);
                }
            }
        }
    }
}