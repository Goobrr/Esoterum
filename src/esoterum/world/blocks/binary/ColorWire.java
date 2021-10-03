package esoterum.world.blocks.binary;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
import esoterum.util.*;
import esoterum.world.blocks.bundled.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.ui.*;

public class ColorWire extends BinaryWire{
    public Color[] colors = {
        Color.valueOf("825432"),
        Color.valueOf("1d1c21"), 
        Color.valueOf("474f52"), 
        Color.valueOf("9c9d97"), 
        Color.valueOf("ffffff"), 
        Color.valueOf("ffd83d"), 
        Color.valueOf("f9801d"), 
        Color.valueOf("b02e26"), 
        Color.valueOf("f38caa"), 
        Color.valueOf("c64fbd"), 
        Color.valueOf("8932b7"), 
        Color.valueOf("3c44a9"), 
        Color.valueOf("3ab3da"), 
        Color.valueOf("169c9d"), 
        Color.valueOf("5d7c15"), 
        Color.valueOf("80c71f")};
    private TextureRegion colorRegion, colorConnectionRegion;
    public ColorWire(String name){
        super(name);
        configurable = true;
        config(Integer.class, (ColorWireBuild b, Integer c) -> {
            b.colour = colors[c%16];
            b.channel = c;
        });
    }
    @Override
    public void load(){
        super.load();
        colorRegion = Core.atlas.find("esoterum-wire-color");
        colorConnectionRegion = Core.atlas.find("esoterum-connection-color");
    }
    public class ColorWireBuild extends BinaryWireBuild {
        public @Nullable Color colour = null;
        public int channel = 16;
        public Seq<BundledWire.BundledWireBuild> nbb = new Seq<>(4);
        public boolean[] connections2 = {false, false, false, false};

        @Override
        public void onProximityUpdate(){
            

            // update connected builds only when necessary
            nb.clear();
            nb.add(
                checkType(front()),
                checkType(left()),
                checkType(back()),
                checkType(right())
            );
            nbb.clear();
            nbb.add(
                checkType2(front()),
                checkType2(left()),
                checkType2(back()),
                checkType2(right())
            );
            super.onProximityUpdate();
            updateConnections();
        }
        @Override
        public void updateConnections(){
            for(int i = 0; i < 4; i++){
                connections[i] = connectionCheck(nb.get(i), this);
                connections2[i] = connectionCheck(nbb.get(i), this);
            }
        }
        @Override
        public void drawConnections(){
            if(this.colour==null) this.colour = Color.valueOf("000000");
            Draw.color(Color.white, Pal.accent, lastSignal ? 1f : 0f);
            for(int i = 0; i < 4; i++){
                if(connections[i] || connections2[i]){
                    Draw.color(Color.white, Pal.accent, lastSignal ? 1f : 0f);
                    Draw.z(Draw.z()+0.1f);
                    Draw.rect(connectionRegion, x, y, rotdeg() + 90 * i);
                    Draw.color(Color.white, this.colour, 1f);
                    Draw.z(Draw.z()-0.1f);
                    Draw.rect(colorConnectionRegion, x, y, rotdeg() + 90 * i);
                }
            }
            if(this.colour==Color.valueOf("000000")) this.colour = null;
        }
        @Override
        public void draw(){
            if(this.colour==null) this.colour = Color.valueOf("000000");
            super.draw();
            Draw.color(Color.white, this.colour, 1f);
            Draw.rect(colorRegion, x, y, (rotate && drawRot) ? rotdeg() : 0f);
            if(this.colour==Color.valueOf("000000")) this.colour = null;
        }
        @Override
        public boolean connectionCheck(Building from, BinaryBlock.BinaryBuild to){
            if(from instanceof BundledWire.BundledWireBuild b){
                return (b.outputs()[EsoUtil.relativeDirection(b, to)] & to.inputs()[EsoUtil.relativeDirection(to, b)]
                    || to.outputs()[EsoUtil.relativeDirection(to, b)] & b.inputs()[EsoUtil.relativeDirection(b, to)]);
            } if(from instanceof ColorWire.ColorWireBuild b && to instanceof ColorWire.ColorWireBuild bb){
                return (b.outputs()[EsoUtil.relativeDirection(b, to)] & to.inputs()[EsoUtil.relativeDirection(to, b)]
                    || to.outputs()[EsoUtil.relativeDirection(to, b)] & b.inputs()[EsoUtil.relativeDirection(b, to)]) && (b.colour == bb.colour);
            } else if(from instanceof BinaryBlock.BinaryBuild b){
                return b.outputs()[EsoUtil.relativeDirection(b, to)] & to.inputs()[EsoUtil.relativeDirection(to, b)]
                    || to.outputs()[EsoUtil.relativeDirection(to, b)] & b.inputs()[EsoUtil.relativeDirection(b, to)];
            }
            return false;
        }
        public boolean getSignalRelativeTo(BundledWire.BundledWireBuild from, BinaryBlock.BinaryBuild to){
            if(!from.emits())return false;
            int a = switch(EsoUtil.relativeDirection(from, to)){
                case 0 -> ((from.signalFront() >> this.channel) & 1);
                case 1 -> ((from.signalLeft() >> this.channel) & 1);
                case 2 -> ((from.signalBack() >> this.channel) & 1);
                case 3 -> ((from.signalRight() >> this.channel) & 1);
                default -> 0;
            };
            return a==1;
        }
        @Override
        public boolean getSignal(Building from, BinaryBlock.BinaryBuild to){
            if(from instanceof BundledWire.BundledWireBuild b){
                return getSignalRelativeTo(b, to);
            } else if(from instanceof ColorWire.ColorWireBuild b && to instanceof ColorWire.ColorWireBuild bb){
                return getSignalRelativeTo(b, to) && (b.colour == bb.colour);
            } else if(from instanceof BinaryBlock.BinaryBuild b){
                return getSignalRelativeTo(b, to);
            }
            return false;
        }
        @Override
        public void buildConfiguration(Table table){
            table.table(table0 -> {
                ButtonGroup<ImageButton> group = new ButtonGroup<>();
                group.setMinCheckCount(1);
                group.setMaxCheckCount(1);
                for(int i = 0; i < colors.length; i++){
                    Color color0 = colors[i%16];
                    int j = i;
                    ImageButton button = table0.button(Tex.whiteui, Styles.clearTogglei, 34, () -> {
                        configure(j);
                        this.channel = j;
                        this.onProximityUpdate();
                    }).group(group).size(48).get();
                    button.setChecked(this.colour.equals(color0));
                    button.getStyle().imageUpColor = color0;
                    
                    if(i % 4 == 3){
                        table0.row();
                    }
                }
            });
        }
        @Override
        public Integer config(){
            return this.channel;
        }
        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            this.channel = read.i();
            this.colour = colors[this.channel%16];
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i(this.channel);
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public boolean signal() {
            return getSignal(nb.get(1), this) | getSignal(nb.get(3), this) | getSignal(nbb.get(1), this) | getSignal(nbb.get(3), this);
        }
        @Override
        public boolean signalFront(){
            return ((nb.get(2) != null ?
                nb.get(2).rotation == rotation || !nb.get(2).block.rotate || nb.get(2).allOutputs() ?
                    getSignal(nb.get(2), this) :
                    nextSignal
                : nextSignal )

                | nextSignal) |
                ((nbb.get(2) != null ?
                true ?
                    getSignal(nbb.get(2), this) :
                    nextSignal
                : nextSignal )

                | nextSignal);
        }
    }
}
