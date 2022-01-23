package esoterum.world.blocks.binary.transmission;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import esoterum.graphics.*;
import esoterum.world.blocks.binary.*;
import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.ui.*;

public class BinaryBuffer extends BinaryBlock{
    public BinaryBuffer(String name){
        super(name);
        emits = true;
        rotate = true;
        rotatedBase = true;
        drawArrow = true;
        configurable = saveConfig = true;
        baseHighlight = "gold";
        undirected = false;
        inputs = new boolean[]{false, true, true, true};
        outputs = new boolean[]{true, false, false, false};

        config(IntSeq.class, (BinaryBufferBuild b, IntSeq i) -> {
            b.configs = IntSeq.with(i.items);
            b.updateProximity();
        });
    }

    public class BinaryBufferBuild extends BinaryBuild{
        public float delayTimer = 0f;

        public float delay = 5f;
        public float ticks = 1f;
        /** Direction, Multiplier, Multiplier (but smol), Persistent */
        public IntSeq configs = IntSeq.with(2, 1, 0, 1);

        @Override
        public int inV(int dir){
            if(dir == 0) return 0;
            else return 1;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(signal[4]){
                delayTimer += Time.delta;
            }else{
                if(configs.get(3) == 0){
                    delayTimer = 0;
                }
                delayTimer -= Time.delta;
            }

            // this looks terrible
            if(delayTimer > trueDelay()){
                signal[0]  = true;
                delayTimer = trueDelay();
            }
            if(delayTimer < 0f){
                signal[0] = false;
                delayTimer = 0f;
            }
        }

        @Override
        public boolean updateSignal(){
            signal[5] = signal[4];
            signal[4] = getSignal(relnb[configs.first()], this);
            SignalGraph.graph.setVertexAugmentation(v[0], signal()?1:0);
            return signal[5] != signal[4];
        }

        public float trueDelay(){
            float temp = delay * configs.get(1) + ticks * configs.get(2);
            return temp == 0 ? 1f : temp;
        }

        @Override
        public void draw(){
            drawBase();
            Draw.color(signal() ? team.color : Color.white);
            Draw.rect(connectionRegion, x, y, rotdeg());
            drawConnections();
            drawBuffer();
            Draw.color();
            drawStubs();
        }

        public void drawBuffer(){
            Draw.color(Color.white);
            Lines.stroke(0.5f);
            Lines.circle(x, y, 1.5f);
            float progress = delayTimer / trueDelay();
            if(progress > 0.01f){
                Draw.color(team.color);
                EsoDrawf.arc(x, y, 1.85f, rotdeg() - 180, 360 * progress);
            }
            if(configs.get(3) != 1){
                Draw.color(EsoPal.esoDark);
                Fill.circle(x, y, 0.8f);
            }
        }

        public void drawConnections(){
            Draw.color(signal() ? team.color : Color.white);
            Draw.rect(connectionRegion, x, y, rotdeg() + 90 * configs.first());
        }

        @Override
        public void displayBars(Table table){
            super.displayBars(table);
            table.row();
            table.table(e -> {
                e.row();
                e.left();
                e.label(() -> "Delay: " + Mathf.floor(trueDelay()) + " ticks" + (configs.get(3) == 1 ? " (Persistent)" : "")).color(Color.lightGray);
            }).left();
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(Styles.black5, t -> {
                t.table(dt -> {
                    dt.button(Icon.rotate, () -> {
                        configs.incr(0, -1);
                        if(configs.first() < 1){
                            configs.set(0, 3);
                        }
                        configure(configs);
                    }).size(40f).tooltip("Rotate Input");
                    dt.button(Icon.settings, () -> {
                        configs.set(3, 1 - configs.get(3));
                        configure(configs);
                    }).size(40f).tooltip("Signal persistence");
                    dt.table(Tex.button, label -> {
                        label.labelWrap(() -> Mathf.floor(trueDelay()) + "t")
                            .growX()
                            .left();
                    }).growX().left();
                }).height(40f).growX();
                t.row();
                t.table(Tex.button, dt -> {
                    dt.slider(0, 12, 1, configs.get(1), i -> {
                        configs.set(1, (int)i);
                        configure(configs);
                    }).height(40f).growX().left();
                    dt.row();
                    dt.slider(0, 5, 1, configs.get(2), i -> {
                        configs.set(2, (int)i);
                        configure(configs);
                    }).height(40f).growX().left();
                });
            });
        }

        @Override
        public Object config(){
            return configs;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.f(delayTimer);
            write.i(configs.get(0));
            write.i(configs.get(1));
            write.i(configs.get(2));
            write.i(configs.get(3));
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, (byte)(revision + 1));

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
