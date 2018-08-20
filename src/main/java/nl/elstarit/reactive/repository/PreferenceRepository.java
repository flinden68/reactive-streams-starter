package nl.elstarit.reactive.repository;

import nl.elstarit.reactive.model.Preference;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PreferenceRepository extends ReactiveMongoRepository<Preference, String> {

    public Mono<Preference> findByUserid(String userid);
    
}
