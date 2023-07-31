package com.coding.challenge.database.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(
    name = "EMPLOYEE",
    uniqueConstraints = {@UniqueConstraint(
        name = "UK_EMPLOYEE",
        columnNames = "UUID"
    ),
        @UniqueConstraint(
            name = "UK_EMAIL",
            columnNames = "EMAIL"
        )}
)
@Getter
@Setter
public class EmployeeEntity {

  @Id
  @GenericGenerator(name = "sequence_imei_id", strategy = "com.coding.challenge.database.entity.ImeiIdGenerator")
  @GeneratedValue(generator = "sequence_imei_id")
  @Column(name = "UUID", nullable = false)
  private String uuid;

  @Column(name = "FULLNAME", nullable = false)
  private String fullName;

  @Column(name = "EMAIL", nullable = false)
  private String email;

  @Column(name = "BIRTHDAY", nullable = false)
  private LocalDate birthday;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(
      name = "EMPLOYEE_HOBBY",
      joinColumns = {
          @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "UUID", nullable = false)
      },
      inverseJoinColumns = {
          @JoinColumn(name = "HOBBY_ID", referencedColumnName = "ID", nullable = false)
      }
  )
  private Set<HobbyEntity> hobbies;
}
