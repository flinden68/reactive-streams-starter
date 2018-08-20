package nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.controller;

import nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.model.Topic;
import nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.repository.TopicRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TopicControllerTest {

    private WebTestClient webTestClient;

    @Autowired
    TopicRepository topicRepository;

    static final int QTY = 20;

    @Before
    public void setUp() throws Exception {
        webTestClient = WebTestClient.bindToController(new TopicController(topicRepository)).build();
        topicRepository.deleteAll();

        for (int i = 0; i < QTY; i++) {
            Topic topic = new Topic();
            topic.setName("Test" + i);

            topicRepository.save(topic);
        }
    }

    @Test
    public void streamAllTweets() {
        webTestClient.get().uri("/sector-knowledge/topic-management-service/topic/stream")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Topic.class);
    }

    @Test
    public void getAllTopics() {
        webTestClient.get().uri("/sector-knowledge/topic-management-service/topic/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Topic.class);
    }

    @Test
    public void getTopicByName() {
        Topic topic = new Topic();
        topic.setName("test9991111");

        topicRepository.save(topic);

        webTestClient.get().uri("/sector-knowledge/topic-management-service/topic/test9991111")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("test9991111");
    }

    @Test
    public void createTopic() {
        Topic topic = new Topic();
        topic.setName("testCreate");
        webTestClient.post().uri("/sector-knowledge/topic-management-service/topic/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(topic), Topic.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("testCreate");
    }

    @Test
    public void deleteTopic() {
        Topic topic = new Topic();
        topic.setName("11111111");

        topicRepository.save(topic);

        webTestClient.delete().uri("/sector-knowledge/topic-management-service/topic/delete/test9991112")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void deleteAllTopics() {
        webTestClient.delete().uri("/sector-knowledge/topic-management-service/topic/delete/all")
                .exchange()
                .expectStatus().isOk();
    }
}