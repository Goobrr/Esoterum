package esoterum.world.blocks.binary;

public class BinaryRouter extends BinaryBlock{
    public BinaryRouter(String name){
        super(name);
        emits = true;

        inputs = new boolean[]{true, true, true, true};
        outputs = new boolean[]{true, true, true, true};
    }

    public class BinaryRouterBuild extends BinaryBuild {
        @Override
        public void updateTile() {
            super.updateTile();
            lastSignal = false;
            for(BinaryBuild b : nb){
                lastSignal |= getSignal(b, this);
            };
        }

        @Override
        public boolean signalFront() {
            return lastSignal;
        }
        @Override
        public boolean signalBack() {
            return lastSignal;
        }
        @Override
        public boolean signalLeft() {
            return lastSignal;
        }
        @Override
        public boolean signalRight() {
            return lastSignal;
        }
    }
}
