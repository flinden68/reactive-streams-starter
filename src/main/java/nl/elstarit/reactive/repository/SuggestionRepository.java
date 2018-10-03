package nl.elstarit.reactive.repository;

import nl.elstarit.reactive.model.Suggestion;
import nl.elstarit.reactive.model.Topic;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SuggestionRepository extends ReactiveMongoRepository<Suggestion, String> {
    public Mono<Suggestion> findByName(String name);

}
