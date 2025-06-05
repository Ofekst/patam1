package graph;

public class ParallelAgent implements Agent{
    private final Agent agent;

    public ParallelAgent (Agent agent){
        this.agent = agent;
    }
    @Override
    public String getName() {
        return this.agent.getName();
    }

    @Override
    public void reset() {
        this.agent.reset();
    }

    @Override
    public void callback(String topic, Message msg) {
    this.agent.callback(topic,msg);
    }

    @Override
    public void close() {
        agent.close();
    }
}
