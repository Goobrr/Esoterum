package esoterum.util;

import arc.func.Cons;
import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import mindustry.entities.Units;
import mindustry.gen.*;

public class EsoUtil{
    public static Rect rect = new Rect();
    public static Rect hitrect = new Rect();
    public static Vec2 tr = new Vec2();

    /** relativeTo does not account for building rotation. */
    public static int relativeDirection(Building from, Building to){
        return (4 + from.relativeTo(to) - from.rotation) % 4;
    }

    /** @return the multiplier for the pitch of a sound to be an amount of semitones higher */
    public static float notePitch(int semitones){
        return (float)Math.pow(2, semitones / 12f);
    }

    /** Extracts a number out of a string by removing every non-numerical character  */
    public static String extractNumber(String s){
        //God, I love google. I have no idea what the "[^\\d.]" part even is.
        return s.replaceAll("[^\\d.]", "");
    }

    public static void linecastUnits(float x, float y, float angle, float length, Cons<Unit> cons){
        tr.setZero().trns(angle, length);
        rect.setPosition(x, y).setSize(tr.x, tr.y);
        float x2 = tr.x + x, y2 = tr.y + y;

        if(rect.width < 0){
            rect.x += rect.width;
            rect.width *= -1;
        }

        if(rect.height < 0){
            rect.y += rect.height;
            rect.height *= -1;
        }

        // i dont know why the fuck Damage.linecast does this but i'll do it too
        rect.x -= 3;
        rect.y -= 3;
        rect.width += 6;
        rect.height += 6;


        Units.nearby(rect, u -> {
            u.hitbox(hitrect);
            Rect other = hitrect;

            other.x -= 3;
            other.y -= 3;
            other.width += 6;
            other.height += 6;

            Vec2 vec = Geometry.raycastRect(x, y, x2, y2, other);

            if(vec != null){
                cons.get(u);
            }
        });
    }
}
