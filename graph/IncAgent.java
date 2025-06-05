package graph;

import graph.TopicManagerSingleton.TopicManager;

public class IncAgent implements Agent {

    private final String [] subs;
    private final String [] pubs;

    private final TopicManager topicManager;
    private Double x;

    public IncAgent(String [] subs, String [] pubs){
        this.subs = subs;
        this.pubs = pubs;
        this.topicManager = TopicManagerSingleton.get();

        if (this.subs.length != 0){
            this.topicManager.getTopic(this.subs[0]).subscribe(this);
        }

        if (this.pubs.length != 0){
            this.topicManager.getTopic(this.pubs[0]).addPublisher(this);
        }

    }
    @Override
    public String getName() {
        return "IncAgent";
    }

    @Override
    public void reset() {
        this.x = Double.NaN;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(this.subs[0])){
            this.x = msg.asDouble;
        }
        if (!x.isNaN()){
            x+=1;
            this.topicManager.getTopic(this.pubs[0]).publish(new Message(x));
        }
    }

    @Override
    public void close() {
        try {
            this.topicManager.getTopic(this.subs[0]).unsubscribe(this);
            this.topicManager.getTopic(this.pubs[0]).removePublisher(this);
        }
        catch (Exception e) {
            System.out.println("The error you got is: " + e.getMessage());
        }
    }
}