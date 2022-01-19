package esoterum.world.blocks.binary;

import java.util.HashSet;

import esoterum.util.graph.*;

public class SignalGraph {
    private static final Augmentation AUGMENTATION = new Augmentation() {
        @Override
        public Object combine(Object value1, Object value2){
            return (int)value1 + (int)value2;
        }
    };

    public static ConnGraph graph = new ConnGraph(AUGMENTATION);
    public static HashSet<BinaryBlock.BinaryBuild> sources = new HashSet<>();

    public static void addVertex(BinaryBlock.BinaryBuild b){
        b.v = new ConnVertex();
        graph.setVertexAugmentation(b.v, 0);
        if(!b.propagates()) sources.add(b);
        //Log.info("add");
    }

    public static void addEdge(BinaryBlock.BinaryBuild a, BinaryBlock.BinaryBuild b){
        graph.addEdge(a.v, b.v);
    }

    public static void removeVertex(BinaryBlock.BinaryBuild b){
        graph.removeVertexAugmentation(b.v);
        for(ConnVertex v : graph.vertexInfo.get(b.v).edges.keySet()){
            graph.removeEdge(b.v, v);
        }
        b.v = null;
        if(!b.propagates()) sources.remove(b);
        //Log.info("rm");
    }

    public static void clearVertices(){
        graph = new ConnGraph(AUGMENTATION);
    }

    public static void removeEdge(BinaryBlock.BinaryBuild a, BinaryBlock.BinaryBuild b){
        graph.removeEdge(a.v, b.v);
    }

    public static void clear(){
        graph = new ConnGraph(AUGMENTATION);
        sources.clear();
    }

    public static void clearEdges(BinaryBlock.BinaryBuild b){
        for(ConnVertex v : graph.vertexInfo.get(b.v).edges.keySet()){
            graph.removeEdge(b.v, v);
        }
    }
}