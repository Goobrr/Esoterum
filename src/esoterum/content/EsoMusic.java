package esoterum.content;

import arc.*;
import arc.assets.*;
import arc.assets.loaders.*;
import arc.audio.*;
import arc.struct.*;
import mindustry.*;

// no more "musics"
public class EsoMusic {
    protected static Music loadMusic(String musicName, boolean ambient, boolean dark){
        if (!Vars.headless) {
            String name = "music/" + musicName;
            String path = Vars.tree.get(name + ".mp3").exists() ? name + ".mp3" : name + ".ogg";

            Music music = new Music();

            AssetDescriptor<?> desc = Core.assets.load(path, Music.class, new MusicLoader.MusicParameter(music));
            desc.errored = Throwable::printStackTrace;

            if(ambient) esoAmbientMusic.add(music);
            if(dark) esoDarkMusic.add(music);
            return music;
        } else {
            return new Music();
        }
    }

    public static Seq<Music>
        esoAmbientMusic = new Seq<>(),
        esoDarkMusic = new Seq<>();

    public static Music
        Eso1;

    public void load() {
        Eso1 = loadMusic("Eso1", true, true);
    }
}
