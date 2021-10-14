package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import esoterum.graphics.*;
import esoterum.ui.*;
import esoterum.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

public class MusicBuffer extends BinaryBlock{
    protected float[] restMul = {0.25f, 0.5f, 1, 2, 4};
    public TextureRegionDrawable score;

    public MusicBuffer(String name){
        super(name);
        emits = true;
        rotate = true;
        rotatedBase = true;
        drawArrow = true;
        configurable = saveConfig = true;
        baseType = 1;

        inputs = new boolean[]{false, true, true, true};
        outputs = new boolean[]{true, false, false, false};

        config(IntSeq.class, (MusicBufferBuild b, IntSeq i) -> b.configs = IntSeq.with(i.items));
    }

    @Override
    public void load(){
        super.load();

        score = new TextureRegionDrawable(Core.atlas.find("esoterum-score"));
    }

    public class MusicBufferBuild extends BinaryBuild{
        public float delayTimer = 0f;

        public boolean bufferedSignal;
        /** Direction, BPM, Rest */
        public IntSeq configs = IntSeq.with(2, 120, 2);

        @Override
        public void updateTile(){
            super.updateTile();
            if(bufferedSignal){
                delayTimer += Time.delta;
            }else{
                delayTimer -= Time.delta;
            }

            // this looks terrible
            if(delayTimer > trueDelay()){
                signal[0]  = true;
                delayTimer = trueDelay();
                propagateSignal(true, false, false, false);
            }
            if(delayTimer < 0f){
                signal[0] = false;
                delayTimer = 0f;
                propagateSignal(true, false, false, false);
            }
        }

        @Override
        public void updateSignal(int source){
            try{
                super.updateSignal(source);
                bufferedSignal = getSignal(nb.get(configs.first()), this);
            }catch(Exception ignored){}
        }

        public float trueDelay(){
            float BPM = configs.get(1);
            float quarterNote = 3600 / BPM;
            return quarterNote * restMul[configs.get(2)];
        }

        @Override
        public void draw(){
            drawBase();
            Draw.color(signal() ? Pal.accent : Color.white);
            Draw.rect(connectionRegion, x, y, rotdeg());
            drawConnections();
            drawBuffer();
        }

        public void drawBuffer(){
            Draw.color(Color.white);
            Lines.stroke(0.5f);
            Lines.circle(x, y, 1.5f);
            Draw.color(Pal.accent);
            EsoDrawf.arc(x, y, 1.85f, rotdeg() - 180, 360 * (delayTimer / trueDelay()));
        }

        public void drawConnections(){
            Draw.color(signal() ? Pal.accent : Color.white);
            Draw.rect(connectionRegion, x, y, rotdeg() + 90 * configs.first());
        }

        @Override
        public void buildConfiguration(Table table){
            table.table(t -> {
                t.background(Styles.black5);
                t.table(bt -> {
                    bt.button(Icon.rotate, () -> {
                        configs.incr(0, -1);
                        if(configs.first() < 1){
                            configs.set(0, 3);
                        }
                        configure(configs);
                    }).size(40f).tooltip("Rotate Input").left();
                    bt.field(String.valueOf(configs.get(1)), d -> {
                        d = EsoUtil.extractNumber(d);
                        if(!d.isEmpty()){
                            int q = Math.min((int)(Float.parseFloat(d)), 900);
                            configs.set(1, q);
                            configure(configs);
                        }
                    }).right().width(100);
                    bt.add("BPM").left();
                }).fillX();
                t.row();
                t.table(rt -> {
                    rt.background(score);
                    for(int i = 0; i < 5; i++){
                        int ii = i;
                        TextButton ib = rt.button("", EsoStyle.rests[i], () -> {
                            configs.set(2, ii);
                            configure(configs);
                        }).center().align(Align.center).size(50, 164).scaling(Scaling.none).get();
                        ib.update(() -> ib.setChecked(configs.get(2) == ii));
                    }
                }).height(164).fillX();
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
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            delayTimer = read.f();
            configs.set(0, read.i());
            configs.set(1, read.i());
            configs.set(2, read.i());
        }
    }
}