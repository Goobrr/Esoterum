package esoterum.world.blocks.binary;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;

public class LogicGate extends BinaryBlock{
    public Boolf<boolean[]> operation;
    public boolean single;

    public LogicGate(String name){
        super(name);
        inputs = new boolean[]{false, true, true, true};
        outputs = new boolean[]{true, false, false, false};
        emits = true;
        rotate = true;
        rotatedBase = true;
        drawArrow = true;
        configurable = saveConfig = true;
        baseType = 0;

        operation = e -> false;

        config(IntSeq.class, (LogicGateBuild b, IntSeq i) -> b.configs = IntSeq.with(i.items));

        config(Integer.class, (LogicGateBuild b, Integer i) -> {
            if(single){
                b.configs.set(0, i);
            }else{
                b.configs.set(0, b.configs.get(1));
                b.configs.set(1, i);
            }
            b.nextConfig--;
            if(b.nextConfig < 1) b.nextConfig = 3;
        });
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find("esoterum-gate-base");
    }

    public class LogicGateBuild extends BinaryBuild{
        public IntSeq configs = single ? IntSeq.with(2) : IntSeq.with(3, 2);
        public int nextConfig = 1;

        @Override
        public void updateSignal(int source){
            try{
                super.updateSignal(source, () -> {
                    signal[4] = operation.get(new boolean[]{
                        getSignal(nb.get(configs.first()), this),
                        getSignal(nb.get(configs.get(single ? 0 : 1)), this),
                    });
                    if(signal[0] != signal[4]){
                        signal[0] = signal[4];
                        return new boolean[] {true, false, false, false};
                    } else{
                        return new boolean[4];
                    }
                });
                
            } catch(Exception e){}
        }

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.rotate, () -> configure(nextConfig)).size(40f).tooltip("Rotate Input" + (single ? "" : "s"));
        }

        @Override
        public Object config() {
            return configs;
        }

        @Override
        public void drawConnections(){
            for(int i = 1; i < 4; i++){
                if(!configs.contains(i)) continue;
                Draw.color(Color.white, team.color, Mathf.num(getSignal(nb.get(i), this)));
                Draw.rect(connectionRegion, x, y, rotdeg() + 90 * i);
            }
            Draw.color(Color.white, team.color, Mathf.num(signal()));
            Draw.rect(connectionRegion, x, y, rotdeg());
        }

        @Override
        public boolean inputs(int dir){
            return dir == configs.first() || single ? false : dir == configs.get(1);
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.i(nextConfig);
            write.i(configs.first());
            if(!single) write.i(configs.get(1));
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, (byte)(revision + 1));

            if(revision >= 1){
                nextConfig = read.i();
                if(single){
                    configs = IntSeq.with(read.i());
                }else{
                    configs = IntSeq.with(read.i(), read.i());
                }
            }
        }

        @Override
        public byte version(){
            return 1;
        }
    }
}
