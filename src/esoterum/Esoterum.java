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
    private static final Seq<Music> prevMusic = new Seq<>();
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
                        prevMusic.clear();
                        prevMusic.addAll(control.sound.ambientMusic);
                        control.sound.ambientMusic.clear();
                        control.sound.ambientMusic.addAll(EsoMusic.esoMusic);
                        //Log.info("Swapped to Eso music!");
                    }else{
                        control.sound.ambientMusic.clear();
                        control.sound.ambientMusic.addAll(prevMusic);
                        //Log.info("Swapped to Vanilla music!");
                    }
                }
            });
        }
    }

    @Override
    public void loadContent(){
        new EsoBlocks().load();
        new EsoMusic().load();
    }

}
