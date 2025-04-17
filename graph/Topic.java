package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Topic in a publish-subscribe system.
 * A Topic can have multiple publishers and subscribers.
 * Publishers can send messages to the topic, and subscribers
 * receive those messages via a callback.
 */

public class Topic {
    public final String name;
    private List<Agent> subs;
    private List<Agent> pubs;

    /**
     * Constructs a Topic with the specified name.
     *
     * @param name the name of the topic
     */
    Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
    }

    /**
     * Subscribes an agent to this topic.
     *
     * @param a the agent to subscribe
     */
    public void subscribe(Agent a) {
        this.subs.add(a);
    }

    /**
     * Unsubscribes an agent from this topic.
     *
     * @param a the agent to unsubscribe
     */
    public void unsubscribe(Agent a) {
        this.subs.remove(a);
    }

    /**
     * Publishes a message to all subscribers of this topic.
     * Each subscriber receives the message through its callback method.
     *
     * @param m the message to publish
     */
    public void publish(Message m) {
        subs.parallelStream().forEach(a -> a.callback(this.name, m));
    }

    /**
     * Registers an agent as a publisher to this topic.
     *
     * @param a the agent to add as a publisher
     */
    public void addPublisher(Agent a) {
        this.pubs.add(a);
    }

    /**
     * Removes an agent from the list of publishers of this topic.
     *
     * @param a the agent to remove
     */
    public void removePublisher(Agent a) {
        this.pubs.remove(a);
    }

    /**
     * Returns the list of subscribers for this topic.
     *
     * @return a list of subscribed agents
     */
    public List<Agent> getSubs() {
        return subs;
    }

    /**
     * Returns the list of publishers for this topic.
     *
     * @return a list of publishing agents
     */
    public List<Agent> getPubs() {
        return pubs;
    }

}
