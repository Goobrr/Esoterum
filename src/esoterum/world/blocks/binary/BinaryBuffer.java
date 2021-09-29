package esoterum.world.blocks.binary;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

public class BinaryBuffer extends BinaryBlock{

    public BinaryBuffer(String name){
        super(name);
        emits = true;
        rotate = true;
        drawArrow = true;
        configurable = saveConfig = true;

        inputs = new boolean[]{false, true, true, true};
        outputs = new boolean[]{true, false, false, false};

        config(IntSeq.class, (BinaryBufferBuild b, IntSeq i) -> b.configs = IntSeq.with(i.items));
    }

    public class BinaryBufferBuild extends BinaryBuild{
        public float delayTimer = 0f;

        public float delay = 5f;

        /** Direction, Multiplier */
        public IntSeq configs = IntSeq.with(2, 1);

        @Override
        public void updateTile() {
            if(signal()){
                delayTimer += Time.delta;
            }else{
                delayTimer -= Time.delta;
            }

            // this looks terrible
            if(delayTimer > trueDelay()){
                lastSignal  = true;
                delayTimer = trueDelay();
            }
            if(delayTimer < 0f){
                lastSignal = false;
                delayTimer = 0f;
            }
        }

        public float trueDelay(){
            return delay * configs.get(1);
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            Draw.color(lastSignal ? Pal.accent : Color.white);
            Draw.rect(connectionRegion, x, y, rotdeg());
            drawConnections();
            Draw.color(Color.white, Pal.accent, delayTimer / trueDelay());
            Draw.rect(topRegion, x, y, rotdeg());
        }

        public void drawConnections(){
            Draw.color(lastSignal ? Pal.accent : Color.white);
            Draw.rect(connectionRegion, x, y, rotdeg() + 90 * configs.first());
        }

        @Override
        public boolean signal() {
            return getSignal(nb.get(configs.first()), this);
        }

        @Override
        public boolean signalFront() {
            return lastSignal;
        }

        @Override
        public void displayBars(Table table) {
            super.displayBars(table);
            table.row();
            table.table(e -> {
                Runnable rebuild = () -> {
                    e.clearChildren();
                    e.row();
                    e.left();
                    e.label(() -> "Delay: " + Mathf.floor(trueDelay()) + " ticks").color(Color.lightGray);
                };

                e.update(rebuild);
            }).left();
        }

        @Override
        public void buildConfiguration(Table table){
            table.setBackground(Styles.black5);
            table.button(Icon.rotate, () -> {
                configs.incr(0, -1);
                if(configs.first() < 1){
                    configs.set(0, 3);
                }
                configure(configs);
            }).size(40f);
            table.table(Tex.button, t -> {
                t.left();
                t.button(Icon.settingsSmall, Styles.emptyi, () -> {
                    configs.incr(1, 1);
                    if(configs.get(1) >= 13){
                        configs.set(1, 1);
                    }
                    configure(configs);
                }).size(40f).left();
                t.labelWrap(() -> Mathf.floor(trueDelay()) + "t")
                    .labelAlign(Align.left)
                    .growX()
                    .left();
            }).size(110f, 40f);
        }

        @Override
        public Object config() {
            return configs;
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(delayTimer);
            write.i(configs.get(0));
            write.i(configs.get(1));
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if(revision >= 1){
                delayTimer = read.f();
            }
            if(revision >= 2){
                configs = IntSeq.with(read.i(), read.i());
            }
        }

        @Override
        public byte version() {
            return 2;
        }
    }
}
