package esoterum.world.blocks.defense.beam;

import arc.Core;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import esoterum.util.EsoUtil;
import esoterum.world.blocks.binary.BinaryBlock;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.gen.Icon;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;

public class BeamBlock extends BinaryBlock {
    public float beamLength = 40f;
    public float beamDamage = 15f;
    public boolean acceptsBeam = false;
    public BeamBlock(String name){
        super(name);
        configurable = true;

        config(Float.class, (BeamBuild b, Float f) -> {
            b.beamRotation = f;
        });
    }

    @Override
    public void load() {
        super.load();
        region = Core.atlas.find(size == 1 ? "esoterum-node-base" : "esoterum-base-" + size);
    }

    public void unitHit(Unit u){
        u.damage(beamDamage * Time.delta);
    }

    public class BeamBuild extends BinaryBuild {
        public float beamRotation = 0f;
        public float beamDrawLength = 0f;

        public boolean drawBeam = false;

        public Vec2 tr = new Vec2();
        Tile hit;

        @Override
        public void draw() {
            Draw.rect(region, x, y);
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            Drawf.dashCircle(x, y, beamLength, Pal.lancerLaser);
            Tmp.v2.setZero().trns(beamRotation, beamLength / 2);
            Drawf.dashLine(Pal.lancerLaser, x, y, x + Tmp.v2.x, y + Tmp.v2.y);
        }

        // config
        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.leftSmall, () -> {
                beamRotation += 15f;
                beamRotation = Mathf.mod(beamRotation, 360f);
                configure(beamRotation);
            });
            table.button(Icon.rightSmall, () -> {
                beamRotation -= 15f;
                beamRotation = Mathf.mod(beamRotation, 360f);
                configure(beamRotation);
            });
        }

        @Override
        public void drawConfigure() {
            super.drawConfigure();
            Drawf.dashCircle(x, y, beamLength, Pal.lancerLaser);
            Tmp.v2.setZero().trns(beamRotation, beamLength / 2);
            Drawf.dashLine(Pal.lancerLaser, x, y, x + Tmp.v2.x, y + Tmp.v2.y);
        }

        @Override
        public Object config() {
            return beamRotation;
        }

        // beam stuff
        public float beam(float rot, boolean damage){
            tr.set(0f, 0f).trnsExact(rot, beamLength);

            hit = null;

            boolean found = Vars.world.raycast(tileX(), tileY(), World.toTile(x + tr.x), World.toTile(y + tr.y),
            (x, y) -> (hit = Vars.world.tile(x, y)) != null && hit.build != null && hit.build != this && (hit.build.checkSolid() || hit.block().solid)
            );

            float length = found ? Mathf.dst(x, y, hit.worldx(), hit.worldy()) : beamLength;

            if(found && hit.build != null && hit.build instanceof BeamBuild b && b.acceptsBeam()){
                b.signal(true);
            }

            if(damage){
                EsoUtil.linecastUnits(x, y, rot, length, BeamBlock.this::unitHit);
            }

            return length;
        }

        public void updateBeam(){
            beamDrawLength = beam(beamRotation, true);
            drawBeam = true;
        }

        public void drawBeam(float rot, float length){
            Lines.stroke(1.5f);
            Draw.color(Pal.lancerLaser);
            Draw.z(Layer.effect);


            Lines.lineAngle(x, y, rot, length);

            Tmp.v2.trns(rot, length);
            Fill.circle(x + Tmp.v2.x, y + Tmp.v2.y, 2);
        }

        public boolean acceptsBeam(){
            return acceptsBeam;
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if(revision >= 2) beamRotation = read.f();
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(beamRotation);
        }

        @Override
        public byte version() {
            return 2;
        }
    }
}
