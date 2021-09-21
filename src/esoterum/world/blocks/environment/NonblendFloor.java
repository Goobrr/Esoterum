package esoterum.world.blocks.environment;

import mindustry.world.blocks.environment.*;

public class NonblendFloor extends Floor{
    public NonblendFloor(String name, int variants){
        super(name, variants);
    }

    @Override
    protected boolean doEdge(Floor other){
        return !(other instanceof NonblendFloor) && super.doEdge(other);
    }
}