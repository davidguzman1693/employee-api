package com.coding.challenge.database.repository;

import com.coding.challenge.database.entity.HobbyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HobbyRepository extends JpaRepository<HobbyEntity, String> {

  HobbyEntity findByName(String name);

}
