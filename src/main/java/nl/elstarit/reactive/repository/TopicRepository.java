package nl.elstarit.reactive.repository;

import nl.elstarit.reactive.model.Topic;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TopicRepository extends ReactiveMongoRepository<Topic, String> {
    public Mono<Topic> findByName(String name);

}
