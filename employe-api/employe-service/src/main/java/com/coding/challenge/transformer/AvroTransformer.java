package com.coding.challenge.transformer;

import com.coding.challenge.kafka.avro.model.EmployeeAvroModel;
import com.coding.challenge.kafka.avro.model.HobbyAvroModel;
import com.coding.challenge.model.Employee;
import com.coding.challenge.model.Hobby;
import com.coding.challenge.service.EmployeeOperation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class AvroTransformer {

  public EmployeeAvroModel getAvroModelFromEmployee(Employee employee,
                                                    EmployeeOperation operation) {

    List<HobbyAvroModel> hobbyAvroModels = new ArrayList<>();
    Set<Hobby> hobbies = employee.getHobbies();
    if (hobbies != null) {
      for (Hobby hobby : hobbies) {
        HobbyAvroModel hobbyAvroModel = HobbyAvroModel.newBuilder()
            .setId(hobby.getId())
            .setName(hobby.getName())
            .build();
        hobbyAvroModels.add(hobbyAvroModel);
      }
    }
    return EmployeeAvroModel.newBuilder()
        .setUuid(employee.getUuid())
        .setEmail(employee.getEmail())
        .setFullName(employee.getFullName())
        .setBirthday(employee.getBirthday().toEpochDay())
        .setHobbies(hobbyAvroModels)
        .setOperation(operation.name())
        .build();
  }
}
