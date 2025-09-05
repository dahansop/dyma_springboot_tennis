package com.dyma.tennis.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.dyma.tennis.data.PlayerEntity;
import com.dyma.tennis.exceptions.PlayerAlreadyExistsException;
import com.dyma.tennis.exceptions.PlayerDataRetrievalException;
import com.dyma.tennis.exceptions.PlayerNotFoundException;
import com.dyma.tennis.mapper.PlayerMapper;
import com.dyma.tennis.model.Player;
import com.dyma.tennis.model.PlayerToCreate;
import com.dyma.tennis.model.PlayerToUpdate;
import com.dyma.tennis.repository.PlayerRepository;

@Service
public class PlayerService {

  private static final Integer TEMPORARY_RANK = 99999999;

  private final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);

  @Autowired
  private final PlayerRepository playerRepository;

  @Autowired
  private final PlayerMapper playerMapper;

  /**
   * constructeur
   * @param playerRepository
   */
  public PlayerService(PlayerRepository playerRepository, PlayerMapper playerMapper) {
    this.playerRepository = playerRepository;
    this.playerMapper = playerMapper;
  }

  /**
   *
   * @return la liste de tout les joueurs trié par position
   */
  public List<Player> getAllPlayers() {
    LOGGER.info("Invoking getAllPlayers()");
    try {
      return playerRepository.findAll().stream()
          .map(playerMapper::playerEntityToPlayer)
          .sorted(Comparator.comparing(player -> player.info().rank().position()))
          .collect(Collectors.toList());
    } catch (DataAccessException e) {
      LOGGER.error("Could not retrieve players", e);
      throw new PlayerDataRetrievalException(e);
    }
  }

  /**
   * Recherche un joueur à partir de son identifiant
   * @param identifier identifiant unique du joueur
   * @return joueur
   */
  public Player getByIdentifier(UUID identifier) {
    LOGGER.info("Invoking getByIdentifier() with identifier={}", identifier);
    try {
      Optional<PlayerEntity> playerEntity = playerRepository.findOneByIdentifier(identifier);

      if (playerEntity.isEmpty()) {
        LOGGER.warn("Could not find player with identifier={}", identifier);
        throw new PlayerNotFoundException(identifier);
      }

      return playerMapper.playerEntityToPlayer(playerEntity.get());
    } catch (DataAccessException e) {
      LOGGER.error("Could not find player with identifier={}", identifier, e);
      throw new PlayerDataRetrievalException(e);
    }
  }

  /**
   * Permet d'ajouter un joueur à la liste
   * @param playerToCreate
   * @return
   */
  public Player create(PlayerToCreate playerToCreate) {
    LOGGER.info("Invoking create() with playerToSave={}", playerToCreate);
    try {
      Optional<PlayerEntity> playerAlreadyExists = playerRepository.findOneByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate(playerToCreate.firstName(), playerToCreate.lastName(), playerToCreate.birthDate());
      if (playerAlreadyExists.isPresent()) {
        LOGGER.warn("Player to create with firstName={} lastName={} and birthDate={} already exists", playerToCreate.firstName(), playerToCreate.lastName(), playerToCreate.birthDate());
        throw new PlayerAlreadyExistsException(playerToCreate.firstName(), playerToCreate.lastName(), playerToCreate.birthDate());
      }

      PlayerEntity playerEntity = new PlayerEntity(
        playerToCreate.lastName(),
        playerToCreate.firstName(),
        UUID.randomUUID(),
        playerToCreate.birthDate(),
        playerToCreate.points(),
        TEMPORARY_RANK);
      PlayerEntity registeredPlayer = playerRepository.save(playerEntity);

      updateRankingPlayers();

      return this.getByIdentifier(registeredPlayer.getIdentifier());
    } catch (DataAccessException e) {
      LOGGER.error("Could not create player {}", playerToCreate, e);
      throw new PlayerDataRetrievalException(e);
    }
  }

  /**
   * Permet de mettre à jour un joueur
   * @param playerToUpdate
   * @return
   */
  public Player update(PlayerToUpdate playerToUpdate) {
    LOGGER.info("Invoking update() with playerToUpdate={}", playerToUpdate);
    try {
      Optional<PlayerEntity> playerEntity = playerRepository.findOneByIdentifier(playerToUpdate.identifier());
      if (playerEntity.isEmpty()) {
        LOGGER.warn("Could not find player to update with identifier={}", playerToUpdate.identifier());
        throw new PlayerNotFoundException(playerToUpdate.identifier());
      }

      Optional<PlayerEntity> potentiallyDuplicatedPlayer = playerRepository.findOneByFirstNameIgnoreCaseAndLastNameIgnoreCaseAndBirthDate(playerToUpdate.firstName(), playerToUpdate.lastName(), playerToUpdate.birthDate());
      if (potentiallyDuplicatedPlayer.isPresent() && !potentiallyDuplicatedPlayer.get().getIdentifier().equals(playerToUpdate.identifier())) {
        LOGGER.warn("Player to update with firstName={} lastName={} and birthDate={} already exists",
            playerToUpdate.firstName(), playerToUpdate.lastName(), playerToUpdate.birthDate());
        throw new PlayerAlreadyExistsException(playerToUpdate.firstName(), playerToUpdate.lastName(), playerToUpdate.birthDate());

      }

      playerEntity.get().setFirstName(playerToUpdate.firstName());
      playerEntity.get().setLastName(playerToUpdate.lastName());
      playerEntity.get().setBirthDate(playerToUpdate.birthDate());
      playerEntity.get().setPoints(playerToUpdate.points());
      PlayerEntity updatedPlayer = playerRepository.save(playerEntity.get());

      updateRankingPlayers();

      return this.getByIdentifier(updatedPlayer.getIdentifier());
    } catch (DataAccessException e) {
      LOGGER.error("Could not update player {}", playerToUpdate, e);
      throw new PlayerDataRetrievalException(e);
    }
  }

  /**
   * Permet de supprimer un joueur
   * @param identifier
   */
  public void delete(UUID identifier) {
    LOGGER.info("Invoking delete() with identifier={}", identifier);
    try {
      Optional<PlayerEntity> player = playerRepository.findOneByIdentifier(identifier);
      if (player.isEmpty()) {
        LOGGER.warn("Could not find player to delete with identifier={}", identifier);
        throw new PlayerNotFoundException(identifier);
      }

      playerRepository.delete(player.get());

      updateRankingPlayers();
    } catch (DataAccessException e) {
      LOGGER.error("Could not delete player with identifier={}", identifier, e);
      throw new PlayerDataRetrievalException(e);
    }
  }

  /**
   * Calcul le classement du joueur
   * @param playersList list des joueur (sans celui à mettre à jour)
   * @param playertoSave joueur à mettre à jour
   */
  private void updateRankingPlayers() {
    RankingCalculator rankingCalculator = new RankingCalculator(playerRepository.findAll());
    List<PlayerEntity> newRankingList = rankingCalculator.getNewPlayersRanking();
    playerRepository.saveAll(newRankingList);
  }
}
