package nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.repository;

import nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.model.Preference;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PreferenceRepository extends ReactiveMongoRepository<Preference, String> {

    public Mono<Preference> findByUserid(String userid);
    
}
