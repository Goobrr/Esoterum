package esoterum.interfaces;

import arc.util.Log;
import esoterum.util.*;
import esoterum.world.blocks.binary.ColorWire;
import esoterum.world.blocks.bundled.BundledBlock;
import mindustry.gen.*;


public interface Bundledc{
    default short signal(){
        return 0;
    }

    default short signalFront(){
        return 0;
    }
    default short signalLeft(){
        return 0;
    }
    default short signalRight(){
        return 0;
    }
    default short signalBack(){
        return 0;
    }

    // get relative direction of "To" from "From"'s perspective then get the associated signal output.
    default short getSignalRelativeTo(BundledBlock.BundledBuild from, BundledBlock.BundledBuild to){
        if(!from.emits())return 0;
        return switch(EsoUtil.relativeDirection(from, to)){
            case 0 -> from.signalFront();
            case 1 -> from.signalLeft();
            case 2 -> from.signalBack();
            case 3 -> from.signalRight();
            default -> 0;
        };
    }

    default short getSignalRelativeTo(ColorWire.ColorWireBuild from, BundledBlock.BundledBuild to){
        if(!from.emits())return 0;

        short a =  switch(EsoUtil.relativeDirection(from, to)){
            case 0 -> (short)((from.signalFront()?1:0)<<from.channel);
            case 1 -> (short)((from.signalLeft()?1:0)<<from.channel);
            case 2 -> (short)((from.signalBack()?1:0)<<from.channel);
            case 3 -> (short)((from.signalRight()?1:0)<<from.channel);
            default -> 0;
        };
        return a;
    }

    default boolean connectionCheck(Building from, BundledBlock.BundledBuild to){
        if(from instanceof BundledBlock.BundledBuild b){
            return b.outputs()[EsoUtil.relativeDirection(b, to)] & to.inputs()[EsoUtil.relativeDirection(to, b)]
                || to.outputs()[EsoUtil.relativeDirection(to, b)] & b.inputs()[EsoUtil.relativeDirection(b, to)];
        }
        if(from instanceof ColorWire.ColorWireBuild b){
            return b.outputs()[EsoUtil.relativeDirection(b, to)] & to.inputs()[EsoUtil.relativeDirection(to, b)]
                || to.outputs()[EsoUtil.relativeDirection(to, b)] & b.inputs()[EsoUtil.relativeDirection(b, to)];
        }
        return false;
    }

    default short getSignal(Building from, BundledBlock.BundledBuild to){
        if(from instanceof BundledBlock.BundledBuild b){
            return getSignalRelativeTo(b, to);
        } else if(from instanceof ColorWire.ColorWireBuild b){
            return getSignalRelativeTo(b, to);
        }
        return 0;
    }

    default BundledBlock.BundledBuild checkType(Building b){
        if(b instanceof BundledBlock.BundledBuild bb)return bb;
        return null;
    }
    default ColorWire.ColorWireBuild checkType2(Building b){
        if(b instanceof ColorWire.ColorWireBuild bb)return bb;
        return null;
    }
}
