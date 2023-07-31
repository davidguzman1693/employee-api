package com.coding.challenge.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
public class Employee {
  @Nullable
  private String uuid;
  @NonNull
  private String email;
  @NonNull
  private String fullName;
  @NonNull
  private LocalDate birthday;
  @JsonFormat(pattern = "yyyy-MM-dd")
  @NonNull
  Set<Hobby> hobbies = new HashSet<>();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Employee)) return false;

    Employee employee = (Employee) o;

    if (!Objects.equals(uuid, employee.uuid)) return false;
    if (!Objects.equals(email, employee.email)) return false;
    if (!fullName.equals(employee.fullName)) return false;
    if (!birthday.equals(employee.birthday)) return false;
    return hobbies.equals(employee.hobbies);
  }

  @Override
  public int hashCode() {
    int result = uuid != null ? uuid.hashCode() : 0;
    result = 31 * result + (email != null ? email.hashCode() : 0);
    result = 31 * result + fullName.hashCode();
    result = 31 * result + birthday.hashCode();
    result = 31 * result + hobbies.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "Employee{" +
        "uuid='" + uuid + '\'' +
        ", email='" + email + '\'' +
        ", fullName='" + fullName + '\'' +
        ", birthday=" + birthday +
        ", hobbies=" + hobbies +
        '}';
  }
}
