package esoterum;

import esoterum.content.*;
import mindustry.mod.*;

public class Esoterum extends Mod{

    public Esoterum(){
    }

    @Override
    public void loadContent(){
        new EsoBlocks().load();
        new EsoMusic().load();
    }

}
