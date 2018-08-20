package nl.elstarit.reactive.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Preference extends BaseEntity{

    private String userid;
    private List<Topic> topics;
}
