package esoterum.world.blocks.defense.beam;

public class BeamReflector extends BeamBlock{
    public BeamReflector(String name){
        super(name);
        rotate = true;
        rotatedBase = false;
        acceptsBeam = true;
    }

    public class BeamReflectorBuild extends BeamBuild {
        @Override
        public void updateTile() {
            super.updateTile();
            active = false;
            if(signal()){
                updateBeam();
                signal(false);
            }
        }

        public void draw() {
            super.draw();
            if(active) drawBeam(beamRotation + rotdeg(), beamDrawLength);
        }
    }
}