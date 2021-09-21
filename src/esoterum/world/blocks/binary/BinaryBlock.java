package esoterum.world.blocks.binary;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import esoterum.graphics.EsoDrawf;
import esoterum.interfaces.Binaryc;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.Category;
import mindustry.world.*;
import mindustry.world.meta.BuildVisibility;

public class BinaryBlock extends Block {
    public TextureRegion connectionRegion;
    public TextureRegion topRegion;
    /** in order {front, left, back, right} */
    public boolean[] outputs = new boolean[]{false, false, false, false};
    /** in order {front, left, back, right} */
    public boolean[] inputs = new boolean[]{false, false, false, false};
    public boolean emits = false;

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

    public class BinaryBuild extends Building implements Binaryc {
        /** in order {front, left, back, right} */
        public Seq<BinaryBuild> nb = new Seq<>(4);
        public boolean[] connections = new boolean[]{false, false, false, false};

        public boolean nextSignal;
        public boolean lastSignal;

        @Override
        public void draw(){
            super.draw();

            drawConnections();
            Draw.color(Color.white, Pal.accent, lastSignal ? 1f : 0f);
            Draw.rect(topRegion, x, y, rotate ? rotdeg() : 0f);
        }

        public void drawConnections(){
            Draw.color(Color.white, Pal.accent, lastSignal ? 1f : 0f);
            for(int i = 0; i < 4; i++){
                if(connections[i]) Draw.rect(connectionRegion, x, y, rotdeg() + 90 * i);
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
                    e.label(() -> "State: " + (lastSignal ? "1" : "0")).color(Color.lightGray);
                };

                e.update(rebuild);
            }).left();
        }

        // emission
        public boolean emits(){
            return emits;
        }

        public boolean[] outputs(){
            return outputs;
        }
        public boolean[] inputs() {
            return inputs;
        }
    }
}
