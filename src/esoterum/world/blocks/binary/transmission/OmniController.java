package esoterum.world.blocks.binary.transmission;

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
import esoterum.world.blocks.binary.*;
import mindustry.gen.*;

// each side's behavior is configurable.
public class OmniController extends BinaryBlock{
    public String[] states = new String[]{"X", "/"};

    public TextureRegion inputRegion, outputRegion;

    public OmniController(String name){
        super(name);
        configurable = saveConfig = true;
        allOutputs = true;
        rotate = true;
        rotatedBase = false;
        emits = true;
        inputs = new boolean[]{true, true, true, true};
        outputs = new boolean[]{true, true, true, true};
        undirected = false;
        config(BoolSeq.class, (OmniControllerBuild b, BoolSeq i) -> {
            b.configs = BoolSeq.with(i.items);
            b.updateProximity();
        });

        config(Integer.class, (OmniControllerBuild b, Integer i) -> {
            b.configs.set(i, !b.configs.get(i));
            b.updateProximity();
        });
    }

    @Override
    public void load(){
        super.load();
        
        // looks better with rounded corners
        // no
        connectionRegion = Core.atlas.find("esoterum-connection-large");
    }

    public class OmniControllerBuild extends BinaryBuild{
        public boolean rotInit = false;
        /** IO configuration:
         * 0 = ignore/do nothing |
         * 1 = input |
         * 2 = output */
        public BoolSeq configs = BoolSeq.with(false, false, false, false);

        @Override
        public int inV(int dir){
            return 0;
        }

        @Override
        public void placed(){
            super.placed();
            if(!rotInit){
                for(int i = 0; i < rotation; i++){
                    configs = BoolSeq.with(
                        configs.get(3),
                        configs.get(0),
                        configs.get(1),
                        configs.get(2)
                    );
                }
                rotInit = true;
                rotation(0);
            }
            updateNeighbours();
            updateConnections();
            updateMask();
        }

        @Override
        public void drawConnections(){
            for(int i = 0; i < 4; i++){
                if(configs.get(i)){
                    Draw.color(Color.white, team.color, Mathf.num(getSignal(relnb[i], this) || getSignal(this, relnb[i])));
                    Draw.rect(connectionRegion, x, y, rotdeg() + 90 * i);
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
                TextButton b = t.button(states[configs.get(index)?1:0], () -> configure(index)).size(40f).get();
                b.update(() -> b.setText(states[configs.get(index)?1:0]));
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
            return configs.get(i);
        }

        @Override
        public boolean outputs(int i) {
            return configs.get(i);
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
                    configs.set(i, read.bool());
                }
            }
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            for(int i = 0; i < 4; i++){
                write.bool(configs.get(i));
            }
        }
    }
}
