package esoterum.content;

import arc.Core;
import arc.assets.AssetDescriptor;
import arc.assets.loaders.MusicLoader;
import arc.audio.Music;
import mindustry.Vars;

public class EsoMusicLoader {
    protected static Music loadMusic(String musicsName) {
        if (!Vars.headless) {
            String name = "musics/" + musicsName;
            String path = Vars.tree.get(name + ".ogg").exists() ? name + ".ogg" : name + ".mp3";

            Music music = new Music();

            AssetDescriptor<?> desc = Core.assets.load(path, Music.class, new MusicLoader.MusicParameter(music));
            desc.errored = Throwable::printStackTrace;

            return music;
        } else {
            return new Music();
        }
    }

    public static Music
            Eso1;

    public static void load() {

        Eso1 = loadMusic("Eso1");
    }
}
