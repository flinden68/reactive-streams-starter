package nl.elstarit.reactive.controller;

import nl.elstarit.reactive.model.Topic;
import nl.elstarit.reactive.repository.TopicRepository;
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
        //topicRepository.deleteAll();

        for (int i = 0; i < QTY; i++) {
            Topic topic = new Topic();
            topic.setName("Test" + i);

            Mono newTopic = topicRepository.save(topic);
        }
    }

    @Test
    public void streamAllTweets() {
        webTestClient.get().uri("/reactive/streams/topic/stream")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Topic.class);
    }

    @Test
    public void getAllTopics() {
        webTestClient.get().uri("/reactive/streams/topic/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Topic.class);
    }

    //@Test
    public void getTopicByName() {
        Topic topic = new Topic();
        topic.setName("testCreate");

        Mono newTopic =  topicRepository.save(topic);

        webTestClient.get().uri("/reactive/streams/topic/testCreate")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.name").isEqualTo("testCreate");
    }

    @Test
    public void createTopic() {
        Topic topic = new Topic();
        topic.setName("testCreate");
        webTestClient.post().uri("/reactive/streams/topic/create")
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

        webTestClient.delete().uri("/reactive/streams/topic/delete/11111111")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    public void deleteAllTopics() {
        webTestClient.delete().uri("/reactive/streams/topic/delete/all")
                .exchange()
                .expectStatus().isOk();
    }
}