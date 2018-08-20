package nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.repository;

import nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.model.Topic;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TopicRepository extends ReactiveMongoRepository<Topic, String> {
    public Mono<Topic> findByName(String name);

}
