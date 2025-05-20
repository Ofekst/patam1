package graph;

import java.util.function.BinaryOperator;

import graph.TopicManagerSingleton.TopicManager;

public class BinOpAgent implements Agent{
    private final String agentName;
    private final String firstTopicName;
    private final String secondTopicName;
    private final String outputTopicName;
    private final BinaryOperator<Double> function;
    private TopicManager topicManager;
    private Double firstMessage;
    private Double secondMessage;

    /**
     * This is our main constructor
     *
     * @param agentName - Our agent name
     * @param firstTopicName - The first topic in formula
     * @param secondTopicName - The second topic in formula
     * @param outputTopicName - The topic that will contain the result of calculation
     * @param function - The math function we want to run
     */
    public BinOpAgent(String agentName, String firstTopicName,String secondTopicName,
                      String outputTopicName,BinaryOperator<Double> function){
        this.agentName = agentName;
        this.firstTopicName = firstTopicName;
        this.secondTopicName = secondTopicName;
        this.outputTopicName = outputTopicName;
        this.function = function;

        this.topicManager = TopicManagerSingleton.get();
        this.topicManager.getTopic(this.firstTopicName).subscribe(this);
        this.topicManager.getTopic(this.secondTopicName).subscribe(this);
        this.topicManager.getTopic(this.outputTopicName).addPublisher(this);

        this.topicManager.getTopic(agentName);
    }


    /**
     *
     * The function returns the name of our agent
     *
     * @return The name of our agent
     */
    @Override
    public String getName() {
        return agentName;
    }
    /**
     *
     * The function resets the input topics
     *
     */
    @Override
    public void reset() {

        this.topicManager = TopicManagerSingleton.get();
        this.topicManager.getTopic(this.firstTopicName).publish(new Message(0));
        this.topicManager.getTopic(this.secondTopicName).publish(new Message(0));
    }

    /**
     *
     * The function execute the math formula between the messages
     *
     * @param topic - The topic that contain the message
     * @param msg - The parameter to calculate
     */
    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(this.firstTopicName)){
            this.firstMessage = msg.asDouble;
        }
        else if (topic.equals(this.secondTopicName)) {
            this.secondMessage = msg.asDouble;
        }
        if (this.firstMessage != null && this.secondMessage != null){
            Double result = this.function.apply(this.firstMessage,this.secondMessage);
            this.topicManager.getTopic(this.outputTopicName).publish(new Message(result));
        }
    }

    @Override
    public void close() {

    }
}


