package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import graph.TopicManagerSingleton.TopicManager;

public class Graph extends ArrayList<Node>{


    /**
     * Checks if graph has cycles
     *
     * @return True if graph has cycles, else False
     */
    public boolean hasCycles() {
        for (Node node : this){
            if (node.hasCycles()){
                return true;
            }
        }
        return false;
    }
    /**
     * Creates graph from all topics and agents
     *
     */

    public void createFromTopics() {
        TopicManager topicManager = TopicManagerSingleton.get();
        Map<String, Node> graph = new HashMap<>();

        for (Topic topic : topicManager.getTopics()) {
            String topicName = topic.name;
            boolean isEmpty = topic.getSubs().isEmpty() && topic.getPubs().isEmpty();
            String nodeName = (isEmpty ? "A" : "T") + topicName;

            Node currentNode = graph.computeIfAbsent(nodeName, Node::new);

            // from topic to all agents
            for (Agent pub : topic.getPubs()) {
                String agentNodeName = "A" + pub.getName();
                Node agentNode = graph.computeIfAbsent(agentNodeName, Node::new);
                currentNode.addEdge(agentNode);
            }

            // from agent to all topics
            for (Agent sub : topic.getSubs()) {
                String agentNodeName = "A" + sub.getName();
                Node agentNode = graph.computeIfAbsent(agentNodeName, Node::new);
                agentNode.addEdge(currentNode);
            }


        }

        this.addAll(graph.values());
    }


}
