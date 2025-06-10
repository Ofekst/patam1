package test;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TopicManagerSingleton {

    public static class TopicManager{

        private static final TopicManager instance = new TopicManager();

        Map<String,Topic> topics;

        /**
         * Constructs a TopicManager .
         *
         */
        private TopicManager(){
            this.topics = new HashMap<>();
        }

        /**
         * Function gets topic name and if it does not exist
         * in map, it will create it.Then, it will return
         * the topic
         *
         * @param topicName the message to publish
         * @return The requested topic
         */
        public synchronized Topic getTopic(String topicName){
            if (!this.topics.containsKey(topicName)){
                this.topics.put(topicName,new Topic(topicName));
            }
            return this.topics.get(topicName);
        }


        /**
         * Function returns all current topics in map
         *
         * @return All existing topics
         */
        public Collection<Topic> getTopics(){
            return this.topics.values();
        }

        /**
         * Function clears all topic's data
         *
         */
        public void clear(){
            this.topics.clear();
        }
    }
    public static TopicManager get(){
        return TopicManager.instance;
    }


}
