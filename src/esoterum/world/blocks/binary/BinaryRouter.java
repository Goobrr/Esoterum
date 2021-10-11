package esoterum.world.blocks.binary;

public class BinaryRouter extends BinaryBlock{
    public BinaryRouter(String name){
        super(name);
        emits = true;
        transmits = true;
        inputs = new boolean[]{true, true, true, true};
        outputs = new boolean[]{true, true, true, true};
    }

    public class BinaryRouterBuild extends BinaryBuild {
        @Override
        public void updateSignal(int depth) {
            if(depth < depthLimit){
                if(nb.get(0) != null && connectionCheck(nb.get(1), this))
                    nb.get(0).updateSignal(depth + 1);
                if(nb.get(1) != null && connectionCheck(nb.get(1), this))
                    nb.get(1).updateSignal(depth + 1);
                if(nb.get(2) != null && connectionCheck(nb.get(2), this))
                    nb.get(2).updateSignal(depth + 1);
                if(nb.get(3) != null && connectionCheck(nb.get(3), this))
                    nb.get(3).updateSignal(depth + 1);
            }
            signal(false);
            for(BinaryBuild b : nb){
                signal(signal() || getSignal(b, this));
            };
        }
    }
}
