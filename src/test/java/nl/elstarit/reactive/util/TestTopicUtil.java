package nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.util;

import nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.model.Topic;

import java.util.ArrayList;
import java.util.List;

public class TestTopicUtil {

    public static List<Topic> constructTopicsList(int amoumt){
        List<Topic> topics = new ArrayList<>();
        for (int i = 0; i < amoumt; i++) {
            Topic topic = new Topic();
            topic.setName("Test" + i);
            topic.setId("10000" + i);

            topics.add(topic);
        }
        return topics;
    }
}
