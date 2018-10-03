package nl.elstarit.reactive.handler;

import lombok.extern.slf4j.Slf4j;
import nl.elstarit.reactive.model.Suggestion;
import nl.elstarit.reactive.model.Topic;
import nl.elstarit.reactive.repository.SuggestionRepository;
import nl.elstarit.reactive.repository.TopicRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SuggestionHandler {

  private SuggestionRepository suggestionRepository;

  public SuggestionHandler(SuggestionRepository suggestionRepository){
      this.suggestionRepository = suggestionRepository;
  }

  public Mono<ServerResponse> allSuggestions(ServerRequest req) {
    return ServerResponse.ok().body(suggestionRepository.findAll(), Suggestion.class);
  }

  public Mono<ServerResponse> suggestionByName(ServerRequest req) {
    return ServerResponse.ok().body(suggestionRepository.findByName(req.pathVariable("name")), Suggestion.class);
  }
}
