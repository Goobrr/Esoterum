package esoterum.world.blocks.binary;

import esoterum.ui.dialogs.ManualDialog;
import mindustry.gen.Building;
import mindustry.type.*;
import mindustry.world.Block;
import mindustry.world.meta.BuildVisibility;

public class Manual extends Block{
    public Manual(String name){
        super(name);
        update = true;
        solid = true;
        destructible = true;
        configurable = true;

        buildVisibility = BuildVisibility.shown;
        category = Category.logic;
    }

    public class ManualBuild extends Building{
        ManualDialog manual = new ManualDialog();

        @Override
        public boolean configTapped(){
            manual.show();
            return false;
        }
    }
}
