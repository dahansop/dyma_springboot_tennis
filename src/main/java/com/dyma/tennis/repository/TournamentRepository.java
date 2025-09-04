package com.dyma.tennis.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dyma.tennis.data.TournamentEntity;

@Repository
public interface TournamentRepository extends JpaRepository<TournamentEntity, Long> {

  public Optional<TournamentEntity> findOneByIdentifier(UUID identifier);

  public Optional<TournamentEntity> findOneByNameIgnoreCase(String name);

}
