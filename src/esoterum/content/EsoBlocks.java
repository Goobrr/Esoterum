package esoterum.content;

import esoterum.world.blocks.binary.*;
import esoterum.world.blocks.environment.*;
import mindustry.ctype.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

public class EsoBlocks implements ContentList{
    public static Block
        // Environment
        esoPanel, esoPanel1, esoPanel2, esoPanel3, esoPanelFlat, esoSolidPanel,
        esoPanelOpen, esoPanelE, esoPanelS, esoPanelO,
        // Signal distribution
        esoWire, esoJunction, esoRouter, esoNode, esoLatch, esoController, esoBuffer,
        // Signal sources
        esoSwitch, esoButton,
        // Logic gates
        esoAND, esoXOR, esoNOT,
        // Logic outputs
        noteBlock,

        esoManual;

    @Override
    public void load(){
        // region environment
        esoPanel = new NonblendFloor("panel", 0);
        esoPanel1 = new NonblendFloor("panel-1", 0);
        esoPanel2 = new NonblendFloor("panel-2", 0);
        esoPanel3 = new NonblendFloor("panel-3", 0);

        esoPanelFlat = new NonblendFloor("panel-flat", 0);
        esoSolidPanel = new StaticWall("panel-solid"){{
            variants = 0;
        }};
        esoPanelOpen = new NonblendFloor("panel-open", 4);

        esoPanelE = new NonblendFloor("panel-e", 0);
        esoPanelS = new NonblendFloor("panel-s", 0);
        esoPanelO = new NonblendFloor("panel-o", 0);
        // endregion environment

        // region distribution
        esoWire = new BinaryWire("wire");

        esoJunction = new BinaryJunction("junction");

        esoRouter = new BinaryRouter("router");

        esoNode = new BinaryNode("node", 5);

        esoLatch = new LatchBlock("latch");

        esoController = new SignalController("controller");

        esoBuffer = new BinaryBuffer("buffer");
        // endregion distribution

        // region sources
        esoSwitch = new BinaryButton("switch", true);

        esoButton = new BinaryButton("button", false);
        // endregion sources

        // region logic gates
        esoAND = new LogicGate("AND"){{
            operation = i -> i[0] && i[1];
        }};

        esoXOR = new LogicGate("XOR"){{
            operation = i -> i[0] ^ i[1];
        }};

        esoNOT = new LogicGate("NOT"){{
            operation = i -> !i[0];
            single = true;
        }};
        // endregion logic gates

        // region logic outputs
        noteBlock = new NoteBlock("note-block");
        // endregion logic outputs

        esoManual = new Manual("manual");
    }
}
