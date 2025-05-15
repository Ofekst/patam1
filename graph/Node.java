package graph;

import java.util.ArrayList;
import java.util.List;


public class Node {
    private String name;
    private List<Node> edges;
    private Message msg;

    /**
     * Constructs a Node with the specified name.
     *
     * @param node_name the name of the node
     */
    public Node (String node_name){
        this.name=node_name;
        this.edges = new ArrayList<Node>();
        this.msg=null;
    }


    // Getter functions

    public String getName() {
        return name;
    }

    public List<Node> getEdges() {
        return edges;
    }

    public Message getMsg() {
        return msg;
    }

    // Setter functions

    public void setName(String node_name) {
        this.name = node_name;
    }

    public void setEdges(List<Node> edges) {
        this.edges = edges;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    // Extra functions

    /**
     * Add new node to the list (if it does not exist).
     *
     * @param node the node to add to our list
     */
    public void addEdge(Node node){
        if (!edges.contains(node)){
            this.edges.add(node);
        }
    }
    //TODO: Need to Add the function hasCycles()

}