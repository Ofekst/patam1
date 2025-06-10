package test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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


    /**
     * Checks if specific node's edges have cycles
     *
     * @param currentNode Current node to check
     * @param passedNodes Set of all passed nodes
     * @param  recursionStack Set of the nodes that need to be checked
     * @return True if node has cycles, else False
     */
    private boolean checkCycles(Node currentNode, Set<Node> passedNodes,
                                Set<Node> recursionStack){
    passedNodes.add(currentNode);
    recursionStack.add(currentNode);

    for (Node node: currentNode.edges){
        if(!passedNodes.contains(node)){
            if (checkCycles(node,passedNodes,recursionStack)){
                return true;
            }
        }
        else if (recursionStack.contains(node)) {
            return true;

        }

    }
        recursionStack.remove(currentNode);
        return false;
    }

    /**
     * Checks if node has cycles
     *
     * @return True if node has cycles, else False
     */
    public boolean hasCycles(){
        Set<Node> passedNodes = new HashSet<Node>();
        Set<Node> recursionNodes = new HashSet<Node>();
        return checkCycles(this, passedNodes, recursionNodes);
    }
}