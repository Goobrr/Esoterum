package esoterum.world.blocks.binary;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class LogicGate extends BinaryBlock{
    public Boolf<boolean[]> operation;
    public boolean single;

    public LogicGate(String name){
        super(name);
        inputs = new boolean[]{false, true, true, true};
        outputs = new boolean[]{true, false, false, false};
        emits = true;
        rotate = true;
        drawArrow = true;
        configurable = saveConfig = true;

        operation = e -> false;

        config(IntSeq.class, (LogicGateBuild b, IntSeq i) -> b.configs = IntSeq.with(i.items));

        config(Integer.class, (LogicGateBuild b, Integer i) -> {
            b.configs.removeIndex(0);
            b.configs.add(i);
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
        public void updateTile(){
            super.updateTile();
            lastSignal = signal();
        }

        @Override
        public void buildConfiguration(Table table){
            table.button(Icon.rotate, () -> configure(nextConfig)).size(40f);
        }

        @Override
        public Object config() {
            return configs;
        }

        @Override
        public boolean signal(){ //Assumes logic gates only have 2 inputs.
            return operation.get(new boolean[]{
                getSignal(nb.get(configs.first()), this),
                getSignal(nb.get(configs.peek()), this),
            });
        }

        @Override
        public boolean signalFront() {
            return configs.first() == 2 ? signal() : lastSignal;
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);

            drawConnections();
            Draw.color(Color.white, Pal.accent, lastSignal ? 1f : 0f);
            Draw.rect(topRegion, x, y, rotate ? rotdeg() : 0f);
        }

        @Override
        public void drawConnections(){
            for(int i = 1; i < 4; i++){
                if(!configs.contains(i)) continue;
                Draw.color(Color.white, Pal.accent, getSignal(nb.get(i), this) ? 1f : 0f);
                Draw.rect(connectionRegion, x, y, rotdeg() + 90 * i);
            }
            Draw.color(Color.white, Pal.accent, lastSignal ? 1f : 0f);
            Draw.rect(connectionRegion, x, y, rotdeg());
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.i(nextConfig);
            write.i(configs.first());
            if(!single) write.i(configs.peek());
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

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
