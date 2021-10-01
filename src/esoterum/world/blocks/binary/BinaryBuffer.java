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
import mindustry.logic.*;

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
        public float ticks = 1f;

        /** Direction, Multiplier, Multiplier, Persistent */
        public IntSeq configs = IntSeq.with(2, 1, 0, 1);

        @Override
        public void updateTile() {
            if(signal()){
                delayTimer += Time.delta;
            }else{
                if(configs.get(3) == 0){
                    delayTimer = 0;
                }
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
            float temp = delay * configs.get(1) + ticks * configs.get(2);
            return temp == 0 ? 1f : temp;
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
            Draw.color(signal() ? Pal.accent : Color.white);
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
                    e.label(() -> "Delay: " + Mathf.floor(trueDelay()) + " ticks" + (configs.get(3) == 1 ? " (Persistent)" : ""))
                        .color(Color.lightGray);
                };

                e.update(rebuild);
            }).left();
        }

        @Override
        public void buildConfiguration(Table table){
            table.setBackground(Styles.black5);
            table.table(t -> {
                t.button(Icon.rotate, () -> {
                    configs.incr(0, -1);
                    if(configs.first() < 1){
                        configs.set(0, 3);
                    }
                    configure(configs);
                }).size(40f).tooltip("Rotate Input");
                t.button(Icon.settings, () -> {
                    configs.set(3, 1 - configs.get(3));
                    configure(configs);
                }).size(40f).tooltip("Signal persistence");
                t.table(Tex.button, label -> {
                    label.labelWrap(() -> Mathf.floor(trueDelay()) + "t")
                        .growX()
                        .left();
                }).growX().left();
            }).height(40f).growX();
            table.row();
            table.table(Tex.button, t -> {
                t.slider(0, 12, 1, configs.get(1),i -> {
                    configs.set(1, (int) i);
                    configure(configs);
                }).height(40f).growX().left();
                t.row();
                t.slider(0, 5, 1, configs.get(2),i -> {
                    configs.set(2, (int) i);
                    configure(configs);
                }).height(40f).growX().left();
            });
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
            write.i(configs.get(2));
            write.i(configs.get(3));
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if(revision >= 1){
                delayTimer = read.f();
            }
            if(revision >= 2){
                configs.set(0, read.i());
                configs.set(1, read.i());
            }
            if(revision >= 3){
                configs.set(2, read.i());
            }
            if(revision >= 4){
                configs.set(3, read.i());
            }
        }

        @Override
        public byte version() {
            return 4;
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.config){
                //controlling capability
                int p = (int) p1;
                if (p <= 0){
                    configs.set(1, 0);
                    configs.set(2, 1);
                    configure(configs);
                    return;
                }
                configs.set(1, p/5);
                configs.set(2, p%5);
                configure(configs);
            }
        }
    }
}
