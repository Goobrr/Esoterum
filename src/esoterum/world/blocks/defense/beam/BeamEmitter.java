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
            signal[4] = false;
            for(BinaryBuild b : nb){
                signal[4] |= getSignal(b, this);
            }
            signal(signal[4]);
            if(signal()) updateBeam();
        }

        @Override
        public void draw() {
            super.draw();
            if(signal()) drawBeam(beamRotation, beamDrawLength);
        }
    }
}
