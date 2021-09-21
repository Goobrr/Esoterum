package esoterum.util;

import arc.math.*;
import mindustry.gen.*;

public class EsoUtil{

    // relativeTo does not account for building rotation.
    public static int relativeDirection(Building from, Building to){
        return Mathf.mod(4 + from.relativeTo(to) - from.rotation, 4);
    }
}
