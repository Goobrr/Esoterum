package esoterum.world.blocks.binary;

import arc.Graphics.*;
import arc.Graphics.Cursor.*;
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
    public Effect entryEffect = EsoFx.manualEntry;
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
        if(landTime < 0) landTime = entryEffect.lifetime;
    }

    public class ManualBuild extends Building{
        public boolean landed;
        public float drawRotation;

        @Override
        public void created(){
            super.created();
            if(isValid() || !landed) entryEffect.at(this, 160);
            Time.run(landTime, () -> {
                if(!isValid() || landed) return;
                landEffect.at(this);
              
                landSound.at(this, Mathf.random(0.8f, 1.2f));
                landed = true;
            });
        }

        @Override
        public void placed(){
            super.placed();
            drawRotation = Mathf.random(-50, 50);
        }

        @Override
        public void draw(){
            if(!landed) return;
            Drawf.shadow(region, x, y - 1f, drawRotation);
            Draw.rect(region, x, y, drawRotation);
        }

        @Override
        public boolean configTapped(){
            manual.show();
            return false;
        }

        @Override
        public boolean shouldShowConfigure(Player player){
            return landed;
        }

        @Override
        public Cursor getCursor(){
            return !landed ? SystemCursor.arrow : super.getCursor();
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(drawRotation);
            write.bool(landed);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if(revision >= 1){
                drawRotation = read.f();
            }
            if(revision >= 2){
                landed = read.bool();
            }
        }

        @Override
        public byte version() {
            return 2;
        }
    }
}
