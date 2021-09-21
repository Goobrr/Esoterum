package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

public class ConfigWireI extends BinaryBlock{
    protected String[] letters = {"L", "B", "R"};

    public TextureRegion centerRegion;

    public ConfigWireI(String name){
        super(name);
        emits = true;
        configurable = true;
        rotate = true;
        inputs = new boolean[]{false, true, true, true};
        outputs = new boolean[]{true, false, false, false};

        config(Integer.class, (ConfigWireIBuild b, Integer i) -> {
            b.sides[i] = (byte)(b.sides[i] == 1 ? 0 : 1);
        });

        config(byte[].class, (ConfigWireIBuild b, byte[] sides) -> b.sides = sides.clone());
    }

    @Override
    public void load(){
        super.load();

        connectionRegion = Core.atlas.find("esoterum-connection-large");
        centerRegion = Core.atlas.find("esoterum-connection-center");
    }

    @Override
    protected TextureRegion[] icons(){
        return new TextureRegion[]{
            region,
            topRegion,
            Core.atlas.find("esoterum-connection-across")
        };
    }

    public class ConfigWireIBuild extends BinaryBuild{
        /** in order {left, front / back, right} */
        public byte[] sides = {0, 1, 0};

        @Override
        public void drawConnections(){
            for(int i = 0; i < 3; i++){
                if(!getBool(sides[i]))continue;
                Draw.color(Color.white, Pal.accent, getSignal(nb.get(i + 1), this) ? 1f : 0f);
                Draw.rect(connectionRegion, x, y, (90f + 90f * i) + rotdeg());
            }
            Draw.color(Color.white, Pal.accent, lastSignal ? 1f : 0f);
            if(active() > 1) Draw.rect(centerRegion, x, y);
            Draw.rect(connectionRegion, x, y, rotdeg());
        }

        @Override
        public void updateTile(){
            lastSignal = signal();
        }

        @Override
        public boolean signal(){
            return
                getSignal(nb.get(1), this) && getBool(sides[0]) ||
                getSignal(nb.get(2), this) && getBool(sides[1]) ||
                getSignal(nb.get(3), this) && getBool(sides[2]);
        }

        @Override
        public boolean signalFront(){
            return signal();
        }

        @Override
        public void buildConfiguration(Table table){
            super.buildConfiguration(table);
            table.setBackground(Styles.black5);
            for(int i = 0; i < 3; i++){
                int ii = i;
                TextButton button = table.button(letters[i], () -> configure(ii)).size(40).get();
                button.getStyle().checked = Tex.buttonOver;
                button.update(() -> button.setChecked(getBool(sides[ii])));
            }
        }

        public boolean getBool(byte b){
            return b == 1;
        }

        public int active(){
            return sides[0] + sides[1] + sides[2];
        }

        @Override
        public Object config(){
            return sides;
        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.b(sides);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            read.b(sides);
        }
    }
}
