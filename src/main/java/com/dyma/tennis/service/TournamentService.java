package com.dyma.tennis.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.dyma.tennis.data.TournamentEntity;
import com.dyma.tennis.exceptions.TournamentAlreadyExistsException;
import com.dyma.tennis.exceptions.TournamentDataRetrievalException;
import com.dyma.tennis.exceptions.TournamentNotFoundException;
import com.dyma.tennis.mapper.TournamentMapper;
import com.dyma.tennis.model.Tournament;
import com.dyma.tennis.model.TournamentToCreate;
import com.dyma.tennis.model.TournamentToUpdate;
import com.dyma.tennis.repository.TournamentRepository;

@Service
public class TournamentService {

  private final Logger LOGGER = LoggerFactory.getLogger(TournamentService.class);

  @Autowired
  private final TournamentRepository tournamentRepository;

  @Autowired
  private final TournamentMapper tournamentMapper;

  /**
   * Constructeur
   * @param tournamentRepository
   */
  public TournamentService(TournamentRepository tournamentRepository, TournamentMapper tournamentMapper) {
    this.tournamentRepository = tournamentRepository;
    this.tournamentMapper = tournamentMapper;
  }

  /**
   * Récupère la liste de tout les tournois
   * @return liste des tournois
   */
  public List<Tournament> getAllTournaments() {
    LOGGER.info("Invoking getAllTournaments()");
    try {
      return tournamentRepository.findAll().stream()
          .map(tournamentMapper::tournamentEntityToTournament)
          .collect(Collectors.toList());
    } catch (DataAccessException e) {
      LOGGER.error("Could not retrieve tournaments", e);
      throw new TournamentDataRetrievalException(e);
    }
  }

  /**
   * Recherche un tournois à partir de son identifiant
   * @param identifier identifiant unique du tournois
   * @return tournois
   */
  public Tournament getByIdentifier(UUID identifier) {
    LOGGER.info("Invoking getByIdentifier() with identifier={}", identifier);
    try {
      Optional<TournamentEntity> tournamentEntity = tournamentRepository.findOneByIdentifier(identifier);

      if (tournamentEntity.isEmpty()) {
        LOGGER.warn("Could not find tournament with identifier={}", identifier);
        throw new TournamentNotFoundException(identifier);
      }

      return tournamentMapper.tournamentEntityToTournament(tournamentEntity.get());
    } catch (DataAccessException e) {
      LOGGER.error("Could not find tournament with identifier={}", identifier, e);
      throw new TournamentDataRetrievalException(e);
    }
  }

  /**
   * Permet d'ajouter un tournois à la liste
   * @param tournamentToCreate tournois à créer
   * @return tournois
   */
  public Tournament create(TournamentToCreate tournamentToCreate) {
    LOGGER.info("Invoking create() with tournamentToCreate={}", tournamentToCreate);
    try {
      Optional<TournamentEntity> tournamentAlreadyExists = tournamentRepository.findOneByNameIgnoreCase(tournamentToCreate.name());
      if (tournamentAlreadyExists.isPresent()) {
        LOGGER.warn("Tournament to create with name={} already exists", tournamentToCreate.name());
        throw new TournamentAlreadyExistsException(tournamentToCreate.name());
      }

      TournamentEntity tournamentEntity = new TournamentEntity(
          UUID.randomUUID(),
          tournamentToCreate.name(),
          tournamentToCreate.startDate(),
          tournamentToCreate.endDate(),
          tournamentToCreate.prizeMoney(),
          tournamentToCreate.capacity());
      TournamentEntity registredTournament = tournamentRepository.save(tournamentEntity);

      return this.getByIdentifier(registredTournament.getIdentifier());
    } catch (DataAccessException e) {
      LOGGER.error("Could not create tournament {}", tournamentToCreate, e);
      throw new TournamentDataRetrievalException(e);
    }
  }

  /**
   * Permet de mettre à jour un tournois
   * @param tournamentToUpdate trounois à mettre à jour
   * @return tournois mis à jour
   */
  public Tournament update(TournamentToUpdate tournamentToUpdate) {
    LOGGER.info("Invoking update() with tournamentToUpdate={}", tournamentToUpdate);
    try {
      Optional<TournamentEntity> tournamentEntity = tournamentRepository.findOneByIdentifier(tournamentToUpdate.identifier());
      if (tournamentEntity.isEmpty()) {
        LOGGER.warn("Could not find tournament to update with identifier={}", tournamentToUpdate.identifier());
        throw new TournamentNotFoundException(tournamentToUpdate.identifier());
      }

      Optional<TournamentEntity> potentiallyDuplicatedtournament = tournamentRepository.findOneByNameIgnoreCase(tournamentToUpdate.name());
      if (potentiallyDuplicatedtournament.isPresent() &&
          !potentiallyDuplicatedtournament.get().getIdentifier().equals(tournamentToUpdate.identifier())) {
        LOGGER.warn("Tournament to update with name={} already exists", tournamentToUpdate.name());
        throw new TournamentAlreadyExistsException(tournamentToUpdate.name());
      }

      tournamentEntity.get().setName(tournamentToUpdate.name());
      tournamentEntity.get().setStartDate(tournamentToUpdate.startDate());
      tournamentEntity.get().setEndDate(tournamentToUpdate.endDate());
      tournamentEntity.get().setPrizeMoney(tournamentToUpdate.prizeMoney());
      tournamentEntity.get().setCapacity(tournamentToUpdate.capacity());

      TournamentEntity updatedtournament = tournamentRepository.save(tournamentEntity.get());

      return this.getByIdentifier(updatedtournament.getIdentifier());
    } catch (DataAccessException e) {
      LOGGER.error("Could not update tournament {}", tournamentToUpdate, e);
      throw new TournamentDataRetrievalException(e);
    }
  }

  /**
   * Supprime un tournois à partir de son identifiant
   * @param identifier Identifiant du tournois
   */
  public void delete(UUID identifier) {
    LOGGER.info("Invoking delete() with identifier={}", identifier);
    try {
      Optional<TournamentEntity> tournamentEntity = tournamentRepository.findOneByIdentifier(identifier);
      if (tournamentEntity.isEmpty()) {
        LOGGER.warn("Could not find tournament to delete with identifier={}", identifier);
        throw new TournamentNotFoundException(identifier);
      }

      tournamentRepository.delete(tournamentEntity.get());

    } catch (DataAccessException e) {
      LOGGER.error("Could not delete tournament with identifier={}", identifier, e);
      throw new TournamentDataRetrievalException(e);
    }
  }
}
