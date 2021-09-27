package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.graphics.*;

// each side's behavior is configurable.
public class SignalController extends BinaryRouter{
    public String[] states = new String[]{"X", "I", "O"};

    public TextureRegion inputRegion, outputRegion;

    public SignalController(String name){
        super(name);
        configurable = saveConfig = true;

        config(IntSeq.class, (ControllerBuild b, IntSeq i) -> b.configs = IntSeq.with(i.items));

        config(Integer.class, (ControllerBuild b, Integer i) -> {
            b.configs.incr(i, 1);
            if(b.configs.get(i) > 2) b.configs.set(i, 0);
        });
    }

    @Override
    public void load(){
        super.load();
        
        // looks better with rounded corners
        region = Core.atlas.find("esoterum-node-base");
        inputRegion = Core.atlas.find(name + "-in");
        outputRegion = Core.atlas.find(name + "-out");
    }

    public class ControllerBuild extends BinaryRouterBuild{
        /** IO configuration:
         * 0 = ignore/do nothing |
         * 1 = input |
         * 2 = output */
        public IntSeq configs = IntSeq.with(0, 0, 0, 0);

        @Override
        public void updateTile(){
            lastSignal = false;
            for(int i = 0; i < 4; i++){
                // check if the current side is configured to accept input
                lastSignal |= getSignal(nb.get(i), this) && configs.get(i) == 1;
            }
        }

        @Override
        public void drawConnections(){
            for(int i = 0; i < 4; i++){
                int c = configs.get(i);
                if(c == 0) continue;
                if(c == 1){
                    Draw.color(Color.white, Pal.accent, getSignal(nb.get(i), this) ? 1f : 0f);
                    Draw.rect(inputRegion, x, y, i * 90f);
                }else{
                    Draw.color(Color.white, Pal.accent, lastSignal ? 1f : 0f);
                    Draw.rect(outputRegion, x, y, i * 90f);
                }
            }
        }

        // i don't know how to arrange the buttons, so i just did this
        @Override
        public void buildConfiguration(Table table){
            table.table().size(40);
            addConfigButton(table, 1).align(Align.center);
            table.row();
            addConfigButton(table, 2);
            table.table().size(40);
            addConfigButton(table, 0);
            table.row();
            table.table().size(40);
            addConfigButton(table, 3).align(Align.center);
        }

        public Cell<Table> addConfigButton(Table table, int index){
            return table.table(t -> {
                TextButton b = t.button(states[configs.get(index)], () -> {
                    configure(index);
                    updateProximity();
                }).get();
                b.update(() -> b.setText(states[configs.get(index)]));
            }).size(40f);
        }

        @Override
        public void updateTableAlign(Table table){
            Vec2 pos = Core.input.mouseScreen(x, y);
            table.setPosition(pos.x, pos.y, Align.center);
        }

        @Override
        public Object config() {
            return configs;
        }

        @Override
        public boolean[] inputs() {
            return new boolean[]{
                configs.get(0) == 1,
                configs.get(1) == 1,
                configs.get(2) == 1,
                configs.get(3) == 1,
            };
        }

        @Override
        public boolean[] outputs() {
            return new boolean[]{
                configs.get(0) == 2,
                configs.get(1) == 2,
                configs.get(2) == 2,
                configs.get(3) == 2,
            };
        }

        // check if the current side is configured to output
        @Override
        public boolean signalFront() {
            return lastSignal && configs.get(0) == 2;
        }

        @Override
        public boolean signalBack() {
            return lastSignal && configs.get(2) == 2;
        }

        @Override
        public boolean signalLeft() {
            return lastSignal && configs.get(1) == 2;
        }

        @Override
        public boolean signalRight() {
            return lastSignal && configs.get(3) == 2;
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if(revision == 1){
                for(int i = 0; i < 4; i++){
                    configs.set(i, read.i());
                }
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            for(int i = 0; i < 4; i++){
                write.i(configs.get(i));
            }
        }
    }
}
