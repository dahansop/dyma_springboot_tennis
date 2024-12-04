package com.dyma.tennis.jeutests;

import java.util.ArrayList;
import java.util.List;

import com.dyma.tennis.dto.Player;
import com.dyma.tennis.dto.PlayerToSave;
import com.dyma.tennis.dto.Rank;

/**
 * Utilisé dans la couche service pour simuler en mémoire les actions sur les joueurs avant la création de la couche persistance
 */
public class RankingCalculatorList {

  private final List<Player> currentPlayerList;
  private final PlayerToSave playerToSave;
  
  /**
   * Constructeur
   * 
   * @param currentPlayerList
   * @param playerToSave
   */
  public RankingCalculatorList(List<Player> currentPlayerList, PlayerToSave playerToSave) {
    this.currentPlayerList = currentPlayerList;
    this.playerToSave = playerToSave;
  }
  
  /**
   * Constructeur
   * 
   * @param currentPlayerList
   */
  public RankingCalculatorList(List<Player> currentPlayerList) {
    this.currentPlayerList = currentPlayerList;
    this.playerToSave = null;
  }
  
  /**
   * Calcul la position des joueurs de la liste
   * et met à jour la list globale
   */
  public void calculateNewPlayersRanking() {
    List<Player> newRankingList = new ArrayList<Player>(currentPlayerList);
    
    // on ajoute le joueur si besoin
    if (playerToSave != null) {
      newRankingList.add(new Player(
          playerToSave.firstName(),
          playerToSave.lastName(),
          playerToSave.birthDate(),
          new Rank(999999999, playerToSave.points())));
    }
    
    // trie par ordre décroissant de point la liste des joueurs
    newRankingList.sort((player1, player2) -> Integer.compare(player2.rank().points(), player1.rank().points()));
    
    // mise à jour du classement
    List<Player> updatedPlayersList = new ArrayList<Player>();
    for (int index = 0; index < newRankingList.size(); index++) {
      Player current = newRankingList.get(index);
      updatedPlayersList.add(new Player(
          current.firstName(),
          current.lastName(),
          current.birthDate(),
          new Rank(index + 1, current.rank().points())));
    }
    
    // mise à jour de notre liste globale
    PlayerList.ALL = updatedPlayersList;
  }
}
