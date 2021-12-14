package esoterum.world.blocks.binary.interaction;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import esoterum.world.blocks.binary.basis.*;
import mindustry.logic.*;

public class Toggler extends BinaryBlock{
    public TextureRegion[] states = new TextureRegion[2];

    public Toggler(String name){
        super(name);
        rotate = true;
        drawRot = false;
        rotatedBase = true;
        baseHighlight = "gold";
        emits = true;
        outputs = new boolean[]{true, false, false, false};
        inputs = new boolean[]{false, true, true, true};
        propagates = true;
    }

    @Override
    public void load(){
        super.load();
        states[0] = Core.atlas.find(name + "-off");
        states[1] = Core.atlas.find(name + "-on");
        topRegion = states[0];
    }

    public class TogglerBuild extends BinaryBuild{
        @Override
        public boolean updateSignal(){
            signal[5] = signal[0];
            signal[0] = getSignal(relnb[1], this) | getSignal(relnb[2], this) | getSignal(relnb[3], this);
            if(front() != null) front().control(LAccess.enabled, Mathf.num(signal()), 0d, 0d, 0d);
            return signal[5] != signal[0];
        }

        @Override
        public void draw(){
            drawBase();
            drawConnections();
            Draw.color();
            Draw.rect(states[Mathf.num(signal())], x, y, (rotate && drawRot) ? rotdeg() : 0f);
        }

        @Override
        public void drawSelect(){
            super.drawSelect();
            if(front() != null){
                Draw.color(signal() ? team.color : Color.white);
                Lines.square(front().x, front().y, 4 * front().block.size);
            }
        }
    }
}