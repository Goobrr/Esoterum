package esoterum;

import arc.*;
import arc.audio.*;
import arc.struct.*;
import esoterum.content.*;
import mindustry.game.EventType.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;

import static mindustry.Vars.*;

public class Esoterum extends Mod{
    private static final Seq<Music>
        prevAmbient = new Seq<>(),
        prevDark = new Seq<>();
    private boolean lastMapEso;

    public Esoterum(){}

    @Override
    public void init(){
        if(!headless){
            LoadedMod eso = mods.locateMod("esoterum");
            Events.on(WorldLoadEvent.class, e -> {
                boolean isEso = state.map.mod != null && state.map.mod == eso;
                if(isEso != lastMapEso){
                    lastMapEso = !lastMapEso;
                    if(isEso){
                        swapMusic(control.sound.ambientMusic, EsoMusic.esoAmbientMusic, prevAmbient);
                        swapMusic(control.sound.darkMusic, EsoMusic.esoDarkMusic, prevDark);
                        //Log.info("Swapped to Eso music!");
                    }else{
                        swapMusic(control.sound.ambientMusic, prevAmbient, null);
                        swapMusic(control.sound.darkMusic, prevDark, null);
                        //Log.info("Swapped to Vanilla music!");
                    }
                }
            });
        }
    }

    private void swapMusic(Seq<Music> target, Seq<Music> replacement, Seq<Music> save){
        if(save != null){
            save.clear();
            save.addAll(target);
        }
        target.clear();
        target.addAll(replacement);
    }

    @Override
    public void loadContent(){
        new EsoBlocks().load();
        new EsoMusic().load();
    }

}
