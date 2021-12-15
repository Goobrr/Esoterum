package esoterum.content;

import esoterum.world.blocks.binary.*;
import esoterum.world.blocks.binary.transmission.BinaryRouter;
import esoterum.world.blocks.binary.interaction.*;
import esoterum.world.blocks.binary.music.*;
import esoterum.world.blocks.binary.source.*;
import esoterum.world.blocks.binary.transmission.*;
import esoterum.world.blocks.defense.*;
import esoterum.world.blocks.defense.beam.*;
import esoterum.world.blocks.environment.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;

public class EsoBlocks implements ContentList{
    public static Block
        // Environment
        esoPanel, esoPanel1, esoPanel2, esoPanel3, esoPanelFlat, esoSolidPanel,
        esoPanelOpen, esoPanelE, esoPanelS, esoPanelO,
        // Signal transmission
        esoWire, esoJunction, esoCJunction, esoRouter, esoNode, esoLatch, esoController, esoBuffer, esoFlipflop,
        // Signal sources
        esoSwitch, esoButton, esoClock,
        // Logic gates
        esoAND, esoXOR, esoNOT,
        // Logic music
        musicBuffer, noteBlock,
        // Logic interaction
        togglerBlock,
        // Beams
        beamEmitter, beamReflector, beamAcceptor,
        // Defense
        smallSentry,

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
        
        // region transmission
        esoWire = new BinaryRouter("wire");

        esoJunction = new BinaryJunction("junction");

        esoCJunction = new BinaryCJunction("cornerjunction");
        
        esoRouter = new BinaryRouter("router");

        esoNode = new BinaryNode("node", 6);

        esoLatch = new LatchBlock("latch");

        esoController = new SignalController("controller");

        esoBuffer = new BinaryBuffer("buffer");

        esoFlipflop = new BinaryFlipflop("flipflop");
        // endregion transmission
        
        // region sources
        esoSwitch = new BinaryButton("switch", true);

        esoButton = new BinaryButton("button", false);
        
        esoClock = new BinaryClock("clock");
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

        // region logic music
        musicBuffer = new MusicBuffer("rest-buffer");

        noteBlock = new NoteBlock("note-block");
        // endregion logic music

        // region logic interaction
        togglerBlock = new Toggler("toggler");
        // endregion logic interaction
        
        // region beams
        beamEmitter = new BeamEmitter("beam-emitter"){{
            beamLength = 80f;
        }};

        beamReflector = new BeamReflector("beam-reflector"){{
            beamLength = 80f;
            drawLight = true;
        }};

        beamAcceptor = new BeamAcceptor("beam-acceptor"){{
            drawLight = true;
        }};
        // endregion beams

        // region defense
        smallSentry = new SentryTurret("small-sentry"){{
            reloadTime = 6f;
            range = 70f;
            buildVisibility = BuildVisibility.shown;
            powerUse = 2f;
            shootCone = 45f;
            detectionCone = 20f;
            swayMag = 90f / 2;
            swayScl = 30f;
            inaccuracy = 8f;

            shootSound = Sounds.pew;
            smokeEffect = Fx.none;
            shootEffect = EsoFx.sentryShoot;
            shootType = new BasicBulletType(16f, 20){{
                width = 2f;
                height = 10f;

                smokeEffect = Fx.none;
                shootEffect = Fx.none;
            }};
        }};
        // endregion defense

        esoManual = new Manual("manual");
    }
}