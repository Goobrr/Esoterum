package esoterum.world.blocks.binary;

import esoterum.world.blocks.binary.BinaryBlock.*;

import java.util.*;

import arc.struct.*;

class Node {
    public Node left, right, parent;
    public int children, height;
    public Edge value;
    public SplayTree tree;
    public long xor;
    public Node(){
        left = null;
        right = null;
        parent = null;
        children = 0;
        height = 0;
    }
    public Node(Edge e){
        left = null;
        right = null;
        parent = null;
        children = 0;
        height = 0;
        value = e;
    }
    public Node root(){
        Node p = this;
        while(p.parent != null) p = p.parent;
        return p;
    }
    public void height(){
        if(left.height > right.height)
            height = left.height + 1;
        else height = right.height + 1;
    }
    public void xor(){
        if(value.u == value.v) xor = 0;
        else xor = value.u.xor;
        xor ^= left.xor ^ right.xor;
    }
}

class Vertex {
    public BinaryBuild value;
    public HashMap<ETTree, Node> nodes;
    public int id;
    public long xor;
    public Node node(ETTree t){
        return nodes.get(t);
    }
    public void node(ETTree t, Node n){
        nodes.put(t, n);
    }
}

class Edge {
    public Vertex u, v;
    public boolean directed = false;
    public HashMap<ETTree, Node> nodes;
    public long id;
    public Edge(Vertex u, Vertex v){
        this.u = u;
        this.v = v;
        id = ((long)u.id << 32) | (long)v.id;
    }
    public Edge(Vertex u, Vertex v, boolean d){
        this.u = u;
        this.v = v;
        directed = d;
        id = ((long)u.id << 32) | (long)v.id;
    }
    public Edge reversed(){
        return new Edge(v, u, directed);
    }
    public Node node(ETTree t){
        return nodes.get(t);
    }
    public void node(ETTree t, Node n){
        nodes.put(t, n);
    }
}

class AdjList {
    public HashMap<Vertex, HashMap<Vertex, Edge>> list = new HashMap<>();
    public void addVertex(Vertex v){
        list.put(v, new HashMap<>());
    }
    public void addEdge(Edge e){
        list.get(e.u).put(e.v, e);
        e.u.xor ^= e.id;
        if(!e.directed){
            list.get(e.v).put(e.u, e.reversed());
            e.v.xor ^= e.reversed().id;
        }
    }
    public void removeVertex(Vertex v){
        for(Edge e : list.get(v).values()){
            removeEdge(e);
        }
        list.remove(v);
    }
    public void removeEdge(Edge e){
        list.get(e.u).remove(e.v);
        e.u.xor ^= e.id;
        if(!e.directed){
            list.get(e.v).remove(e.u);
            e.v.xor ^= e.reversed().id;
        }
    }
    public void clear(){
        list.clear();
    }
}

class SplayTree {
    Node root;
    public void zig(Node p){
        Node x = p.right;
        if(x != null){
            p.right = x.left;
            if(x.left != null) x.left.parent = p;
            x.parent = p.parent;
        }
        if(p.parent == null) root = x;
        else if (p == p.parent.left) p.parent.left = x;
        else p.parent.right = x;
        if(x != null) {
            x.left = p;
            x.children += p.left.children + 1;
            p.children -= x.right.children + 1;
        }
        p.parent = x;
        p.height();
        x.height();
        p.xor();
        x.xor();
    }
    public void zag(Node p){
        Node x = p.left;
        if(x != null){
            p.left = x.right;
            if(x.right != null) x.right.parent = p;
            x.parent = p.parent;
        }
        if(p.parent == null) root = x;
        else if (p == p.parent.left) p.parent.left = x;
        else p.parent.right = x;
        if(x != null) {
            x.right = p;
            x.children += p.right.children + 1;
            p.children -= x.left.children + 1;
        }
        p.parent = x;
        p.height();
        x.height();
        p.xor();
        x.xor();
    }
    public void splay(Node x){
        if(x == null) return;
        while (x.parent != null){
            if (x.parent.parent == null) {
                if (x.parent.left == x) zag(x.parent);
                else zig(x.parent);
            } else if (x.parent.left == x && x.parent.parent.left == x.parent) {
                zag(x.parent.parent);
                zag(x.parent);
            } else if (x.parent.right == x && x.parent.parent.right == x.parent) {
                zig(x.parent.parent);
                zig(x.parent);
            } else if (x.parent.left == x && x.parent.parent.right == x.parent) {
                zag(x.parent);
                zig(x.parent);
            } else {
                zig(x.parent);
                zag(x.parent);
            }
        }
        root.tree = this;
    }
    public Node subMin(Node u){
        while(u.left != null) u = u.left;
        return u;
    }
    public Node subMax(Node u){
        while(u.right != null) u = u.right;
        return u;
    }
    public SplayTree(){
        root = null;
    }
    public void insertFront(Node x){
        if(root == null){
            root = x;
            return;
        }
        Node u = root;
        root.children++;
        while(u.left != null) {
            u = u.left;
            u.children++;
        }
        u.left = x;
        x.parent = u;
        x.height = 0;
        x.xor();
        while(u != null){
            u.xor();
            u.height();
            if(u.left.height - u.right.height >= 2){
                zag(u);
                u = u.parent;
            }
            else if(u.right.height - u.left.height >= 2){
                zig(u);
                u = u.parent;
            }
            u = u.parent;
        }
    }
    public void insertBack(Node x){
        if(root == null){
            root = x;
            return;
        }
        Node u = root;
        root.children++;
        while(u.right != null) {
            u = u.right;
            u.children++;
        }
        u.right = x;
        x.parent = u;
        x.height = 0;
        x.xor();
        while(u != null){
            u.xor();
            u.height();
            if(u.left.height - u.right.height >= 2){
                zag(u);
                u = u.parent;
            }
            else if(u.right.height - u.left.height >= 2){
                zig(u);
                u = u.parent;
            }
            u = u.parent;
        }
    }
    public void eraseFront() {
        if(root == null) return;
        zig(root);
        Node u = root;
        root.children--;
        while(u.left != null) {
            u = u.left;
            u.children--;
        }
        u.right.parent = u.parent;
        u.parent.left = u.right;
        u.parent = null;
        u.right = null;
        while(u != null){
            u.xor();
            u.height();
            if(u.left.height - u.right.height >= 2){
                zag(u);
                u = u.parent;
            }
            else if(u.right.height - u.left.height >= 2){
                zig(u);
                u = u.parent;
            }
            u = u.parent;
        }
    }
    public void eraseBack() {
        if(root == null) return;
        zag(root);
        Node u = root;
        root.children--;
        while(u.right != null) {
            u = u.right;
            u.children--;
        }
        u.left.parent = u.parent;
        u.parent.right = u.left;
        u.parent = null;
        u.left = null;
        while(u != null){
            u.xor();
            u.height();
            if(u.left.height - u.right.height >= 2){
                zag(u);
                u = u.parent;
            }
            else if(u.right.height - u.left.height >= 2){
                zig(u);
                u = u.parent;
            }
            u = u.parent;
        }
    }
    public SplayTree splitLeft(Node x){
        splay(x);
        SplayTree out = new SplayTree();
        out.root = root.left;
        root.left.parent = null;
        root.children -= root.left.children;
        root.left = null;
        out.root.tree = out;
        root.xor();
        return out;
    }
    public SplayTree splitRight(Node x){
        splay(x);
        SplayTree out = new SplayTree();
        out.root = root.right;
        root.right.parent = null;
        root.children -= root.right.children;
        root.right = null;
        out.root.tree = out;
        root.xor();
        root.height();
        return out;
    }
    public void joinLeft(SplayTree t){
        splay(min());
        root.left = t.root;
        root.children += root.left.children;
        t.root = null;
        root.xor();
    }
    public void joinRight(SplayTree t){
        splay(max());
        root.right = t.root;
        root.children += root.right.children;
        t.root = null;
        root.xor();
        root.height();
    }
    public Node min(){
        return subMin(root);
    }
    public Node max(){
        return subMax(root);
    }
    public boolean empty(){
        return root == null;
    }
    public int size(){
        return root.children;
    }
}

class ETTree {
    public SplayTree tour = new SplayTree();
    public AdjList graph;
    public ETTree(AdjList g){
        graph = g;
    }
    public ETTree(AdjList graph, Vertex root){
        this.graph = graph;
        Deque<Vertex> stack = new ArrayDeque<>();
        HashMap<Vertex, Boolean> visited = new HashMap<>();
        Vertex previous = null, current = root;
        for(Edge e : graph.list.get(root).values()){
            stack.push(root);
            stack.push(e.v);
        }
        visited.put(root, true);
        root.node(this, new Node(new Edge(root, root)));
        tour.insertBack(root.node(this));
        while(!stack.isEmpty()){
            previous = current;
            current = stack.pop();
            graph.list.get(previous).get(current).node(this, new Node(graph.list.get(previous).get(current)));
            tour.insertBack(graph.list.get(previous).get(current).node(this));
            for(Edge e : graph.list.get(current).values()){
                if(visited.get(e.v) == null){
                    stack.push(current);
                    stack.push(e.v);
                    visited.put(e.v, true);
                    e.v.node(this, new Node(new Edge(e.v, e.v)));
                    tour.insertBack(e.v.node(this));
                }
            };
        }
    }
    public Vertex findRoot(){
        return tour.min().value.u;
    }
    public void link(Edge e){
        SplayTree tree = e.v.node(this).root().tree.splitRight(e.v.node(this));
        SplayTree twee = e.u.node(this).root().tree.splitRight(e.u.node(this));
        e.node(this, new Node(e));
        tree.insertFront(e.node(this));
        e.u.node(this).root().tree.joinRight(tree);
        e.u.node(this).root().tree.joinRight(e.v.node(this).root().tree);
        graph.list.get(e.v).get(e.u).node(this, new Node(graph.list.get(e.v).get(e.u)));
        e.u.node(this).root().tree.insertBack(graph.list.get(e.v).get(e.u).node(this));
        e.u.node(this).root().tree.joinRight(twee);
    }
    public ETTree cut(Edge e){
        ETTree et = new ETTree(graph);
        SplayTree tree = tour.splitLeft(e.node(this));
        if(graph.list.get(e.v).get(e.u).node(this).root() == e.node(this).root()){
            tour.eraseFront();
            tree.joinRight(tour.splitRight(graph.list.get(e.v).get(e.u).node(this)));
            tour.eraseBack();
        } else {
            tour.eraseFront();
            tour.joinLeft(tree.splitLeft(graph.list.get(e.v).get(e.u).node(this)));
            tree.eraseFront();
        }
        et.tour = tree;
        return et;
    }
}

class CutSet {
    public AdjList list;
    public Seq<ETTree> forest = new Seq<>();
    public void insertTreeEdge(Edge e){

    }
    public void deleteTreeEdge(Edge e){

    }
    public void insertEdge(Edge e){

    }
    public void deleteEdge(Edge e){

    }
    public ETTree tree(Vertex v){
        return new ETTree(list);
    }
    public Edge outgoingEdge(ETTree t){
        return t.tour.root.value;
    }
}
public class SignalGraph {
    public AdjList graph;
    public int layers = 0;
    public Seq<CutSet> cutsets = new Seq<>();
    public void insert(Edge e){
        for(int i=0;i<layers;i++)
            cutsets.get(i).insertEdge(e);
        if(cutsets.get(layers-1).tree(e.u) != cutsets.get(layers-1).tree(e.v))
            for(int i=0;i<layers;i++)
            cutsets.get(i).insertTreeEdge(e);
    }
    public void delete(Edge e){
        for(int i=0;i<layers;i++)
            cutsets.get(i).deleteEdge(e);
        for(int i=0;i<layers;i++)
            cutsets.get(i);
    }
    public void update(Edge e){

    }
}