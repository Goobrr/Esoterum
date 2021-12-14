package esoterum.world.blocks.binary.basis;

import arc.struct.*;

public class BinarySink extends BinarySource{
    public BinarySink(String name){
        super(name);

        rotate = true;
    }

    public class BinarySinkBuild extends BinarySourceBuild{
        public Seq<WireGraph> signals = new Seq<>(4);
    }
}