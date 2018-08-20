package nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Topic extends BaseEntity {

    private String name;
}
