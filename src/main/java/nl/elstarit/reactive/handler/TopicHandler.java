package nl.elstarit.reactive.handler;

import lombok.extern.slf4j.Slf4j;
import nl.elstarit.reactive.model.Topic;
import nl.elstarit.reactive.repository.TopicRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class TopicHandler {

  private TopicRepository topicRepository;

  public TopicHandler(TopicRepository topicRepository){
      this.topicRepository = topicRepository;
  }

  public Mono<ServerResponse> allTopics(ServerRequest req) {
    log.info("BOE");
    return ServerResponse.ok().body(topicRepository.findAll(), Topic.class);
  }

  public Mono<ServerResponse> topicByName(ServerRequest req) {
    return ServerResponse.ok().body(topicRepository.findByName(req.pathVariable("name")), Topic.class);
  }
}
