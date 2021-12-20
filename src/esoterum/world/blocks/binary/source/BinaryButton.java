package esoterum.world.blocks.binary.source;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.io.*;
import esoterum.world.blocks.binary.basis.*;
import mindustry.logic.*;

public class BinaryButton extends BinarySource{
    /** Whether the button emits continuously (like a switch). */
    public boolean continuous;
    /** Buttons will have a pulse length of 60 ticks by default. */
    public float duration = 60;

    public TextureRegion onRegion, offRegion;

    public BinaryButton(String name, boolean cont){
        super(name);
        configurable = true;
        continuous = cont;
        baseHighlight = "gold";
        config(Boolean.class, (BinaryButtonBuild b, Boolean on) -> {
            b.active = on;
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
            baseRegion,
            topRegion,
            offRegion,
        };
    }

    public class BinaryButtonBuild extends BinarySourceBuild{
        public boolean active;
        public float timer;

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.enabled) configure(!Mathf.zero((float)p1));
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(!continuous && (timer -= delta()) <= 0) active = false;
        }

        @Override
        public boolean configTapped(){
            if(continuous){
                configure(!active);
            }else{
                configure(true);
            }
            return false;
        }

        @Override
        public void draw() {
            drawBase();
            Draw.color(Color.white, team.color, Mathf.num(active));
            drawConnections();
            Draw.color();
            Draw.rect(active ? onRegion : offRegion, x, y);
        }

        @Override
        public boolean isActive(SignalGraph graph){
            return active;
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(timer);
            write.bool(active);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, (byte)(revision + 1));

            if(revision >= 1){
                timer = read.f();
                if(revision >= 2){
                    active = read.bool();
                }
            }
        }

        @Override
        public byte version() {
            return 2;
        }
    }
}