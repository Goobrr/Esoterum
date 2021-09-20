package esoterum.content;

import esoterum.world.blocks.binary.*;
import mindustry.ctype.ContentList;
import mindustry.world.*;

public class EsoBlocks implements ContentList {
    public Block
        // Signal distribution
        esoWire, esoJunction, esoRouter, esoNode, esoLatch,
        // Signal sources
        esoSwitch, esoButton,
        // Logic gates
        esoAND, esoXOR, esoNOT;

    @Override
    public void load() {
        esoWire = new BinaryWire("wire");

        esoJunction = new BinaryJunction("junction");

        esoRouter = new BinaryRouter("router");

        esoNode = new BinaryNode("node", 5);

        esoLatch = new LatchBlock("latch");

        esoSwitch = new BinaryButton("switch", true);

        esoButton = new BinaryButton("button", false);

        esoAND = new LogicGate("AND", false, true, true){{
            operation = i -> i[1] && i[2];
        }};

        esoXOR = new LogicGate("XOR", false, true, true){{
            operation = i -> i[1] ^ i[2];
        }};

        esoNOT = new LogicGate("NOT", false, true, false){{
            operation = i -> !i[1];
        }};
    }
}
