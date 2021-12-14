package esoterum.world.blocks.binary.transmission;

import esoterum.world.blocks.binary.basis.*;

public class BinaryRouter extends BinaryBlock{
    public BinaryRouter(String name){
        super(name);
        emits = true;
        inputs = new boolean[]{true, true, true, true};
        outputs = new boolean[]{true, true, true, true};
        propagates = true;
    }

    public class BinaryRouterBuild extends BinaryBuild {
        @Override
        public boolean updateSignal() {
            signal[5] = signal[4];
            signal[4] = false;
            for(BinaryBuild b : relnb) signal[4] |= getSignal(b, this);
            signal(signal[4]);
            return signal[5] != signal[4];
        }
    }
}