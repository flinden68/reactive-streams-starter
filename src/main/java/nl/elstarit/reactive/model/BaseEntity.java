package nl.rabobank.gict.crmvirtual.relevance.sectorknowledge.topicmanagementservice.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Data
public abstract class BaseEntity {

  @Id
  protected String id;

  @CreatedDate
  protected LocalDateTime created;

  @LastModifiedDate
  protected LocalDateTime modified;

}
