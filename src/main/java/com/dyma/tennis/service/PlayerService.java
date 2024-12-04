package com.dyma.tennis.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dyma.tennis.dto.Player;
import com.dyma.tennis.dto.PlayerToSave;
import com.dyma.tennis.exceptions.PlayerNotFoundException;
import com.dyma.tennis.jeutests.PlayerList;
import com.dyma.tennis.jeutests.RankingCalculatorList;

@Service
public class PlayerService {

  /**
   * 
   * @return la liste de tout les joueurs trié par position
   */
  public List<Player> getAll() {
    return PlayerList.ALL.stream()
        .sorted(Comparator.comparing(player -> player.rank().position()))
        .collect(Collectors.toList());
  }
  
  /**
   * Recherche un joueur à partir de son nom
   * @param lastName Nom du joueur
   * @return joueur
   */
  public Player getByLastName(String lastName) {
    return PlayerList.ALL.stream()
        .filter(player -> player.lastName().equals(lastName))
        .findFirst()
        .orElseThrow(() -> new PlayerNotFoundException(lastName));
  }
  
  /**
   * Permet d'ajouter un joueur à la liste
   * @param playerToSave
   * @return
   */
  public Player create(PlayerToSave playerToSave) {
    return getNewRankingplayer(PlayerList.ALL, playerToSave);
  }
  
  /**
   * Permet de mettre à jour un joueur
   * @param playerToSave
   * @return
   */
  public Player update(PlayerToSave playerToSave) {
    getByLastName(playerToSave.lastName()); // remonte une exception si le joueur existe déjà
    
    // on supprime le joueur du classement (les record ne sont pas modifiables)
    List<Player> playersWithoutPlayerToUpdate = PlayerList.ALL.stream()
        .filter(player -> !player.lastName().equals(playerToSave.lastName()))
        .toList();
    
    // mise à jour du classement général
    return getNewRankingplayer(playersWithoutPlayerToUpdate, playerToSave);
  }
  
  /**
   * Permet de supprimer un joueur
   * @param lastName
   */
  public void delete(String lastName) {
    Player playerToDelete = getByLastName(lastName); // remonte une exception si le joueur existe déjà
    
    // on supprime le joueur du classement (les record ne sont pas modifiables)
    List<Player> playersWithoutPlayerToDelete = PlayerList.ALL.stream()
        .filter(player -> !player.lastName().equals(playerToDelete.lastName()))
        .toList();
    
    // mise à jour du classement général
    RankingCalculatorList rankingCalculator = new RankingCalculatorList(playersWithoutPlayerToDelete, null);
    rankingCalculator.calculateNewPlayersRanking();
  }
  
  /**
   * Calcul le classement du joueur
   * @param playersList list des joueur (sans celui à mettre à jour)
   * @param playertoSave joueur à mettre à jour
   */
  private Player getNewRankingplayer(List<Player> playersList, PlayerToSave playerToSave) {
    RankingCalculatorList rankingCalculator = new RankingCalculatorList(playersList, playerToSave);
    rankingCalculator.calculateNewPlayersRanking();
    
    return PlayerList.ALL.stream()
        .filter(player -> player.lastName().equals(playerToSave.lastName()))
        .findFirst()
        .get();
  }
}
