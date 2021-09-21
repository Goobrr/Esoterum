package esoterum.world.blocks.binary;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;

public class BinaryWire extends BinaryBlock{
    public BinaryWire(String name){
        super(name);
        outputs = new boolean[]{true, false, false, false};
        inputs = new boolean[]{false, true, true, true};
        emits = true;
        rotate = true;
        drawArrow = true;
    }

    @Override
    public void load() {
        super.load();
        connectionRegion = Core.atlas.find("esoterum-connection-large");
        topRegion = Core.atlas.find("esoterum-wire-top");
    }


    @Override
    public boolean canReplace(Block other) {
        if(other.alwaysReplace) return true;
        return (other != this || rotate) && other instanceof BinaryBlock && size == other.size;
    }

    public class BinaryWireBuild extends BinaryBuild {
        @Override
        public void updateTile() {
            super.updateTile();
            lastSignal = nextSignal | getSignal(nb.get(2), this);
            nextSignal = signal();
        }

        // bad, bad, bad, bad, bad, bad, bad, bad, bad, bad, bad
        // TODO fix layering without DOING THIS SHIT
        @Override
        public void drawSelect() {
            BinaryBuild b;
            for(int i = 0; i < 4; i++){
                if(connections[i]){
                    b = nb.get(i);

                    Draw.z(Layer.overlayUI);
                    Lines.stroke(3f);
                    Draw.color(Pal.gray);
                    Lines.line(x, y, b.x, b.y);
                }
            }

            for(int i = 0; i < 4; i++){
                if(outputs()[i] && connections[i]){
                    b = nb.get(i);
                    Draw.z(Layer.overlayUI + 1);
                    Drawf.arrow(x, y, b.x, b.y, 2f, 2f, lastSignal ? Pal.accent : Color.white);
                }
            }

            for (int i = 0; i < 4; i++){
                if(connections[i]) {
                    b = nb.get(i);
                    Draw.z(Layer.overlayUI + 3);
                    Lines.stroke(1f);
                    Draw.color((outputs()[i] ? lastSignal : getSignal(b, this)) ? Pal.accent : Color.white);
                    Lines.line(x, y, b.x, b.y);

                    Draw.reset();
                }
            }
        }

        @Override
        public boolean signal() {
            return getSignal(nb.get(1), this) | getSignal(nb.get(3), this);
        }

        public boolean signalFront(){
            return (nb.get(2) != null ?
                connectionCheck(nb.get(2), this) || !nb.get(2).block.rotate ?
                    getSignal(nb.get(2), this) :
                    nextSignal
                : nextSignal )

                | nextSignal;
        }
    }
}
