package test;

import test.TopicManagerSingleton.TopicManager;

public class PlusAgent implements Agent{
    private final String [] subs;
    private final String [] pubs;

    private final TopicManager topicManager;
    private Double x;
    private Double y;

    public PlusAgent (String [] subs, String [] pubs){
        this.subs = subs;
        this.pubs = pubs;
        this.topicManager = TopicManagerSingleton.get();

        if (this.subs.length == 1){
          this.topicManager.getTopic(this.subs[0]).subscribe(this);
        }
        else if (this.subs.length > 1) {
            this.topicManager.getTopic(this.subs[0]).subscribe(this);
            this.topicManager.getTopic(this.subs[1]).subscribe(this);
        }

        if (this.pubs.length != 0){
            this.topicManager.getTopic(this.pubs[0]).addPublisher(this);
        }
    }
    @Override
    public String getName() {
        return "PlusAgent";
    }

    @Override
    public void reset() {
        this.x = Double.NaN;
        this.y = Double.NaN;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (topic.equals(this.subs[0])){
            this.x = msg.asDouble;
        }
        else if (subs.length > 1 && topic.equals(this.subs[1])) {
            this.y = msg.asDouble;
        }

        if (this.x!= null && this.y!=null){
            double result = x + y;
            this.topicManager.getTopic(this.pubs[0]).publish(new Message(result));
        }
    }

    @Override
    public void close() {
    try {
        this.topicManager.getTopic(this.subs[0]).unsubscribe(this);
        this.topicManager.getTopic(this.subs[1]).unsubscribe(this);
        this.topicManager.getTopic(this.pubs[0]).removePublisher(this);
    }
    catch (Exception e)
    {
        System.out.println("The error you got is: " + e.getMessage());
    }
    }
}
