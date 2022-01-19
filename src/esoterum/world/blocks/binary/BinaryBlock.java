package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import esoterum.util.*;
import esoterum.util.graph.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.logic.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class BinaryBlock extends Block {
    public TextureRegion topRegion, connectionRegion, baseRegion, highlightRegion, stubRegion;
    public TextureRegion[] baseRegions, highlightRegions = new TextureRegion[4];
    public int[] tiles = new int[]{
        39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
        38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
        39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
        38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
         3,  4,  3,  4, 15, 40, 15, 20,  3,  4,  3,  4, 15, 40, 15, 20,
         5, 28,  5, 28, 29, 10, 29, 23,  5, 28,  5, 28, 31, 11, 31, 32,
         3,  4,  3,  4, 15, 40, 15, 20,  3,  4,  3,  4, 15, 40, 15, 20,
         2, 30,  2, 30,  9, 46,  9, 22,  2, 30,  2, 30, 14, 44, 14,  6,
        39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
        38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
        39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
        38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
         3,  0,  3,  0, 15, 42, 15, 12,  3,  0,  3,  0, 15, 42, 15, 12,
         5,  8,  5,  8, 29, 35, 29, 33,  5,  8,  5,  8, 31, 34, 31,  7,
         3,  0,  3,  0, 15, 42, 15, 12,  3,  0,  3,  0, 15, 42, 15, 12,
         2,  1,  2,  1,  9, 45,  9, 19,  2,  1,  2,  1, 14, 18, 14, 13
    };
    
    /** in order {front, left, back, right} */
    public boolean[] outputs = new boolean[]{false, false, false, false};
    public boolean[] inputs = new boolean[]{false, false, false, false};
    public boolean emits;
    public boolean allOutputs;
    public boolean drawConnectionArrows;
    public boolean drawRot = true;
    public String baseType = "square";
    public String baseHighlight = "none";
    public boolean rotatedBase = false;
    public int visitLimit = 5;
    public boolean propagates = true;

    public BinaryBlock(String name) {
        super(name);
        rotate = false;
        update = true;
        solid = true;
        destructible = true;
        hideDetails = false;
        buildVisibility = BuildVisibility.shown;
        category = Category.logic;
    }

    public void load() {
        super.load();
        baseRegion = Core.atlas.find("esoterum-base-" + baseType);
        highlightRegion = Core.atlas.find("esoterum-base-" + baseHighlight, "esoterum-base-none");
        for(int i = 0; i < 4; i++){
            highlightRegions[i] = Core.atlas.find("esoterum-base-" + baseHighlight + "-" + i, "esoterum-base-none");
        }
        connectionRegion = Core.atlas.find(name + "-connection", "esoterum-connection");
        topRegion = Core.atlas.find(name, "esoterum-router"); // router supremacy
        stubRegion = Core.atlas.find("esoterum-stub");
        baseRegions = getRegions(Core.atlas.find("esoterum-base-ultra"), 12, 4);
    }

    //yoinked from xelo
    public static TextureRegion[] getRegions(TextureRegion region, int w, int h){
        int size = w * h;
        TextureRegion[] regions = new TextureRegion[size];

        float tileW = (region.u2 - region.u) / w;
        float tileH = (region.v2 - region.v) / h;

        for(int i = 0; i < size; i++){
            float tileX = ((float)(i % w)) / w;
            float tileY = ((float)(i / w)) / h;
            TextureRegion reg = new TextureRegion(region);

            //start coordinate
            reg.u = Mathf.map(tileX, 0f, 1f, reg.u, reg.u2) + tileW * 0.02f;
            reg.v = Mathf.map(tileY, 0f, 1f, reg.v, reg.v2) + tileH * 0.02f;
            //end coordinate
            reg.u2 = reg.u + tileW * 0.96f;
            reg.v2 = reg.v + tileH * 0.96f;

            reg.width = reg.height = 32;

            regions[i] = reg;
        }
        return regions;
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{
            baseRegion,
            rotate && rotatedBase ? highlightRegions[0] : highlightRegion,
            topRegion
        };
    }

    @Override
    public boolean canReplace(Block other) {
        if(other.alwaysReplace) return true;
        return (other != this || rotate) && other instanceof BinaryBlock && size == other.size;
    }

    public class BinaryBuild extends Building {
        //front, left, back, right
        public BinaryBuild[] relnb = new BinaryBuild[]{null, null, null, null};
        //right, topright, top, topleft, left, bottomleft, bottom, bottomright
        public BinaryBuild[] absnb = new BinaryBuild[]{null, null, null, null, null, null, null, null};
        public boolean[] connections = new boolean[]{false, false, false, false};

        public boolean[] signal = new boolean[]{false, false, false, false, false, false};
        public ConnVertex v;
        public int mask = 0; //bitmasked draw

        // Mindustry saves block placement rotation even for blocks that don't rotate.
        // Usually this doesn't cause any problems, but with the current implementation
        // it is necessary for non-rotatable binary blocks to have a rotation of 0.
        @Override
        public void created(){
            super.created();
            if(!rotate) rotation(0);
            SignalGraph.addVertex(this);
            updateNeighbours();
            updateConnections();
            updateMask();
        }

        @Override
        public void updateProximity(){
            super.updateProximity();
            updateNeighbours();
            updateConnections();
            updateMask();
            updateSignal();
            for(int i=0;i<4;i++)
                if(absnb[i*2+1] != null){
                    absnb[i*2+1].updateNeighbours();
                    absnb[i*2+1].updateConnections();
                    absnb[i*2+1].updateMask();
                };
        }

        public void updateConnections(){
            for(int i = 0; i < 4; i++){
                connections[i] = connectionCheck(relnb[i], this);
            }
            SignalGraph.clearEdges(this);
            for(BinaryBuild b : getOutputs()){
                if(b != null) SignalGraph.addEdge(this, b);
            }
        }

        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            updateNeighbours();
            updateConnections();
            updateMask();
        }

        public void updateNeighbours(){
            //I don't like this
            relnb[0] = checkType(front());
            relnb[1] = checkType(left());
            relnb[2] = checkType(back());
            relnb[3] = checkType(right());
            absnb[0] = checkType(nearby(1, 0));
            absnb[1] = checkType(nearby(1, 1));
            absnb[2] = checkType(nearby(0, 1));
            absnb[3] = checkType(nearby(-1, 1));
            absnb[4] = checkType(nearby(-1, 0));
            absnb[5] = checkType(nearby(-1, -1));
            absnb[6] = checkType(nearby(0, -1));
            absnb[7] = checkType(nearby(1, -1));
        }

        public void updateMask(){
            mask = 0;
            for(int i = 0; i < 8; i++){
                if(i % 2 == 0){
                    mask |= (connectionCheck(this, absnb[i]) ? 1 : 0) << i;
                } else if(absnb[(i-1)%8] != null && absnb[(i+1)%8] != null){
                    mask |= ((connectionCheck(absnb[(i-1)%8], absnb[(i-1)%8].absnb[(i+1)%8]) && connectionCheck(absnb[(i+1)%8], absnb[(i+1)%8].absnb[(i-1)%8])) ? 1 : 0) << i;
                }
            }
        }

        public boolean updateSignal(){
            return true;
        }

        @Override
        public void updateTile(){
            updateSignal();
            SignalGraph.graph.setVertexAugmentation(v, signal()?1:0);
        }

        public BinaryBuild[] getInputs(){
            BinaryBuild[] i = new BinaryBuild[relnb.length];
            int c = 0;
            for(BinaryBuild b : relnb)
                if (b != null && inputs(c) && connections[c]) i[c] = b;
            return i;
        }

        public BinaryBuild[] getOutputs(){
            BinaryBuild[] o = new BinaryBuild[relnb.length];
            int c = 0;
            for(BinaryBuild b : relnb){
                if (b != null && outputs(c) && connections[c]) o[c] = b;
                c++;
            }
            return o;
        }

        public BinaryBlock.BinaryBuild checkType(Building b){
            if(b instanceof BinaryBlock.BinaryBuild bb) return bb;
            return null;
        }

        public boolean connectionCheck(Building from, BinaryBlock.BinaryBuild to){
            if(from == null || to == null) return false;
            if(from instanceof BinaryBlock.BinaryBuild b){
                int t = EsoUtil.relativeDirection(b, to);
                int f = EsoUtil.relativeDirection(to, b);
                return b.outputs(t) & to.inputs(f)
                    || to.outputs(f) & b.inputs(t);
            }
            return false;
        }

        public boolean getSignal(Building from, BinaryBlock.BinaryBuild to){
            if(from instanceof BinaryBlock.BinaryBuild b)
                return b.signal[EsoUtil.relativeDirection(b, to)];
            return false;
        }

        public boolean signal(){
            return signal[0] || signal[1] || signal[2] || signal[3];
        }

        public void signal(boolean b){
            signal[0] = signal[1] = signal[2] = signal[3] = b;
        }

        @Override
        public void draw(){
            drawBase();
            drawConnections();
            Draw.color(Color.white, team.color, Mathf.num(signal()));
            Draw.rect(topRegion, x, y, (rotate && drawRot) ? rotdeg() : 0f);
            Draw.color();
            drawStubs();
        }

        public void drawBase(){
            Draw.rect(baseRegions[tiles[mask]], x, y);
            if(!rotate || !rotatedBase){
                Draw.rect(highlightRegion, x, y);
            }else{
                Draw.rect(highlightRegions[rotation], x, y);
            }
        }

        public void drawConnections(){
            for(int i = 0; i < 4; i++){
                if(connections[i]){
                    Draw.color(Color.white, team.color, Mathf.num((getSignal(relnb[i], this) && relnb[i].outputs(EsoUtil.relativeDirection(relnb[i], this))) || (signal[i] && relnb[i].inputs(EsoUtil.relativeDirection(relnb[i], this)))));
                    Draw.rect(connectionRegion, x, y, rotdeg() + 90 * i);
                }
            }
        }

        public void drawStubs(){
            for(int i = 0; i < 4; i++){
                if(!connections[i]){
                    Draw.rect(stubRegion, x, y, rotdeg() + 90 * i);
                }
            }
        }

        @Override
        public void drawTeam(){
            //no
        }

        @Override
        public void drawSelect(){
            if(!drawConnectionArrows) return;
            BinaryBuild b;
            for(int i = 0; i < 4; i++){
                if(connections[i]){
                    b = relnb[i];

                    Draw.z(Layer.overlayUI);
                    Lines.stroke(3f);
                    Draw.color(Pal.gray);
                    Lines.line(x, y, b.x, b.y);
                }
            }

            for(int i = 0; i < 4; i++){
                if(outputs(i) && connections[i]){
                    b = relnb[i];
                    Draw.z(Layer.overlayUI + 1);
                    Drawf.arrow(x, y, b.x, b.y, 2f, 2f, signal() ? team.color : Color.white);
                }
            }

            for (int i = 0; i < 4; i++){
                if(connections[i]) {
                    b = relnb[i];
                    Draw.z(Layer.overlayUI + 3);
                    Lines.stroke(1f);
                    Draw.color((outputs(i) ? signal() : getSignal(b, this)) ? team.color : Color.white);
                    Lines.line(x, y, b.x, b.y);

                    Draw.reset();
                }
            }
        }

        @Override
        public void displayBars(Table table) {
            super.displayBars(table);
            table.table(e -> {
                e.row();
                e.left();
                e.label(() -> "State: " + (signal() ? "1" : "0")).color(Color.lightGray);
            }).left();
        }

        public boolean outputs(int i){
            return outputs[i];
        }

        public boolean inputs(int i) {
            return inputs[i];
        }

        public boolean propagates(){
            return propagates;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if(revision >= 2){
                signal[0] = read.bool();
                signal[1] = read.bool();
                signal[2] = read.bool();
                signal[3] = read.bool();
            } else if(revision >= 1){
                signal[0] = signal[1] = signal[2] = signal[3] = read.bool();
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.bool(signal[0]);
            write.bool(signal[1]);
            write.bool(signal[2]);
            write.bool(signal[3]);
        }

        @Override
        public byte version() {
            return 2;
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.enabled) return Mathf.num(signal());
            return super.sense(sensor);
        }
    }
}
