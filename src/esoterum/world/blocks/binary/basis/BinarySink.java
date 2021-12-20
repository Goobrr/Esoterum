package esoterum.world.blocks.binary.basis;

import arc.util.io.*;

public class BinarySink extends BinarySource{
    public BinarySink(String name){
        super(name);

        rotate = true;
    }

    public class BinarySinkBuild extends BinarySourceBuild{
        public boolean active;

        public void signalUpdate(){

        }

        @Override
        public void write(Writes write){
            super.write(write);

            write.bool(active);
        }

        @Override
        public void read(Reads read, byte revision){
            super.read(read, revision);

            if(revision >= 2) active = read.bool();
        }

        @Override
        public byte version(){
            return 2;
        }
    }
}