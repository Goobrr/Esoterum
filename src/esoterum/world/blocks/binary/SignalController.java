package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.gen.Tex;

// each side's behavior is configurable.
public class SignalController extends BinaryBlock{
    public String[] states = new String[]{"X", "I", "O"};

    public TextureRegion inputRegion, outputRegion;

    public SignalController(String name){
        super(name);
        configurable = saveConfig = true;
        allOutputs = true;
        rotate = true;
        rotatedBase = false;
        baseType = 2;
        emits = true;
        inputs = new boolean[]{true, true, true, true};
        outputs = new boolean[]{true, true, true, true};
        config(IntSeq.class, (ControllerBuild b, IntSeq i) -> b.configs = IntSeq.with(i.items));

        config(Integer.class, (ControllerBuild b, Integer i) -> {
            b.configs.incr(i, 1);
            if(b.configs.get(i) > 2) b.configs.set(i, 0);
        });
    }

    @Override
    public void load(){
        super.load();
        
        // looks better with rounded corners
        inputRegion = Core.atlas.find(name + "-in");
        outputRegion = Core.atlas.find(name + "-out");
    }

    public class ControllerBuild extends BinaryBuild{
        public boolean rotInit = false;
        /** IO configuration:
         * 0 = ignore/do nothing |
         * 1 = input |
         * 2 = output */
        public IntSeq configs = IntSeq.with(0, 0, 0, 0);

        @Override
        public void updateSignal(int source){
            try{
                super.updateSignal(source, () -> {
                    if(!rotInit){
                        for(int i = 0; i < rotation; i++){
                            configs = IntSeq.with(
                                configs.get(3),
                                configs.get(0),
                                configs.get(1),
                                configs.get(2)
                            );
                        }
                        rotInit = true;
                        rotation(0);
                    }
                    signal[4] = (getSignal(nb.get(0), this) && configs.get(0) == 1)
                        ||  (getSignal(nb.get(1), this) && configs.get(1) == 1)
                        ||  (getSignal(nb.get(2), this) && configs.get(2) == 1)
                        ||  (getSignal(nb.get(3), this) && configs.get(3) == 1);
                    if(signal() != signal[4]){
                        signal(false);
                        signal[0] = signal[4] && configs.get(0) == 2;
                        signal[1] = signal[4] && configs.get(1) == 2;
                        signal[2] = signal[4] && configs.get(2) == 2;
                        signal[3] = signal[4] && configs.get(3) == 2;
                        return new boolean [] {configs.get(0) == 2 && source != 0, 
                            configs.get(1) == 2 && source != 1, 
                            configs.get(2) == 2 && source != 2, 
                            configs.get(3) == 2 && source != 3};
                    } else {
                        return new boolean[4];
                    }
                });
            }catch(Exception ignored){}
        }

        @Override
        public void drawConnections(){
            for(int i = 0; i < 4; i++){
                int c = configs.get(i);
                if(c == 0) continue;
                if(c == 1){
                    Draw.color(Color.white, team.color, Mathf.num(getSignal(nb.get(i), this)));
                    Draw.rect(inputRegion, x, y, i * 90f);
                }else{
                    Draw.color(Color.white, team.color, Mathf.num(signal()));
                    Draw.rect(outputRegion, x, y, i * 90f);
                }
            }
        }

        // i don't know how to arrange the buttons, so i just did this
        @Override
        public void buildConfiguration(Table table){
            table.table(Tex.clear, t -> {
                t.table().size(40);
                addConfigButton(t, 1).align(Align.center);
                t.row();
                addConfigButton(t, 2);
                t.table().size(40);
                addConfigButton(t, 0);
                t.row();
                t.table().size(40);
                addConfigButton(t, 3).align(Align.center);
            });
        }

        public Cell<Table> addConfigButton(Table table, int index){
            return table.table(t -> {
                TextButton b = t.button(states[configs.get(index)], () -> {
                    configure(index);
                    updateProximity();
                }).size(40f).get();

                b.update(() -> b.setText(states[configs.get(index)]));
            }).size(40f);
        }

        @Override
        public void updateTableAlign(Table table){
            Vec2 pos = Core.input.mouseScreen(x, y);
            table.setPosition(pos.x, pos.y, Align.center);
        }

        @Override
        public Object config() {
            return configs;
        }

        @Override
        public boolean inputs(int i) {
            return configs.get(i) == 1;
        }

        @Override
        public boolean outputs(int i) {
            return configs.get(i) == 2;
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, (byte)(revision + 1));

            if(revision >= 1){
                for(int i = 0; i < 4; i++){
                    configs.set(i, read.i());
                }
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            for(int i = 0; i < 4; i++){
                write.i(configs.get(i));
            }
        }
    }
}
