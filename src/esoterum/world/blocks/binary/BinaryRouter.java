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
        public void updateSignal(int source) {
            try {
                super.updateSignal(source);
                signal[4] = false;
                for(BinaryBuild b : nb){
                    signal[4] |= getSignal(b, this);
                };
                if(signal() != signal[4]){
                    signal(signal[4]);
                    propagateSignal(source != 0, source != 1, source != 2, source != 3);
                }
            } catch(Exception e){}
        }
    }
}
