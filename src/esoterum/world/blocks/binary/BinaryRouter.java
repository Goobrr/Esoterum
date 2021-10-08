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
            lastSignal = 0;
            for(BinaryBuild b : nb){
                lastSignal |= getSignal(b, this);
            };
        }

        @Override
        public int signalFront() {
            return lastSignal;
        }
        @Override
        public int signalBack() {
            return lastSignal;
        }
        @Override
        public int signalLeft() {
            return lastSignal;
        }
        @Override
        public int signalRight() {
            return lastSignal;
        }
    }
}
