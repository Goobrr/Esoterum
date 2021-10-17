package esoterum.world.blocks.defense.beam;

public class BeamAcceptor extends BeamBlock{
    public BeamAcceptor(String name){
        super(name);
        rotate = false;
        acceptsBeam = true;
        outputs = new boolean[]{true, true, true, true};
        emits = true;
    }

    public class BeamAcceptorBuild extends BeamBuild {
        @Override
        public void updateTile() {
            super.updateTile();
            signal[0] = signal[1] = signal[2] = signal[3] = active;
            if(active != signal[4])propagateSignal(true, true, true, true);
            signal[4] = active;
        }

        @Override
        public void updateBeam() {
            active = true;
            if(beamStrength - 1 <= 0){
                beamDrawLength = 0;
                return;
            }
            beamDrawLength = beam(beamRotation + rotdeg(), true, beamStrength - 1);
        }
    }
}
