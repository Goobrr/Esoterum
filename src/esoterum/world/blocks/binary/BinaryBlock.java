package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
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
    /** in order {front, left, back, right} */
    public boolean[] outputs = new boolean[]{false, false, false, false};
    public boolean[] inputs = new boolean[]{false, false, false, false};
    public boolean emits;
    public boolean allOutputs;
    public boolean drawConnectionArrows;
    public boolean drawRot = true;

    public BinaryBlock(String name) {
        super(name);
        rotate = false;
        update = true;
        solid = true;
        destructible = true;
        buildVisibility = BuildVisibility.shown;

        category = Category.logic;
    }

    public void load() {
        super.load();
        region = Core.atlas.find(name + "-base", "esoterum-base");
        connectionRegion = Core.atlas.find(name + "-connection", "esoterum-connection");
        topRegion = Core.atlas.find(name + "-top", "esoterum-router-top"); // router supremacy
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{
            region,
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

        public int nextSignal;
        public int lastSignal;

        public int signal(){
            return 0;
        }

        public int signalFront(){
            return 0;
        }

        public int signalLeft(){
            return 0;
        }

        public int signalBack(){
            return 0;
        }

        public int signalRight(){
            return 0;
        }
    
        // get relative direction of "To" from "From"'s perspective then get the associated signal output.
        public int getSignalRelativeTo(BinaryBlock.BinaryBuild from, BinaryBlock.BinaryBuild to){
            if(!from.emits()) return 0;
            
            return switch(EsoUtil.relativeDirection(from, to)) {
                case 0 -> from.signalFront(); //front
                case 1 -> from.signalLeft(); //left
                case 2 -> from.signalBack(); //back
                case 3 -> from.signalRight(); //right
                default -> 0;
            };
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
    
        public int getSignal(Building from, BinaryBlock.BinaryBuild to){
            if(from instanceof BinaryBlock.BinaryBuild b) return getSignalRelativeTo(b, to);
            return 0;
        }
    
        public BinaryBlock.BinaryBuild checkType(Building b){
            if(b instanceof BinaryBlock.BinaryBuild bb) return bb;
            return null;
        }

        @Override
        public void draw(){
            super.draw();

            drawConnections();
            Draw.color(Color.white, Pal.accent, lastSignal > 0 ? 1f : 0f);
            Draw.rect(topRegion, x, y, (rotate && drawRot) ? rotdeg() : 0f);
        }

        public void drawConnections(){
            Draw.color(Color.white, Pal.accent, lastSignal > 0 ? 1f : 0f);
            for(int i = 0; i < 4; i++){
                if(connections[i]) Draw.rect(connectionRegion, x, y, rotdeg() + 90 * i);
            }
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
                    Drawf.arrow(x, y, b.x, b.y, 2f, 2f, lastSignal > 0 ? Pal.accent : Color.white);
                }
            }

            for (int i = 0; i < 4; i++){
                if(connections[i]) {
                    b = nb.get(i);
                    Draw.z(Layer.overlayUI + 3);
                    Lines.stroke(1f);
                    Draw.color((outputs(i) ? lastSignal : getSignal(b, this)) > 0 ? Pal.accent : Color.white);
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
                Runnable rebuild = () -> {
                    e.clearChildren();
                    e.row();
                    e.left();
                    e.label(() -> "State: " + (lastSignal > 0 ? "1" : "0")).color(Color.lightGray);
                };

                e.update(rebuild);
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
                nextSignal = lastSignal = read.i();
            } else if(revision >= 1){
                nextSignal = lastSignal = read.bool()?1:0;
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(lastSignal);
        }

        @Override
        public byte version() {
            return 2;
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.enabled) return lastSignal > 0 ? 1 : 0;
            return super.sense(sensor);
        }
    }
}
