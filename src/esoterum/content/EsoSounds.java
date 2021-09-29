package esoterum.content;

import arc.*;
import arc.assets.*;
import arc.assets.loaders.SoundLoader.*;
import arc.audio.*;
import mindustry.*;

public class EsoSounds{
    protected static Sound loadSound(String soundName){
        String name = "sounds/" + soundName;
        String path = Vars.tree.get(name + ".ogg").exists() ? name + ".ogg" : name + ".mp3";

        Sound sound = new Sound();

        AssetDescriptor<?> desc = Core.assets.load(path, Sound.class, new SoundParameter(sound));
        desc.errored = Throwable::printStackTrace;

        return sound;
    }

    public static Sound[] loadNotes(String soundName){
        Sound[] out = new Sound[5];
        for(int i = 0; i < 5; i++){
            out[i] = loadSound(soundName + "/" + soundName + "C" + (2 + i));
        }
        return out;
    }

    public static Sound[]
        bells, bass, saw, organ;

    public static void load(){
        bells = loadNotes("bell");
        bass = loadNotes("bass");
        saw = loadNotes("saw");
        organ = loadNotes("organ");
    }
}
