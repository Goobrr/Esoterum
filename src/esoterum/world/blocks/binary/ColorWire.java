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
import esoterum.world.blocks.bundled.BundledWire;
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
            b.colour = new Color().rgba8888(c);
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
        public Seq<BundledWire.BundledWireBuild> nbb = Seq.with(null, null, null, null);
        public BundledWire.BundledWireBuild checkType2(Building b){
            if(b instanceof BundledWire.BundledWireBuild bb)return bb;
            return null;
        }
        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();

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
            updateConnections();
        }
        @Override
        public void updateConnections(){
            for(int i = 0; i < 4; i++){
                connections[i] = connectionCheck(nb.get(i), this) || connectionCheck(nbb.get(i), this);
            }
        }
        @Override
        public void drawConnections(){
            if(this.colour==null) this.colour = Color.valueOf("000000");
            Draw.color(Color.white, Pal.accent, lastSignal ? 1f : 0f);
            for(int i = 0; i < 4; i++){
                if(connections[i]){
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
    
            return switch(EsoUtil.relativeDirection(from, to)){
                case 0 -> (from.signalFront() & (1 << this.channel))==1;
                case 1 -> (from.signalLeft() & (1 << this.channel))==1;
                case 2 -> (from.signalBack() & (1 << this.channel))==1;
                case 3 -> (from.signalRight() & (1 << this.channel))==1;
                default -> false;
            };
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
                    Color color0 = colors[i];
                    int j = i;
                    ImageButton button = table0.button(Tex.whiteui, Styles.clearTogglei, 34, () -> {
                        configure(color0.rgba8888());
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
            return this.colour.rgba8888();
        }
        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            int i = read.i();
            if(i == -1) this.colour = null;
            else this.colour = new Color().rgba8888(i);
            this.channel = read.i();
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.i((this.colour==null)?-1:this.colour.rgba8888());
            write.i(this.channel);
        }

        @Override
        public byte version() {
            return 1;
        }
    }
}
