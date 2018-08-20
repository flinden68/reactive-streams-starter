package nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableMongoAuditing
@EnableReactiveMongoRepositories
public class TopicManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TopicManagementServiceApplication.class, args);
	}
}
