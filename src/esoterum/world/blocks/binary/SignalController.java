package esoterum.world.blocks.binary;

import arc.Core;
import arc.math.geom.Vec2;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;

// each side's behavior is configurable.
public class SignalController extends BinaryRouter {
    public SignalController(String name){
        super(name);
        configurable = true;

        config(IntSeq.class, (ControllerBuild b, IntSeq i) -> b.configs = i);

        config(Integer.class, (ControllerBuild b, Integer i) -> {
            b.configs.incr(i, 1);
            if(b.configs.get(i) > 2) b.configs.set(i, 0);
        });
    }

    public class ControllerBuild extends BinaryRouterBuild {
        // IO configuration
        // 0 = ignore/do nothing
        // 1 = input
        // 2 = output
        public IntSeq configs = new IntSeq(new int[]{0, 0, 0, 0});
        public String[] states = new String[]{"-", "I", "O"};

        @Override
        public void updateTile() {
            lastSignal = false;
            for(int i = 0; i < 4; i++){
                // check if the current side is configured to accept input
                lastSignal |= getSignal(nb.get(i), this) && configs.get(i) == 1;
            }
        }


        // i don't know how to arrange the buttons, so i just did this
        @Override
        public void buildConfiguration(Table table) {
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

        public Cell<Table> addConfigButton(Table table, int index) {
            return table.table(t -> t.button(states[configs.get(index)], () -> {
                configure(index);
                ((TextButton) t.getChildren().first()).setText(states[configs.get(index)]);
            }).size(40f));
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
