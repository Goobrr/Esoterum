package esoterum.world.blocks.defense.beam;

public class BeamAcceptor extends BeamBlock{
    public BeamAcceptor(String name){
        super(name);
        rotate = true;
        acceptsBeam = true;
        outputs = new boolean[]{true, true, true, true};
        emits = true;
        propagates = false;
    }

    public class BeamAcceptorBuild extends BeamBuild {
        @Override
        public boolean updateSignal(){
            signal[5] = signal();
            super.updateSignal();
            signal(active);
            return signal[5] != signal();
        }
        
        @Override
        public void updateBeam() {
            active = true;
            signal(active);
            if(beamStrength - 1 <= 0){
                beamDrawLength = 0;
                return;
            }
            beamDrawLength = beam(beamRotation + rotdeg(), true, beamStrength - 1);
        }
    }
}
