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
import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof HobbyEntity)) return false;

    HobbyEntity that = (HobbyEntity) o;

    if (!Objects.equals(id, that.id)) return false;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }


}
