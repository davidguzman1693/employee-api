package com.coding.challenge.database.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "HOBBY",
    uniqueConstraints = @UniqueConstraint(
        name = "UK_HOBBY",
        columnNames = "ID"
    ))
@Getter
@Setter
public class HobbyEntity {

  @Id
  @GenericGenerator(name = "sequence_imei_id", strategy = "com.coding.challenge.database.entity.ImeiIdGenerator")
  @GeneratedValue(generator = "sequence_imei_id")
  @Column(name = "ID")
  private String id;

  @Column(name = "HOBBY_NAME", nullable = false)
  private String name;
}
