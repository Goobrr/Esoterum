package esoterum.entities;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import esoterum.world.blocks.binary.*;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.*;

public class SignalSurges {

    public static class SignalSurge extends BulletType {
        {
            absorbable = false;
            damage = 0f;
            speed = 0f;
            hitSize = 1.5f;

            hittable = false;
            collides = false;
            collidesTiles = false;
            collidesAir = false;
            collidesGround = false;

            lightColor = Pal.accent;
            hitEffect = Fx.none;
            shootEffect = Fx.none;
        }
        public Building previous;

        @Override
        public void update(Bullet b) {
            b.time(0f);

            Tile t = Vars.world.tileWorld(b.x, b.y);
            if(t == null)return;

            if(t.build instanceof BinaryBlock.BinaryBuild build){
                if(build.block instanceof BinaryRouter){
                    if(spreadMove(build, b)) return;
                    eject(b);
                    return;
                }
                if(build.block instanceof BinaryWire){
                    b.x += Geometry.d4(build.rotation).x * 8;
                    b.y += Geometry.d4(build.rotation).y * 8;
                    previous = build;
                    return;
                }
                if(build instanceof BinaryNode.BinaryNodeBuild n){
                    if(n.linkedNode() == previous){
                        if(spreadMove(build, b)) return;
                        eject(b);
                        return;
                    }
                    b.x = n.linkedNode().x;
                    b.y = n.linkedNode().y;
                    previous = build;
                    return;
                }
                if(build.block.rotate){
                    b.x += Geometry.d4(build.rotation).x * 8;
                    b.y += Geometry.d4(build.rotation).y * 8;
                    previous = build;
                }else{
                    if(spreadMove(build, b)) return;
                    eject(b);
                }
            }else{
                eject(b);
            }
        }

        private boolean validate(BinaryBlock.BinaryBuild build, int i){
            return build.outputs()[i]
                && build.nb.get(i) != null
                && build.nb.get(i) != previous
                && build.nb.get(i).front() != build;
        }

        private boolean spreadMove(BinaryBlock.BinaryBuild build, Bullet b){
            for(int i = 0; i < 4; i++){
                int rot = Mathf.mod(i + build.routerCounter, 4);
                if(validate(build, rot)){
                    b.x += Geometry.d4(rot).x * 8;
                    b.y += Geometry.d4(rot).y * 8;
                    previous = build;
                    build.routerCounter++;
                    return true;
                }
            }
            return false;
        }

        private void eject(Bullet b){
            b.remove();
        }

        @Override
        public void draw(Bullet b) {
            Draw.color(Pal.accent);
            Fill.circle(b.x, b.y, 2f);

            Draw.z(Layer.effect + 1);
            Draw.blend(Blending.additive);
            Fill.light(b.x, b.y, 8, 8f, Pal.accent, Color.clear);
            Draw.blend();
        }
    }
}
