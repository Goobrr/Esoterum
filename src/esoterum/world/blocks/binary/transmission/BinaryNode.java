package esoterum.world.blocks.binary.transmission;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.io.*;
import esoterum.world.blocks.binary.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class BinaryNode extends BinaryBlock{
    public int range;

    public TextureRegion centerRegion;

    public BinaryNode(String name, int linkRange){
        super(name);
        rotate = false;
        emits = true;
        range = linkRange;
        configurable = true;

        outputs = new boolean[]{true, true, true, true};
        inputs = new boolean[]{true, true, true, true};
        undirected = true;

        //point2 config is relative
        config(Point2.class, (BinaryNodeBuild tile, Point2 i) -> {
            tile.link = Point2.pack(i.x + tile.tileX(), i.y + tile.tileY());
            tile.updateProximity();
            if(tile.linkedNode() != null) tile.linkedNode().updateNeighbours();
            if(tile.linkedNode() != null) tile.linkedNode().updateConnections();
        });
        //integer is not
        config(Integer.class, (BinaryNodeBuild tile, Integer i) -> {
            tile.link = i;
            tile.updateProximity();
            if(tile.linkedNode() != null) tile.linkedNode().updateNeighbours();
            if(tile.linkedNode() != null) tile.linkedNode().updateConnections();
        });

        configClear((BinaryNodeBuild tile) -> tile.link = -1);
    }

    @Override
    public void load(){
        super.load();

        centerRegion = Core.atlas.find(name + "-center");
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{
            baseRegion,
            rotate && rotatedBase ? highlightRegions[0] : highlightRegion,
            topRegion, centerRegion
        };
    }

    @Override
    public void init(){
        super.init();

        clipSize = Math.max(clipSize, (range * tilesize + 8f) * 2f);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Drawf.circles(x * tilesize + offset, y * tilesize + offset, range * tilesize, Color.white);
        Draw.reset();
    }

    public boolean linkValid(Tile tile, Tile other){
        if(other == null || tile == null || other.dst(tile) > range * tilesize) return false;

        return other.block() == tile.block() && tile.team() == other.team();
    }

    public BinaryNodeBuild getLink(int pos){
        return world.build(pos) instanceof BinaryNodeBuild b ? b : null;
    }

    public class BinaryNodeBuild extends BinaryBuild{
        public int link = -1;

        @Override
        public void updateTile(){
            super.updateTile();
            BinaryNodeBuild c = linkedNode();
            if(c != null && c.link != pos()){
                configure(null);
            }
        }
        
        @Override
        public BinaryBuild[] getInputs(){
            BinaryBuild[] i = new BinaryBuild[relnb.length + 1];
            int c = 0;
            for(BinaryBuild b : relnb)
                if (b != null && inputs(c) && connections[c]) i[c] = b;
            i[relnb.length] = linkedNode();
            return i;
        }

        @Override
        public BinaryBuild[] getOutputs(){
            BinaryBuild[] o = new BinaryBuild[relnb.length + 1];
            int c = 0;
            for(BinaryBuild b : relnb){
                if (b != null && outputs(c) && connections[c]) o[c] = b;
                c++;
            }
            o[relnb.length] = linkedNode();
            return o;
        }

        @Override
        public void placed(){
            super.placed();

            BinaryNodeBuild c = linkedNode();
            if(c != null && c.link != pos()){
                c.disconnect();
                c.configure(pos());
            }
        }

        @Override
        public void draw(){
            drawBase();
            drawConnections();
            Draw.color(Color.white, team.color, Mathf.num(signal[0]));
            Draw.rect(topRegion, x, y, (rotate && drawRot) ? rotdeg() : 0f);

            Draw.z(Layer.power);
            Lines.stroke(1f, signal() || signal[0] ? team.color : Color.white);
            Draw.rect(centerRegion, x, y, (rotate && drawRot) ? rotdeg() : 0f);
            BinaryNodeBuild c = linkedNode();
            if(c != null){
                Lines.line(
                    x, y,
                    c.x, c.y,
                    false
                );
                Draw.reset();
            }
        }

        @Override
        public boolean signal(){
            return signal[0] = signal[1] = signal[2] = signal[3] = (int)SignalGraph.graph.getComponentAugmentation(v[0]) > 0;
        }

        @Override
        public void drawConfigure(){
            Tmp.c1.set(Color.white).lerp(team.color, Mathf.num(signal()));

            Drawf.circles(x, y, size * tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f), Tmp.c1);
            Drawf.circles(x, y, range * tilesize, Tmp.c1);

            BinaryNodeBuild connection = linkedNode();
            if(link != -1 && connection != null){
                Drawf.square(connection.x, connection.y, connection.block.size * tilesize / 2f + 1f, Tmp.c1);
            }
        }

        @Override
        public boolean onConfigureTileTapped(Building other){
            if(linkValid(tile, other.tile)){
                disconnect();
                if(other.pos() == link){
                    configure(null);
                } else if(other != self()){
                    ((BinaryNodeBuild)other).disconnect();
                    getLink(other.pos()).configure(pos());
                    configure(other.pos());
                }
                return false;
            }
            return true;
        }

        public void disconnect(){
            if(linkedNode() != null){
                linkedNode().configure(null);
            }
            configure(null);
        }

        @Override
        public void remove(){
            super.remove();
            disconnect();
        }

        @Override
        public Point2 config(){
            return Point2.unpack(link).sub(tile.x, tile.y);
        }

        public BinaryNodeBuild linkedNode(){
            return getLink(link);
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.i(link);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, (byte)(revision + 1));

            link = read.i();
        }
    }
}