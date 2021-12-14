package esoterum.world.blocks.binary.basis;

import mindustry.gen.*;
import mindustry.logic.*;
import mindustry.world.*;

public class BinaryBlock extends Block{
    public BinaryBlock(String name){
        super(name);

        solid = true;
        update = true;
        destructible = true;
    }

    public class BinaryBuild extends Building{
        //literally just exists to differentiate from Building
    }
}