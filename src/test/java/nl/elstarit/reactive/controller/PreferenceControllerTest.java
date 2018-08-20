package nl.elstarit.reactive.controller;

import nl.elstarit.reactive.model.Preference;
import nl.elstarit.reactive.repository.PreferenceRepository;
import nl.elstarit.reactive.util.TestTopicUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PreferenceControllerTest {

    private WebTestClient webTestClient;

    @Autowired
    PreferenceRepository preferenceRepository;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Before
    public void setUp() throws Exception {
        webTestClient = WebTestClient.bindToController(new PreferenceController(preferenceRepository)).build();
        preferenceRepository.deleteAll();
    }

    @Test
    public void getPreferenceById() {
        Preference preference = new Preference();
        preference.setUserid("d367b9e61e6e2ec0c229c45d681538107697704f7f36bcd6bab5283d4070bac3Id");
        preference.setTopics(TestTopicUtil.constructTopicsList(2));

        preferenceRepository.save(preference).block();

        webTestClient.get().uri("/reactive/streams/preference/d367b9e61e6e2ec0c229c45d681538107697704f7f36bcd6bab5283d4070bac3Id")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.userid").isEqualTo("d367b9e61e6e2ec0c229c45d681538107697704f7f36bcd6bab5283d4070bac3Id");
    }

    @Test
    public void getAllPreferences() {
        webTestClient.get().uri("/reactive/streams/preference/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Preference.class);
    }

    @Test
    public void savePreference() {
        Preference preference = new Preference();
        preference.setUserid("d367b9e61e6e2ec0c229c45d681538107697704f7f36bcd6bab5283d4070bac3Save1");
        preference.setTopics(TestTopicUtil.constructTopicsList(2));

        webTestClient.post().uri("/reactive/streams/preference/save")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(preference), Preference.class)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.userid").isEqualTo("d367b9e61e6e2ec0c229c45d681538107697704f7f36bcd6bab5283d4070bac3Save1");

    }

    @Test
    public void deletePreference() {
        Preference preference = new Preference();
        preference.setUserid("d367b9e61e6e2ec0c229c45d681538107697704f7f36bcd6bab5283d4070bac3Delete1");
        preference.setTopics(TestTopicUtil.constructTopicsList(2));

        preferenceRepository.save(preference).block();

        webTestClient.delete().uri("/reactive/streams/preference/delete/d367b9e61e6e2ec0c229c45d681538107697704f7f36bcd6bab5283d4070bac3Delete1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void deleteAllPreferences() {
        webTestClient.delete().uri("/reactive/streams/preference/delete/all")
                .exchange()
                .expectStatus().isOk();
    }

}