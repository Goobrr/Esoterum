package esoterum.world.blocks.defense;

import arc.Core;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.ui.layout.Table;
import arc.util.Tmp;
import arc.util.io.*;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.entities.Units;
import mindustry.game.Team;
import mindustry.gen.Icon;
import mindustry.graphics.*;
import mindustry.logic.LAccess;
import mindustry.type.Category;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.PowerTurret;

public class SentryTurret extends PowerTurret {
    public float detectionCone = 45f;
    public float swayScl = 15f;
    public float swayMag = 0f;
    public boolean targetBuildings = false;

    public SentryTurret(String name) {
        super(name);
        configurable = true;
        consumesPower = true;
        targetInterval = 5;

        category = Category.turret;

        config(Float.class, (SentryBuild t, Float f) -> t.startAngle = f);
    }

    public void onDetect() {

    }

    @Override
    public void load() {
        super.load();

        baseRegion = Core.atlas.find(size == 1 ? "esoterum-node-base" : "esoterum-base-" + size);
    }

    public class SentryBuild extends PowerTurretBuild {
        public float startAngle = 0f;
        public boolean wasLocked = false;
        public Tile obstacle;
        public float maxRange = 0f;

        @Override
        public boolean canControl() {
            return false;
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4) {
            if (type == LAccess.shoot) return;
            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void control(LAccess type, Object p1, double p2, double p3, double p4) {
            if (type == LAccess.shootp) return;
            super.control(type, p1, p2, p3, p4);
        }

        // config
        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.rotate, () -> {
                startAngle += 45f;
                startAngle = Mathf.mod(startAngle, 360f);
                configure(startAngle);
            });
        }

        @Override
        public void drawConfigure() {
            super.drawConfigure();
            Draw.color(Pal.gray);
            Lines.stroke(3f);
            drawDetectionCone();
            Draw.color(Pal.accent);
            Lines.stroke(1.2f);
            drawDetectionCone();
        }

        @Override
        public Object config() {
            return startAngle;
        }

        // turret stuff
        @Override
        public void draw() {
            super.draw();
            if (!enabled || !consValid()) return;
            Draw.z(Layer.turret - 1);

            Draw.blend(Blending.additive);
            Draw.color(obstacle != null ? Color.red : Pal.accent);
            Lines.stroke(1);
            if (target == null) {
                Lines.lineAngle(x, y, size * 4, rotation, maxRange - size * 4);
            }
            Draw.blend();
        }

        public void drawDetectionCone() {
            Lines.lineAngle(x, y, size * 4f + 1f, startAngle + swayMag, range - (size * 4f + 1f));
            Lines.lineAngle(x, y, size * 4f + 1f, startAngle - swayMag, range - (size * 4f + 1f));
            //Lines.swirl(x, y, range, ((swayMag * 2f) / 360f) + 0.02f, startAngle - swayMag);
        }

        @Override
        public void updateTile() {
            maxRange = getObstacle(rotation);
            super.updateTile();
            if ((target != null) && !wasLocked) onDetect();
            wasLocked = (target != null);
            if (target == null) {
                turnToTarget(startAngle + Mathf.sin(swayScl, swayMag));
            }
        }

        @Override
        protected boolean validateTarget() {
            return !Units.invalidateTarget(target, canHeal() ? Team.derelict : team, x, y, range) || isControlled() || logicControlled();
        }

        @Override
        protected void findTarget() {
            if (target != null) return;
            if (targetAir && !targetGround) {
                target = Units.bestEnemy(team, x, y, range, e -> !e.dead() && !e.isGrounded(), unitSort);
            } else {
                target = Units.bestTarget(team, x, y, range, e -> !e.dead() && (e.isGrounded() || targetAir) && (!e.isGrounded() || targetGround), b -> targetBuildings, unitSort);
            }

            if (
                target != null && (!Angles.within(angleTo(target), rotation, detectionCone / 2)
                    || getObstacle(rotation) < dst(target))
            ) target = null;
        }

        public float getObstacle(float rot) {
            Tmp.v2.set(0f, 0f).trnsExact(rot, range);

            obstacle = null;

            boolean found = Vars.world.raycast(tileX(), tileY(), World.toTile(x + Tmp.v2.x), World.toTile(y + Tmp.v2.y),
                (x, y) -> (obstacle = Vars.world.tile(x, y)) != null && (obstacle.build != null) && obstacle.build != this && (obstacle.build.checkSolid() || obstacle.block().solid));

            return found && obstacle != null ? Mathf.dst(x, y, obstacle.worldx(), obstacle.worldy()) : range;
        }

        // saving
        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);

            if (revision >= 1) startAngle = read.f();
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.f(startAngle);
        }

        @Override
        public byte version() {
            return 1;
        }
    }
}
