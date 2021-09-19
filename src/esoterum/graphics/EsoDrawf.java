package esoterum.graphics;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;

import static arc.math.Angles.*;
import static arc.math.Mathf.*;

public class EsoDrawf{
    private static final Vec2 v1 = new Vec2(), v2 = new Vec2();

    public static void curvedLine(float x, float y, float tx, float ty, float w){
        float dst = dst(x, y, tx, ty);
        int seg = (int)(dst / 2f);
        float ang = angle(x, y, tx, ty) - 90f;

        for(int i = 0; i < seg; i++){
            float p1 = i / (float)seg, p2 = ((i + 1f) / seg);
            v1.trns(ang, Mathf.sin(p1 * Mathf.PI) * w, Mathf.lerp(0f, dst, p1));
            v2.trns(ang, Mathf.sin(p2 * Mathf.PI) * w, Mathf.lerp(0f, dst, p2));
            Lines.line(
                x + v1.x, y + v1.y,
                x + v2.x, y + v2.y
            );
        }
    }
}
