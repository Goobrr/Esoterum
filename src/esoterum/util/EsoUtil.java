package esoterum.util;

import mindustry.gen.*;

public class EsoUtil{
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
}
