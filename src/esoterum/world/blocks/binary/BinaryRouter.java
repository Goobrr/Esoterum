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
        public void updateSignal(int depth) {
            signal(false);
            for(BinaryBuild b : nb){
                signal(signal() || getSignal(b, this));
            };
        }
    }
}
