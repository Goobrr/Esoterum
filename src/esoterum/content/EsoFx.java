package esoterum.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.Tmp;
import mindustry.entities.*;
import mindustry.graphics.*;

public class EsoFx{
    public static Effect

    manualLand = new Effect(90f, e -> {
        Draw.z(Layer.effect + 1);

        Draw.blend(Blending.additive);
        e.scaled(60, f -> {
            Draw.color(Pal.lancerLaser);
            Draw.alpha(f.fout(Interp.pow10In));
            Fill.circle(e.x, e.y, 180 * f.fin(Interp.pow10Out));
        });
        Draw.blend();

        Draw.color(Pal.lancerLaser, Pal.darkerGray, e.fin());
        Angles.randLenVectors(e.id, 60, 150 * e.fin(Interp.pow5Out), e.rotation, 80, (x, y) -> {
            Fill.circle(e.x + x + 25 * e.fin(Interp.pow2In), e.y + y, 15 * e.fout(Interp.pow2Out));
        });

        Draw.color(Pal.darkishGray, Pal.darkerGray, e.fin());
        Draw.alpha(0.5f);
        Angles.randLenVectors(e.id + 1, 45, 60 * e.fin(Interp.pow5Out), e.rotation, 360, (x, y) -> {
            Fill.circle(e.x + x + 25 * e.fin(Interp.pow2In), e.y + y, 30 * e.fout(Interp.pow2Out));
        });

        Draw.z(Layer.effect);
        Draw.color(Pal.lancerLaser);
        Lines.stroke(15 * e.fout(Interp.pow10In));
        Lines.circle(e.x, e.y, 120 * e.fin(Interp.pow10Out));
    }),

    manualEntry = new Effect(20, e -> {
        Tmp.v2.trns(e.rotation, 1000 * e.fout());

        Drawf.tri(e.x + Tmp.v2.x, e.y + Tmp.v2.y, 10f, 75f, e.rotation);
        Drawf.tri(e.x + Tmp.v2.x, e.y + Tmp.v2.y, 10f, 35f, e.rotation + 90);
        Drawf.tri(e.x + Tmp.v2.x, e.y + Tmp.v2.y, 10f, 35f, e.rotation - 90);

        Draw.z(Layer.effect + 1);
        Draw.blend(Blending.additive);
        Fill.light(e.x + Tmp.v2.x, e.y + Tmp.v2.y, 20, 60, Pal.lancerLaser, Color.clear);
        Draw.blend();
    });
}
