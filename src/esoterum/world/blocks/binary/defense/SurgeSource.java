package esoterum.world.blocks.binary.defense;

import esoterum.entities.SignalSurges;
import esoterum.world.blocks.binary.*;

public class SurgeSource extends BinaryButton {
    public SignalSurges.SignalSurge surge = new SignalSurges.SignalSurge();
    public SurgeSource(String name){
        super(name, false);
    }

    public class SurgeSourceBuild extends BinaryButtonBuild {
        @Override
        public boolean configTapped() {
            surge.create(this, x, y, 0f);
            return false;
        }
    }
}
