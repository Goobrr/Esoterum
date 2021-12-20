package esoterum.world.blocks.binary.basis;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.gen.*;
import mindustry.world.*;

public class BinaryBlock extends Block{
    public TextureRegion topRegion, connectionRegion, baseRegion, highlightRegion, stubRegion;
    public TextureRegion[] baseRegions, highlightRegions = new TextureRegion[4];
    /** Can someone explain what the hell this is k thx. */
    public int[] tiles = new int[]{
        39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
        38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
        39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
        38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
        3,  4,  3,  4, 15, 40, 15, 20,  3,  4,  3,  4, 15, 40, 15, 20,
        5, 28,  5, 28, 29, 10, 29, 23,  5, 28,  5, 28, 31, 11, 31, 32,
        3,  4,  3,  4, 15, 40, 15, 20,  3,  4,  3,  4, 15, 40, 15, 20,
        2, 30,  2, 30,  9, 46,  9, 22,  2, 30,  2, 30, 14, 44, 14,  6,
        39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
        38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
        39, 36, 39, 36, 27, 16, 27, 24, 39, 36, 39, 36, 27, 16, 27, 24,
        38, 37, 38, 37, 17, 41, 17, 43, 38, 37, 38, 37, 26, 21, 26, 25,
        3,  0,  3,  0, 15, 42, 15, 12,  3,  0,  3,  0, 15, 42, 15, 12,
        5,  8,  5,  8, 29, 35, 29, 33,  5,  8,  5,  8, 31, 34, 31,  7,
        3,  0,  3,  0, 15, 42, 15, 12,  3,  0,  3,  0, 15, 42, 15, 12,
        2,  1,  2,  1,  9, 45,  9, 19,  2,  1,  2,  1, 14, 18, 14, 13
    };

    public boolean rotatedBase = false;
    public boolean rotatedTop = false;
    public String baseType = "square";
    public String baseHighlight = "none";

    public BinaryBlock(String name){
        super(name);

        solid = true;
        update = true;
        destructible = true;
    }

    public void load() {
        super.load();
        baseRegion = Core.atlas.find("esoterum-base-" + baseType);
        highlightRegion = Core.atlas.find("esoterum-base-" + baseHighlight, "esoterum-base-none");
        for(int i = 0; i < 4; i++){
            highlightRegions[i] = Core.atlas.find("esoterum-base-" + baseHighlight + "-" + i, "esoterum-base-none");
        }
        connectionRegion = Core.atlas.find(name + "-connection", "esoterum-connection");
        topRegion = Core.atlas.find(name, "esoterum-router"); // router supremacy
        stubRegion = Core.atlas.find("esoterum-stub");
        baseRegions = getRegions(Core.atlas.find("esoterum-base-ultra"), 12, 4);
    }

    //yoinked from xelo
    public static TextureRegion[] getRegions(TextureRegion region, int w, int h){
        int size = w * h;
        TextureRegion[] regions = new TextureRegion[size];
        float tileW = (region.u2 - region.u) / w;
        float tileH = (region.v2 - region.v) / h;
        for(int i = 0; i < size; i++){
            float tileX = ((float)(i % w)) / w;
            float tileY = ((float)(i / w)) / h;
            TextureRegion reg = new TextureRegion(region);
            //start coordinate
            reg.u = Mathf.map(tileX, 0f, 1f, reg.u, reg.u2) + tileW * 0.02f;
            reg.v = Mathf.map(tileY, 0f, 1f, reg.v, reg.v2) + tileH * 0.02f;
            //end coordinate
            reg.u2 = reg.u + tileW * 0.96f;
            reg.v2 = reg.v + tileH * 0.96f;
            reg.width = reg.height = 32;
            regions[i] = reg;
        }
        return regions;
    }
    @Override
    protected TextureRegion[] icons() {
        return new TextureRegion[]{
            baseRegion,
            rotate && rotatedBase ? highlightRegions[0] : highlightRegion,
            topRegion
        };
    }

    public class BinaryBuild extends Building{
        public SignalGraph signal;

        @Override
        public void onProximityAdded(){
            super.onProximityAdded();

            signal.updateConnected(this);
        }

        @Override
        public void onProximityRemoved(){
            super.onProximityRemoved();

            signal.removeConnected(this);
        }

        @Override
        public void draw(){
            drawBase();
            drawTop();
        }

        public void drawBase(){
            Draw.rect(baseRegion, x, y);
            if(!rotate || !rotatedBase){
                Draw.rect(highlightRegion, x, y);
            }else{
                Draw.rect(highlightRegions[rotation], x, y);
            }
        }

        public void drawTop(){
            Draw.color(Color.white, team.color, Mathf.num(signal()));
            Draw.rect(topRegion, x, y, rotate && rotatedTop ? rotdeg() : 0);
        }


    }
}