package com.coding.challenge.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Data
@NoArgsConstructor
@Getter
@Setter
public class Hobby {
  private String id;
  private String name;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Hobby)) return false;

    Hobby hobby = (Hobby) o;

    if (!Objects.equals(id, hobby.id)) return false;
    return Objects.equals(name, hobby.name);
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }
}
