package esoterum.world.blocks.defense.beam;

public class BeamReflector extends BeamBlock{
    public BeamReflector(String name){
        super(name);
        rotate = true;
        rotatedBase = false;
        acceptsBeam = true;
    }
}
