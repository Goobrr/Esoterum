package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.logic.LAccess;

public class Toggler extends BinaryBlock{
    public TextureRegion[] states = new TextureRegion[2];

    public Toggler(String name){
        super(name);
        rotate = true;
        drawRot = false;
        rotatedBase = true;
        baseType = 1;
        emits = true;
        outputs = new boolean[]{true, false, false, false};
        inputs = new boolean[]{false, true, true, true};
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
        public void updateSignal(int source){
            try {
                super.updateSignal(source, () -> {
                    signal[4] = getSignal(nb.get(1), this) | getSignal(nb.get(2), this) | getSignal(nb.get(3), this);
                    boolean[] out = new boolean[4];
                    if(signal[0] != signal[4]){
                        signal[0] = signal[4];
                        out  = new boolean[]{true, false, false, false};
                    }
                    if(front() != null){
                        front().control(LAccess.enabled, Mathf.num(signal()), 0d, 0d, 0d);
                    }
                    return out;
                });
                
            } catch(Exception ignored){}
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
