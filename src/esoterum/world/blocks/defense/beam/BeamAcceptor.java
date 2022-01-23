package esoterum.world.blocks.defense.beam;

import esoterum.world.blocks.binary.*;

public class BeamAcceptor extends BeamBlock{
    public BeamAcceptor(String name){
        super(name);
        rotate = true;
        acceptsBeam = true;
        outputs = new boolean[]{true, true, true, true};
        emits = true;
        undirected = false;
    }

    public class BeamAcceptorBuild extends BeamBuild {
        @Override
        public int inV(int dir){
            return dir;
        }

        @Override
        public boolean updateSignal(){
            signal[5] = signal();
            super.updateSignal();
            signal(active);
            SignalGraph.graph.setVertexAugmentation(v[0], signal()?1:0);
            SignalGraph.graph.setVertexAugmentation(v[1], signal()?1:0);
            SignalGraph.graph.setVertexAugmentation(v[2], signal()?1:0);
            SignalGraph.graph.setVertexAugmentation(v[3], signal()?1:0);
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
