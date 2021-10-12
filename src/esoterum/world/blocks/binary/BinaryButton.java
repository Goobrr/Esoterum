package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.io.*;
import arc.math.*;
import mindustry.graphics.*;
import mindustry.logic.*;

public class BinaryButton extends BinaryBlock{
    // whether the button emits continuously (like a switch).
    public boolean continuous;
    // Buttons will have a pulse length of 60 ticks by default
    public float duration = 60;

    public TextureRegion onRegion, offRegion;

    public BinaryButton(String name, boolean cont){
        super(name);
        outputs = new boolean[]{true, true, true, true};
        configurable = true;
        continuous = cont;
        emits = true;
        baseType = 1;

        config(Boolean.class, (BinaryButtonBuild b, Boolean on) -> {
            b.signal(on);
            b.timer = duration;
        });
    }

    @Override
    public void load() {
        super.load();

        onRegion = Core.atlas.find(name + "-on");
        offRegion = Core.atlas.find(name + "-off");
    }

    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{
            region,
            topRegion,
            offRegion,
        };
    }

    public class BinaryButtonBuild extends BinaryBuild {
        public float timer;

        @Override
        public void updateSignal(int source) {
            try{super.updateSignal(source);} catch(StackOverflowError e){}
            if(!continuous){
                if((timer -= delta()) <= 0){
                    signal(false);
                    propagateSignal(true, true, true, true);
                }
            }
        }

        @Override
        public boolean configTapped(){
            if(continuous){
                configure(!signal());
                propagateSignal(true, true, true, true);
            } else {
                configure(true);
                propagateSignal(true, true, true, true);
            }
            return false;
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);
            Draw.color(Color.white, Pal.accent, signal() ? 1f : 0f);
            for(int i = 0; i < 4; i++){
                if(connections[i]) Draw.rect(connectionRegion, x, y, rotdeg() + 90 * i);
            }
            Draw.color();
            Draw.rect(signal() ? onRegion : offRegion, x, y);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, (byte)(revision + 1));

            if(revision >= 1){
                timer = read.f();
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(timer);
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.enabled){
                //controlling capability
                signal(!Mathf.zero((float)p1));
            }
        }
    }
}
