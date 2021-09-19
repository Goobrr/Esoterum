package esoterum.content;

import esoterum.world.blocks.binary.*;
import mindustry.ctype.ContentList;
import mindustry.world.*;

public class EsoBlocks implements ContentList {
    public Block
        esoWire, esoJunction, esoNode, esoSwitch, esoButton, esoAND;

    @Override
    public void load() {
        esoWire = new BinaryWire("wire");

        esoJunction = new BinaryJunction("junction");

        esoNode = new BinaryNode("node");

        esoSwitch = new BinaryButton("switch", true);

        esoButton = new BinaryButton("button", false);

        esoAND = new LogicGate("AND", false, true, true){{
            operation = i -> i[1] && i[2];
        }};
    }
}
