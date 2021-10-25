package esoterum;

import arc.*;
import arc.audio.*;
import arc.struct.*;
import esoterum.content.*;
import esoterum.ui.*;
import esoterum.ui.dialogs.*;
import esoterum.world.blocks.binary.*;
import mindustry.game.*;
import mindustry.game.EventType.*;
import mindustry.io.*;
import mindustry.mod.*;
import mindustry.mod.Mods.*;

import static mindustry.Vars.*;

public class Esoterum extends Mod{
    public static ManualDialog manual;

    private static final Seq<Music> prevAmbient = new Seq<>(), prevDark = new Seq<>();
    private boolean lastMapEso;
    public Thread t;

    public Esoterum(){
        if(!headless){
            Events.on(EventType.FileTreeInitEvent.class, h -> EsoSounds.load());
            Events.on(ClientLoadEvent.class, e -> {
                manual = new ManualDialog();
                EsoStyle.init();
            });
            Events.on(WorldLoadEvent.class, e -> {
                LoadedMod eso = mods.locateMod("esoterum");
                boolean isEso = state.map.mod != null && state.map.mod == eso;

                if(isEso != lastMapEso){
                    lastMapEso = !lastMapEso;
                    if(isEso){
                        swapMusic(control.sound.ambientMusic, EsoMusic.esoAmbientMusic, prevAmbient);
                        swapMusic(control.sound.darkMusic, EsoMusic.esoDarkMusic, prevDark);
                    }else{
                        swapMusic(control.sound.ambientMusic, prevAmbient, null);
                        swapMusic(control.sound.darkMusic, prevDark, null);
                    }
                }
            });
            Events.on(SaveWriteEvent.class, e -> {
                t.interrupt();
                StringMap map = new StringMap();
                SignalGraph.writeGraph(map);
                state.rules.tags.put("SignalGraph", JsonIO.json.toJson(map, StringMap.class, String.class));
            });
            Events.on(SaveLoadEvent.class, e -> {
                if(state.rules.tags.get("SignalGraph") != null)
                    SignalGraph.readGraph(JsonIO.json.fromJson(StringMap.class, String.class, state.rules.tags.get("SignalGraph")));
                t = new Thread(){
                    @Override
                    public void run(){
                        SignalGraph.run();
                    }
                };
                t.start();
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
        new EsoUnits().load();
        new EsoBlocks().load();
        new EsoMusic().load();
    }
}
