package esoterum.world.blocks.binary.source;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import esoterum.graphics.*;
import esoterum.util.*;
import esoterum.world.blocks.binary.*;
import mindustry.gen.*;
import mindustry.ui.*;

public class BinaryClock extends BinaryBlock{
    public int maxInterval = 300;

    public BinaryClock(String name){
        super(name);
        outputs = new boolean[]{true, true, true, true};
        configurable = true;
        emits = true;
        baseHighlight = "gold";
        undirected = false;
        config(IntSeq.class, (BinaryClockBuild b, IntSeq i) -> b.configs = IntSeq.with(i.items));
    }

    public class BinaryClockBuild extends BinaryBuild{
        /** Interval, Active Time, Offset */
        public IntSeq configs = IntSeq.with(60, 20, 0);

        @Override
        public int inV(int dir){
            return dir;
        }

        @Override
        public void updateTile(){
            super.updateTile();
            signal(Mathf.mod(Time.time - configs.get(2), configs.first()) <= configs.get(1));
            SignalGraph.graph.setVertexAugmentation(v[0], signal()?1:0);
            SignalGraph.graph.setVertexAugmentation(v[1], signal()?1:0);
            SignalGraph.graph.setVertexAugmentation(v[2], signal()?1:0);
            SignalGraph.graph.setVertexAugmentation(v[3], signal()?1:0);
        }

        @Override
        public void draw(){
            drawBase();
            drawConnections();
            Lines.stroke(0.5f);
            Draw.color(Color.white, team.color, Mathf.num(signal()));
            Lines.circle(x, y, 1.5f);
            Draw.color(team.color);
            EsoDrawf.arc(x, y, 1.85f, -configs.get(2) / (float)configs.first() * 360f + 90f, configs.get(1) / (float)configs.first() * 360f);
            Draw.color(Color.white);
            Fill.circle(x, y, 0.75f);
            Lines.lineAngle(x, y, -Mathf.mod(Time.time, configs.first()) / configs.first() * 360f + 90f, 1.5f);
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(Styles.black5, t -> {
                t.table(Tex.underline, it -> {
                    it.add("Interval:");
                    it.row();
                    it.table(s -> {
                        TextField iField = s.field(configs.first() + "t", i -> {
                            i = EsoUtil.extractNumber(i);
                            if(!i.isEmpty() && Float.parseFloat(i) > 1){
                                configs.set(0, (int)(Float.parseFloat(i)));
                                configure(configs);
                            }
                        }).labelAlign(Align.right).padRight(8).size(100, 40).get();
                        iField.update(() -> {
                            Scene stage = iField.getScene();
                            if(!(stage != null && stage.getKeyboardFocus() == iField))
                                iField.setText(configs.first() + "t");
                        });
                        s.slider(2, maxInterval, 1, configs.first(), i -> {
                            configs.set(0, (int)i);
                            configure(configs);
                        }).height(40f).growX().left().get();
                    }).padTop(8);
                }).growX().get();
                t.row();
                t.table(Tex.underline, at -> {
                    at.add("Active Time:");
                    at.row();
                    at.table(s -> {
                        TextField aField = s.field(configs.get(1) + "t", i -> {
                            i = EsoUtil.extractNumber(i);
                            if(!i.isEmpty()){
                                configs.set(1, (int)(Float.parseFloat(i)));
                                configure(configs);
                            }
                        }).labelAlign(Align.right).padRight(8).size(100, 40).get();
                        aField.update(() -> {
                            Scene stage = aField.getScene();
                            if(!(stage != null && stage.getKeyboardFocus() == aField))
                                aField.setText(configs.get(1) + "t");
                        });
                        Slider aSlider = s.slider(0, configs.first(), 1, configs.get(1), i -> {
                            configs.set(1, (int)i);
                            configure(configs);
                        }).height(40f).growX().left().get();
                        aSlider.update(() -> {
                            aSlider.setRange(0, configs.first());
                            aSlider.setValue(configs.get(1));
                        });
                    }).padTop(8);
                }).growX().get();
                t.row();
                t.table(Tex.underline, ot -> {
                    ot.add("Offset:");
                    ot.row();
                    ot.table(s -> {
                        TextField oField = s.field(configs.get(2) + "t", i -> {
                            i = EsoUtil.extractNumber(i);
                            if(!i.isEmpty()){
                                configs.set(2, (int)(Float.parseFloat(i)));
                                configure(configs);
                            }
                        }).labelAlign(Align.right).padRight(8).size(100, 40).get();
                        oField.update(() -> {
                            Scene stage = oField.getScene();
                            if(!(stage != null && stage.getKeyboardFocus() == oField))
                                oField.setText(configs.get(2) + "t");
                        });
                        Slider oSlider = s.slider(0, configs.first(), 1, configs.get(2), i -> {
                            configs.set(2, (int)i);
                            configure(configs);
                        }).height(40f).growX().left().get();
                        oSlider.update(() -> {
                            oSlider.setRange(0, configs.first());
                            oSlider.setValue(configs.get(2));
                        });
                    }).padTop(8);
                }).growX().get();
            });
        }

        @Override
        public Object config(){
            return configs;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.i(configs.first());
            write.i(configs.get(1));
            write.i(configs.get(2));
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, (byte)(revision + 1));

            if(revision >= 2){
                configs = IntSeq.with(read.i(), read.i(), read.i());
            }
        }

        @Override
        public byte version(){
            return 2;
        }
    }
}
