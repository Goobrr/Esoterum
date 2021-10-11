package esoterum.world.blocks.binary;

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
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

public class BinaryClock extends BinaryBlock{
    public int maxInterval = 300;

    public BinaryClock(String name){
        super(name);
        outputs = new boolean[]{true, true, true, true};
        configurable = true;
        emits = true;
        transmits = false;
        baseType = 1;

        config(IntSeq.class, (BinaryClockBuild b, IntSeq i) -> b.configs = IntSeq.with(i.items));
    }

    public class BinaryClockBuild extends BinaryBuild{
        /** Interval, Active Time, Offset */
        public IntSeq configs = IntSeq.with(60, 20, 0);

        @Override
        public void updateSignal(int depth){
            signal(Mathf.mod(Time.time - configs.get(2), configs.first()) <= configs.get(1));
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);

            drawConnections();
            Lines.stroke(0.5f);
            Draw.color(Color.white, Pal.accent, signal() ? 1f : 0f);
            Lines.circle(x, y, 1.5f);
            Draw.color(Pal.accent);
            EsoDrawf.arc(x, y, 1.85f, -configs.get(2) / (float)configs.first() * 360f + 90f, configs.get(1) / (float)configs.first() * 360f);
            Draw.color(Color.white);
            Fill.circle(x, y, 0.75f);
            Lines.lineAngle(x, y, -Mathf.mod(Time.time, configs.first()) / configs.first() * 360f + 90f, 1.5f);
        }

        @Override
        public void buildConfiguration(Table table){
            table.setBackground(Styles.black5);
            table.table(t -> {
                t.add("Interval:");
                t.row();
                t.table(s -> {
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
            }).growX().get().background(Tex.underline);
            table.row();
            table.table(t -> {
                t.add("Active Time:");
                t.row();
                t.table(s -> {
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
            }).growX().get().background(Tex.underline);
            table.row();
            table.table(t -> {
                t.add("Offset:");
                t.row();
                t.table(s -> {
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
            }).growX().get().background(Tex.underline);
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
