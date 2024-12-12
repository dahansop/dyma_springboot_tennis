package com.dyma.tennis.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.dyma.tennis.dto.Player;
import com.dyma.tennis.dto.PlayerToSave;
import com.dyma.tennis.dto.Rank;
import com.dyma.tennis.exceptions.PlayerAlreadyExistException;
import com.dyma.tennis.exceptions.PlayerDataRetrievalException;
import com.dyma.tennis.exceptions.PlayerNotFoundException;
import com.dyma.tennis.repository.PlayerRepository;
import com.dyma.tennis.repository.entity.PlayerEntity;

@Service
public class PlayerService {

  private static final Integer TEMPORARY_RANK = 99999999;
  
  private final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);
  
  @Autowired
  private PlayerRepository playerRepository;
  
  /**
   * constructeur
   * @param playerRepository
   */
  public PlayerService(PlayerRepository playerRepository) {
    this.playerRepository = playerRepository;
  }

  /**
   * 
   * @return la liste de tout les joueurs trié par position
   */
  public List<Player> getAllPlayers() {
    LOGGER.info("Invoking getAllPlayers()");
    try {
      return playerRepository.findAll().stream()
          .map(playerEntity -> convertEntityToDto(playerEntity))
          .sorted(Comparator.comparing(player -> player.rank().position()))
          .collect(Collectors.toList());
    } catch (DataAccessException e) {
      LOGGER.error("Could not retrieve players", e);
      throw new PlayerDataRetrievalException(e);
    }
  }
  
  /**
   * Recherche un joueur à partir de son nom
   * @param lastName Nom du joueur
   * @return joueur
   */
  public Player getByLastName(String lastName) {
    LOGGER.info("Invoking getByLastName() with lastName={}", lastName);
    try {
      Optional<PlayerEntity> playerEntity = playerRepository.findOneByLastNameIgnoreCase(lastName);
    
      if (playerEntity.isEmpty()) {
        LOGGER.warn("Could not find player with lastName={}", lastName);
        throw new PlayerNotFoundException(lastName);
      }
    
      return convertEntityToDto(playerEntity.get());
    } catch (DataAccessException e) {
      LOGGER.error("Could not find player with lastName={}", lastName, e);
      throw new PlayerDataRetrievalException(e);
    }
  }

  /**
   * Permet d'ajouter un joueur à la liste
   * @param playerToSave
   * @return
   */
  public Player create(PlayerToSave playerToSave) {
    LOGGER.info("Invoking create() with playerToSave={}", playerToSave);
    try {
      Optional<PlayerEntity> playerAlreadyExists = playerRepository.findOneByLastNameIgnoreCase(playerToSave.lastName());
      if (playerAlreadyExists.isPresent()) {
        LOGGER.warn("Player to create with lastName={} already exists", playerToSave.lastName());
        throw new PlayerAlreadyExistException(playerToSave.lastName());
      }
    
      PlayerEntity playerEntity = new PlayerEntity(
        playerToSave.firstName(),
        playerToSave.lastName(),
        playerToSave.birthDate(),
        playerToSave.points(),
        TEMPORARY_RANK);
      playerRepository.save(playerEntity);
    
      updateRankingPlayers();
    
      return getByLastName(playerToSave.lastName());
    } catch (DataAccessException e) {
      LOGGER.error("Could not create player {}", playerToSave, e);
      throw new PlayerDataRetrievalException(e);
    }
  }
  
  /**
   * Permet de mettre à jour un joueur
   * @param playerToSave
   * @return
   */
  public Player update(PlayerToSave playerToSave) {
    LOGGER.info("Invoking update() with playerToSave={}", playerToSave);
    try {
      Optional<PlayerEntity> player = playerRepository.findOneByLastNameIgnoreCase(playerToSave.lastName());
      if (player.isEmpty()) {
        LOGGER.warn("Could not find player to update with lastName={}", playerToSave.lastName());
        throw new PlayerNotFoundException(playerToSave.lastName());
      }
    
      PlayerEntity playerEntity = player.get();
      playerEntity.setBirthDate(playerToSave.birthDate());
      playerEntity.setFirstName(playerToSave.firstName());
      playerEntity.setPoints(playerToSave.points());
      playerEntity = playerRepository.save(playerEntity);
    
      updateRankingPlayers();
    
      return getByLastName(playerToSave.lastName());
    } catch (DataAccessException e) {
      LOGGER.error("Could not update player {}", playerToSave, e);
      throw new PlayerDataRetrievalException(e);
    }
  }
  
  /**
   * Permet de supprimer un joueur
   * @param lastName
   */
  public void delete(String lastName) {
    LOGGER.info("Invoking delete() with lastName={}", lastName);
    try {
      Optional<PlayerEntity> player = playerRepository.findOneByLastNameIgnoreCase(lastName);
      if (player.isEmpty()) {
        LOGGER.warn("Could not find player to delete with lastName={}", lastName);
        throw new PlayerNotFoundException(lastName);
      }

      playerRepository.delete(player.get());
    
      updateRankingPlayers();
    } catch (DataAccessException e) {
      LOGGER.error("Could not delete player with lastName={}", lastName, e);
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
  
  /**
   * Converti un PlayerEntity en Player
   * 
   * @param playerEntity
   * @return
   */
  private Player convertEntityToDto(PlayerEntity playerEntity) {
    return new Player(playerEntity.getFirstName(), 
        playerEntity.getLastName(), 
        playerEntity.getBirthDate(), 
        new Rank(playerEntity.getRank(), playerEntity.getPoints()));
  }
}
