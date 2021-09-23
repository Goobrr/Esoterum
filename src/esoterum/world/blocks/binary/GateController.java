package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import mindustry.graphics.Pal;
import arc.math.geom.Vec2;

public class GateController extends LogicGate{
    public String[] states = new String[]{"L", "S", "R"};
    public String[] outs = new String[]{"0", "1"};
    public TextureRegion base, connecs;
    public TextureRegion[][][] gates = new TextureRegion[2][2][2];
    public GateController(String name, boolean l, boolean b, boolean r){
        super(name, l, b, r);
        configurable = saveConfig = true;
        config(IntSeq.class, (GateControllerBuild c, IntSeq i) -> c.configs = IntSeq.with(i.items));

        config(Integer.class, (GateControllerBuild c, Integer i) -> {
            c.configs.incr(i, 1);
            if(c.configs.get(i) > ((i==3)?2:1)) c.configs.set(i, 0);
        });
    }

    @Override
    public void load(){
        super.load();
        // looks better with rounded corners
        base = Core.atlas.find("esoterum-gate-base");
        gates[0][0][0] = Core.atlas.find("esoterum-gate-top-0000");
        gates[0][0][1] = Core.atlas.find("esoterum-gate-top-0001");
        gates[0][1][0] = Core.atlas.find("esoterum-gate-top-0110");
        gates[0][1][1] = Core.atlas.find("esoterum-gate-top-0111");
        gates[1][0][0] = Core.atlas.find("esoterum-gate-top-1000");
        gates[1][0][1] = Core.atlas.find("esoterum-gate-top-1001");
        gates[1][1][0] = Core.atlas.find("esoterum-gate-top-1110");
        gates[1][1][1] = Core.atlas.find("esoterum-gate-top-1111");
        connecs = Core.atlas.find("esoterum-gate-top-con");
    }

    public class GateControllerBuild extends LogicGateBuild{
        IntSeq configs = new IntSeq(new int[]{0, 0, 0, 0});

        public Cell<Table> addConfigButton(Table table, int index){
            return table.table(t -> t.button((index == 3)?(states[configs.get(3)]):(outs[configs.get(index)]), () -> {
                configure(index);
                ((TextButton) t.getChildren().first()).setText((index == 3)?(states[configs.get(3)]):(outs[configs.get(index)]));
            }).size(40f));
        }

        @Override
        public void draw(){
            Draw.rect(base, tile.drawx(), tile.drawy());
            Draw.color(Color.white, Pal.accent, lastSignal ? 1f : 0f);
            Draw.rect(connecs, tile.drawx(), tile.drawy(),90f*rotation-configs.get(3)*90f+180f);
            Draw.rect(gates[configs.get(0)][configs.get(1)][configs.get(2)], tile.drawx(), tile.drawy(),90f*rotation);
        }

        @Override
        public void buildConfiguration(Table table){
            Table outputs = new Table();
            addConfigButton(table, 3);
            table.row();
            table.table().size(40);
            table.row();
            addConfigButton(outputs, 0);
            addConfigButton(outputs, 1);
            addConfigButton(outputs, 2);
            table.add(outputs);
        }

        @Override
        public void updateTableAlign(Table table){
            Vec2 pos = Core.input.mouseScreen(x, y);
            table.setPosition(pos.x, pos.y, Align.center);
        }

        @Override
        public boolean signal() {
            boolean a = (configs.get(3)==0)?(getSignal(nb.get(2), this)):(getSignal(nb.get(3), this));
            boolean b = (configs.get(3)==2)?(getSignal(nb.get(2), this)):(getSignal(nb.get(1), this));
            return (!(a | b) & (configs.get(0) != 0)) || ((a & b) & (configs.get(2) != 0)) || ((a ^ b) & (configs.get(1) != 0));
        }

        @Override
        public Object config() {
            return configs;
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if(revision == 1){
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
