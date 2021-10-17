package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import esoterum.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;
import mindustry.logic.*;

public class BinaryBlock extends Block {
    public TextureRegion topRegion, connectionRegion;
    public TextureRegion[] baseRegions = new TextureRegion[4];
    /** in order {front, left, back, right} */
    public boolean[] outputs = new boolean[]{false, false, false, false};
    public boolean[] inputs = new boolean[]{false, false, false, false};
    public boolean emits;
    public boolean allOutputs;
    public boolean drawConnectionArrows;
    public boolean drawRot = true;
    public int baseType = 0;
    public boolean rotatedBase = false;
    public int visitLimit = 5;

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
        if(rotatedBase){
            region = Core.atlas.find("esoterum-base-" + baseType, "esoterum-base-" + baseType);
        }else{
            region = Core.atlas.find(name + "-base", "esoterum-base-" + baseType);
        }
        for(int i = 0; i < 4; i++){
            baseRegions[i] = Core.atlas.find("esoterum-base-" + baseType + "-" + i, "esoterum-base");
        }
        connectionRegion = Core.atlas.find(name + "-connection", "esoterum-connection");
        topRegion = Core.atlas.find(name, "esoterum-router"); // router supremacy
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{
            rotate && rotatedBase ? baseRegions[0] : region,
            topRegion
        };
    }

    @Override
    public boolean canReplace(Block other) {
        if(other.alwaysReplace) return true;
        return (other != this || rotate) && other instanceof BinaryBlock && size == other.size;
    }

    public class BinaryBuild extends Building {
        public Seq<BinaryBuild> nb = new Seq<>(4);
        public boolean[] connections = new boolean[]{false, false, false, false};

        public boolean[] signal = new boolean[]{false, false, false, false, false};
        public boolean[] visited = {false, false, false, false};

        public void updateSignal(int source) throws Exception {
            updateSignal(source, () -> {return new boolean[4];});
        }

        //front, left, back, right, node, none
        public void updateSignal(int source, Updater updater) throws Exception{
            if(source < 4){
                if(visited[source])
                    throw new Exception();
                else visited[source] = true;
            }
            boolean[] directions = updater.fun();
            propagateSignal(directions[0], directions[1], directions[2], directions[3]);
            visited[source] = false;
        }

        @Override
        public void placed(){
            super.placed();
            try {updateSignal(5);} catch (Exception e) {}
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            signal(false);
            propagateSignal(outputs(0), outputs(1), outputs(2), outputs(3));
        }

        public void bypassSignal(boolean front, boolean left, boolean back, boolean right){
            Thread t = new Thread(null, null, "Bypass"){
                @Override
                public void run(){
                    propagateSignal(front, left, back, right);
                }
            };
            t.start();
            try {t.join();} catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        public void propagateSignal(boolean front, boolean left, boolean back, boolean right){
            try{
                try{
                    if(front && nb.get(0) != null && connectionCheck(this, nb.get(0)))
                        nb.get(0).updateSignal(EsoUtil.relativeDirection(nb.get(0), this));
                    if(left && nb.get(1) != null && connectionCheck(this, nb.get(1)))
                        nb.get(1).updateSignal(EsoUtil.relativeDirection(nb.get(1), this));
                    if(back && nb.get(2) != null && connectionCheck(this, nb.get(2)))
                        nb.get(2).updateSignal(EsoUtil.relativeDirection(nb.get(2), this));
                    if(right && nb.get(3) != null && connectionCheck(this, nb.get(3)))
                        nb.get(3).updateSignal(EsoUtil.relativeDirection(nb.get(3), this));
                }catch(Exception ignored){}
            }catch(StackOverflowError e){
                bypassSignal(front, left, back, right);
            }
        }

        @Override
        public void updateTile(){
            super.updateTile();
            visited[0] = false;
            visited[1] = false;
            visited[2] = false;
            visited[3] = false;
        }

        public boolean signal(){
            return signal[0] || signal[1] || signal[2] || signal[3];
        }

        public void signal(boolean s){
            signal[0] = signal[1] = signal[2] = signal[3] = s;
        }
    
        public boolean connectionCheck(Building from, BinaryBlock.BinaryBuild to){
            if(from instanceof BinaryBlock.BinaryBuild b){
                int t = EsoUtil.relativeDirection(b, to);
                int f = EsoUtil.relativeDirection(to, b);
                return b.outputs(t) & to.inputs(f)
                    || to.outputs(f) & b.inputs(t);
            }
            return false;
        }
    
        public boolean getSignal(Building from, BinaryBlock.BinaryBuild to){
            if(from instanceof BinaryBlock.BinaryBuild b){
                if(!b.emits()) return false;
                return b.signal[EsoUtil.relativeDirection(b, to)];
            }
            return false;
        }
    
        public BinaryBlock.BinaryBuild checkType(Building b){
            if(b instanceof BinaryBlock.BinaryBuild bb) return bb;
            return null;
        }

        @Override
        public void draw(){
            drawBase();
            drawConnections();
            Draw.color(Color.white, team.color, Mathf.num(signal()));
            Draw.rect(topRegion, x, y, (rotate && drawRot) ? rotdeg() : 0f);
        }

        public void drawBase(){
            if(!rotate || !rotatedBase){
                Draw.rect(region, x, y);
            }else{
                Draw.rect(baseRegions[rotation], x, y);
            }
        }

        public void drawConnections(){
            for(int i = 0; i < 4; i++){
                if(inputs(i)) Draw.color(Color.white, team.color, Mathf.num(getSignal(nb.get(i), this)));
                if(outputs(i)) Draw.color(Color.white, team.color, Mathf.num(signal()));
                if(connections[i]) Draw.rect(connectionRegion, x, y, rotdeg() + 90 * i);
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
                    b = nb.get(i);

                    Draw.z(Layer.overlayUI);
                    Lines.stroke(3f);
                    Draw.color(Pal.gray);
                    Lines.line(x, y, b.x, b.y);
                }
            }

            for(int i = 0; i < 4; i++){
                if(outputs(i) && connections[i]){
                    b = nb.get(i);
                    Draw.z(Layer.overlayUI + 1);
                    Drawf.arrow(x, y, b.x, b.y, 2f, 2f, signal() ? team.color : Color.white);
                }
            }

            for (int i = 0; i < 4; i++){
                if(connections[i]) {
                    b = nb.get(i);
                    Draw.z(Layer.overlayUI + 3);
                    Lines.stroke(1f);
                    Draw.color((outputs(i) ? signal() : getSignal(b, this)) ? team.color : Color.white);
                    Lines.line(x, y, b.x, b.y);

                    Draw.reset();
                }
            }
        }

        
        // Mindustry saves block placement rotation even for blocks that don't rotate.
        // Usually this doesn't cause any problems, but with the current implementation
        // it is necessary for non-rotatable binary blocks to have a rotation of 0.
        @Override
        public void created(){
            super.created();
            if(!rotate) rotation(0);
        }

        // connections
        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

            // update connected builds only when necessary
            nb.clear();
            nb.add(
                checkType(front()),
                checkType(left()),
                checkType(back()),
                checkType(right())
            );
            updateConnections();
        }

        public void updateConnections(){
            for(int i = 0; i < 4; i++){
                connections[i] = connectionCheck(nb.get(i), this);
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

        // emission
        public boolean emits(){
            return emits;
        }

        public boolean outputs(int i){
            return outputs[i];
        }
        public boolean inputs(int i) {
            return inputs[i];
        }
        public boolean allOutputs(){
            return allOutputs;
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

        public static interface Updater {
            public boolean[] fun();
        }
    }
}
