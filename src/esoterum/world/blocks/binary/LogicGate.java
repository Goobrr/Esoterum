package esoterum.world.blocks.binary;

import arc.func.Boolf;

public class LogicGate extends BinaryBlock{
    public Boolf<boolean[]> operation;
    public LogicGate(String name, boolean l, boolean b, boolean r){
        super(name);
        inputs = new boolean[]{false, l, b, r};
        outputs = new boolean[]{true, false, false, false};
        emits = true;
        rotate = true;
        drawArrow = true;

        operation = e -> false;
    }

    public class LogicGateBuild extends BinaryBuild {

        @Override
        public void updateTile() {
            super.updateTile();
            lastSignal = signal();
        }

        @Override
        public boolean signal() {
            return operation.get(new boolean[]{
                getSignal(nb.get(1), this),
                getSignal(nb.get(2), this),
                getSignal(nb.get(3), this)
            });
        }

        @Override
        public boolean signalFront() {
            return lastSignal;
        }
    }
}
