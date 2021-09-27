package esoterum.interfaces;

import esoterum.util.*;
import esoterum.world.blocks.binary.*;
import mindustry.gen.*;


public interface Binaryc{
    default boolean signal(){
        return false;
    }

    default boolean signalFront(){
        return false;
    }
    default boolean signalLeft(){
        return false;
    }
    default boolean signalRight(){
        return false;
    }
    default boolean signalBack(){
        return false;
    }

    // get relative direction of "To" from "From"'s perspective then get the associated signal output.
    default boolean getSignalRelativeTo(BinaryBlock.BinaryBuild from, BinaryBlock.BinaryBuild to){
        // if the building does not emit signals, return false.
        if(!from.emits())return false;

        return switch(EsoUtil.relativeDirection(from, to)){
            case 0 -> from.signalFront();
            case 1 -> from.signalLeft();
            case 2 -> from.signalBack();
            case 3 -> from.signalRight();
            default -> false;
        };
    }

    default boolean connectionCheck(Building from, BinaryBlock.BinaryBuild to){
        if(from instanceof BinaryBlock.BinaryBuild b){
            return b.outputs()[EsoUtil.relativeDirection(b, to)] & to.inputs()[EsoUtil.relativeDirection(to, b)]
                || to.outputs()[EsoUtil.relativeDirection(to, b)] & b.inputs()[EsoUtil.relativeDirection(b, to)];
        }
        return false;
    }

    default boolean getSignal(Building from, BinaryBlock.BinaryBuild to){
        if(from instanceof BinaryBlock.BinaryBuild b){
            return getSignalRelativeTo(b, to);
        }
        return false;
    }

    default BinaryBlock.BinaryBuild checkType(Building b){
        if(b instanceof BinaryBlock.BinaryBuild bb)return bb;
        return null;
    }
}
