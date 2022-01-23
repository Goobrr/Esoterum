package esoterum.world.blocks.binary;

import esoterum.util.graph.*;

public class SignalGraph {
    private static final Augmentation AUGMENTATION = new Augmentation() {
        @Override
        public Object combine(Object value1, Object value2){
            return (int)value1 + (int)value2;
        }
    };

    public static ConnGraph graph = new ConnGraph(AUGMENTATION);

    public static void addVertex(BinaryBlock.BinaryBuild b, int k){
        b.v[k] = new ConnVertex();
        graph.setVertexAugmentation(b.v[k], 0);
        //Log.info("add");
    }

    public static void addEdge(ConnVertex u, ConnVertex v){
        if(u != null && v != null) graph.addEdge(u, v);
    }

    public static void removeVertex(BinaryBlock.BinaryBuild b, int k){
        if(b == null || b.v[k] == null) return;
        for(ConnVertex v : graph.vertexInfo.get(b.v[k]).edges.keySet()){
            graph.removeEdge(b.v[k], v);
        }
        graph.removeVertexAugmentation(b.v[k]);
        b.v[k] = null;
        //Log.info("rm");
    }

    public static void clearVertices(){
        graph = new ConnGraph(AUGMENTATION);
    }

    public static void removeEdge(ConnVertex u, ConnVertex v){
        if(u != null && v != null) graph.removeEdge(u, v);
    }

    public static void clear(){
        graph = new ConnGraph(AUGMENTATION);
    }

    public static void clearEdges(ConnVertex v){
        if(v == null || graph.vertexInfo.get(v) == null) return;
        for(ConnVertex u : graph.vertexInfo.get(v).edges.keySet()){
            graph.removeEdge(v, u);
        }
    }
}