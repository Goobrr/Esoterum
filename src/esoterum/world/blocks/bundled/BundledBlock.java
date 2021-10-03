package esoterum.world.blocks.bundled;

import arc.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import esoterum.interfaces.*;
import esoterum.world.blocks.binary.ColorWire;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.meta.*;

public class BundledBlock extends Block {
    public TextureRegion connectionRegion;
    public TextureRegion topRegion;
    /** in order {front, left, back, right} */
    public boolean[] outputs = new boolean[]{false, false, false, false};
    /** in order {front, left, back, right} */
    public boolean[] inputs = new boolean[]{false, false, false, false};
    public boolean emits;
    public boolean allOutputs;
    public boolean drawConnectionArrows;
    public boolean drawRot = true;

    public BundledBlock(String name) {
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
        connectionRegion = Core.atlas.find(name + "-connection", "esoterum-bundled-connection");
        topRegion = Core.atlas.find(name + "-bundled-top"); // router supremacy
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
        return (other != this || rotate) && other instanceof BundledBlock && size == other.size;
    }

    public class BundledBuild extends Building implements Bundledc {
        /** in order {front, left, back, right} */
        public Seq<BundledBuild> nb = new Seq<>(4);
        public Seq<ColorWire.ColorWireBuild> nbb = new Seq<>(4);
        public boolean[] connections = new boolean[]{false, false, false, false};

        public short nextSignal = 0;
        public short lastSignal = 0;

        @Override
        public void draw(){
            super.draw();
            Draw.z(Draw.z()+0.1f);
            drawConnections();
            Draw.rect(topRegion, x, y, (rotate && drawRot) ? rotdeg() : 0f);
            Draw.z(Draw.z()-0.1f);
        }

        public void drawConnections(){
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
            nbb.clear();
            nbb.add(
                checkType2(front()),
                checkType2(left()),
                checkType2(back()),
                checkType2(right())
            );
            updateConnections();
        }

        public void updateConnections(){
            for(int i = 0; i < 4; i++){
                connections[i] = connectionCheck(nb.get(i), this) || connectionCheck(nbb.get(i), this);
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
                    //e.label(() -> "State: " + (lastSignal ? "1" : "0")).color(Color.lightGray);
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
        public boolean allOutputs(){
            return allOutputs;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            lastSignal = read.s();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.s(lastSignal);
        }

        @Override
        public byte version() {
            return 1;
        }
    }
}
