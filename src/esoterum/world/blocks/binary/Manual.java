package esoterum.world.blocks.binary;

import arc.audio.*;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.*;
import arc.util.io.Reads;
import arc.util.io.Writes;
import esoterum.content.*;
import esoterum.ui.dialogs.ManualDialog;
import mindustry.entities.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

public class Manual extends Block{
    public Effect landEffect = EsoFx.manualLand;
    public float landTime = -1f;
    public Sound landSound = Sounds.bang;

    ManualDialog manual;

    public Manual(String name){
        super(name);
        update = true;
        solid = true;
        destructible = true;
        configurable = true;
        hasShadow = false;

        buildVisibility = BuildVisibility.shown;
        category = Category.logic;
    }

    @Override
    public void init(){
        super.init();
        manual = new ManualDialog();
        if(landTime < 0) landTime = landEffect.lifetime;
    }

    public class ManualBuild extends Building{
        public boolean draw;
        public float drawRotation;

        @Override
        public void created(){
            super.created();
            landEffect.at(this);
            Time.run(landTime, () -> {
                if(!isValid() || draw) return;
                landSound.at(this, Mathf.random(0.8f, 1.2f));
                draw = true;
            });
        }

        @Override
        public void placed(){
            super.placed();
            drawRotation = Mathf.random(-50, 50);
        }

        @Override
        public void draw(){
            if(!draw) return;
            Drawf.shadow(region, x, y - 1f, drawRotation);
            Draw.rect(region, x, y, drawRotation);
        }

        @Override
        public boolean configTapped(){
            manual.show();
            return false;
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(drawRotation);
            write.bool(draw);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if(revision >= 1){
                drawRotation = read.f();
            }
            if(revision >= 2){
                draw = read.bool();
            }
        }

        @Override
        public byte version() {
            return 2;
        }
    }
}
