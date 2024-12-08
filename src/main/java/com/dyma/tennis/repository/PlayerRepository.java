package com.dyma.tennis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dyma.tennis.repository.entity.PlayerEntity;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

  public Optional<PlayerEntity> findOneByLastNameIgnoreCase(String lastName);
}
