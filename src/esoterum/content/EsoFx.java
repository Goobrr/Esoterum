package esoterum.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.Tmp;
import mindustry.entities.*;
import mindustry.graphics.*;

import static arc.graphics.g2d.Draw.*;

public class EsoFx{
    private static final Vec2 tr = new Vec2();

    public static Effect

    manualLand = new Effect(90f, 500f, e -> {
        z(Layer.effect + 1);

        blend(Blending.additive);
        e.scaled(60, f -> {
            color(Pal.lancerLaser);
            alpha(f.fout(Interp.pow10In));
            Fill.circle(e.x, e.y, 180 * f.fin(Interp.pow10Out));
        });
        blend();

        color(Pal.lancerLaser, Pal.darkerGray, e.fin());
        Angles.randLenVectors(e.id, 60, 150 * e.fin(Interp.pow5Out), e.rotation, 80, (x, y) -> {
            Fill.circle(e.x + x + 25 * e.fin(Interp.pow2In), e.y + y, 15 * e.fout(Interp.pow2Out));
        });

        color(Pal.darkishGray, Pal.darkerGray, e.fin());
        alpha(0.5f);
        Angles.randLenVectors(e.id + 1, 45, 60 * e.fin(Interp.pow5Out), e.rotation, 360, (x, y) -> {
            Fill.circle(e.x + x + 25 * e.fin(Interp.pow2In), e.y + y, 30 * e.fout(Interp.pow2Out));
        });

        z(Layer.effect);
        color(Pal.lancerLaser);
        Lines.stroke(15 * e.fout(Interp.pow10In));
        Lines.circle(e.x, e.y, 120 * e.fin(Interp.pow10Out));
    }),

    manualEntry = new Effect(20, 1500f, e -> {
        float angle = 160f;
        tr.trns(angle, 1000 * e.fout());

        z(Layer.bullet - 1f);
        rect("esoterum-manual", e.x + tr.x, e.y + tr.y, e.rotation);

        z(Layer.effect);
        Drawf.tri(e.x + tr.x, e.y + tr.y, 5f, 75f, angle);
        Drawf.tri(e.x + tr.x, e.y + tr.y, 5f, 35f, angle + 90);
        Drawf.tri(e.x + tr.x, e.y + tr.y, 5f, 35f, angle - 90);

        z(Layer.effect + 1);
        blend(Blending.additive);
        Fill.light(e.x + tr.x, e.y + tr.y, 20, 60, Pal.lancerLaser, Color.clear);
        blend();
    }),

    sentryShoot = new Effect(10, e -> {
        Tmp.c1.set(Pal.accent);
        e.scaled(e.lifetime / 2f, s -> {
            tr.trns(e.rotation, 10f * e.fin());
            Draw.color(Pal.accent);
            Drawf.tri(e.x + tr.x, e.y + tr.y, 4f * s.fout(), 10f + 5f * s.fin(), e.rotation);
            Drawf.tri(e.x + tr.x, e.y + tr.y, 4f * s.fout(), 5f * s.fin(), e.rotation + 180f);

            Draw.z(Layer.effect + 1f);
            Draw.blend(Blending.additive);
            Tmp.c1.a = s.fout();
            Fill.light(e.x + tr.x, e.y + tr.y, 10, 4f, Tmp.c1, Color.clear);
        });

        Tmp.c1.a = e.fout();
        Fill.light(e.x, e.y, 10, 4, Tmp.c1, Color.clear);
        Draw.blend();
    }),

    notePlay = new Effect(20, e -> {
        Draw.color(Pal.accent);
        Lines.stroke(3f * e.fout(Interp.pow5Out));
        Lines.square(e.x, e.y, 4 * e.fin(Interp.pow5Out), 45f);
    });
}
