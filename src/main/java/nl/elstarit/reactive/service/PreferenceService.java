package nl.elstarit.reactive.service;

import lombok.extern.slf4j.Slf4j;
import nl.elstarit.reactive.model.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class PreferenceService {

  @Value("${topic-management.url}")
  private String topicManagementUrl;

  private final WebClient webClient;

  public PreferenceService() {
    this.webClient = WebClient.create();
  }

  //@HystrixCommand(fallbackMethod = "fallbackFindPreferenceById")
  public Mono<Preference> findPreferenceById(String userId) {
    log.info("Get Preferences for user: {}", userId);
    return this.webClient
      .get()
      .uri(topicManagementUrl+"/{userId}", userId)
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError, clientResponse ->
        Mono.error(new RuntimeException("Preferences could be found: " + userId))
      )
      .onStatus(HttpStatus::is5xxServerError, clientResponse ->
        Mono.error(new RuntimeException("Something went wrong: " + userId))
      )
      .bodyToMono(Preference.class);
  }

  //@HystrixCommand(fallbackMethod = "fallbackSavePreference")
  public Mono<Preference> savePreferences(Preference preference){

    log.debug("Save Preference: {}", preference);
    return this.webClient
      .post()
      .uri(topicManagementUrl+"/save")
      .body(Mono.just(preference), Preference.class)
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError, clientResponse ->
        Mono.error(new RuntimeException("Preferences could be found: " + preference.getUserid()))
      )
      .onStatus(HttpStatus::is5xxServerError, clientResponse ->
        Mono.error(new RuntimeException("Something went wrong: " + preference.getUserid()))
      )
      .bodyToMono(Preference.class);
  }

  public Mono<String> deletePreferences(String userId){

    log.info("Delete Preference for user: {}", userId);
    log.info("Delete URL = {}", topicManagementUrl+"/delete/"+userId);
    return this.webClient
      .delete()
      .uri(topicManagementUrl+"/delete/{userId}", userId)
      .retrieve()
      .onStatus(HttpStatus::is4xxClientError, clientResponse ->
        Mono.error(new RuntimeException("Preferences could be deleted: " + userId))
      )
      .onStatus(HttpStatus::is5xxServerError, clientResponse ->
        Mono.error(new RuntimeException("Something went wrong: " + userId))
      )
      .bodyToMono(String.class);
  }

  //Fall back methods
  public Mono<Preference> fallbackFindPreferenceById(String userId) {
    Preference preference = new Preference();
    preference.setId(userId);
    return Mono.just(preference);
  }

  public Mono<Preference> fallbackSavePreference(Preference preference){
    return Mono.just(preference);
  }

  public Mono<String> fallbackDeletePreferenceById(String userId) {
    return Mono.just(HttpStatus.SERVICE_UNAVAILABLE.toString());
  }

}
