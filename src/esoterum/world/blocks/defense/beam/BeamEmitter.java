package esoterum.world.blocks.defense.beam;

public class BeamEmitter extends BeamBlock{
    public BeamEmitter(String name){
        super(name);
        rotate = false;
        outputs = new boolean[]{false, false, false, false};
        inputs = new boolean[]{true, true, true, true};
    }

    public class BeamEmitterBuild extends BeamBuild {
        @Override
        public void updateTile() {
            super.updateTile();
            lastSignal = false;
            for(BinaryBuild b : nb){
                lastSignal |= getSignal(b, this);
            }
            if(lastSignal) updateBeam();
        }

        @Override
        public void draw() {
            super.draw();
            if(lastSignal) drawBeam(beamRotation, beamDrawLength);
        }
    }
}
