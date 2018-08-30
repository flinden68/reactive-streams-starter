package nl.elstarit.reactive.router;

import nl.elstarit.reactive.handler.TopicHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class TopicRouter {

  @Bean
  public RouterFunction<ServerResponse> routes(TopicHandler topicHandler) {
    return
      route(GET("/reactive/streams/router/topic/all"), topicHandler::allTopics)
      .andRoute(GET("/reactive/streams/router/topic/{name}"), topicHandler::topicByName);
  }
}
