package esoterum.world.blocks.binary.basis;

public class BinarySource extends BinaryBlock{
    public BinarySource(String name){
        super(name);
    }

    public class BinarySourceBuild extends BinaryBuild{
        public boolean isActive(WireGraph graph){
            return false;
        }
    }
}