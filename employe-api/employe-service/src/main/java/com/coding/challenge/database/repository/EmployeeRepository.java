package com.coding.challenge.database.repository;

import com.coding.challenge.database.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Employee repository.
 *
 * @author dguzman.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, String> {
  List<EmployeeEntity> findByEmail(String email);
}
