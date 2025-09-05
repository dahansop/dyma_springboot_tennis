package com.dyma.tennis.service;

import java.util.ArrayList;
import java.util.List;

import com.dyma.tennis.data.PlayerEntity;

public class RankingCalculator {

  private final List<PlayerEntity> currentPlayerList;

  /**
   * Constructeur
   *
   * @param currentPlayerList
   */
  public RankingCalculator(List<PlayerEntity> currentPlayerList) {
    this.currentPlayerList = currentPlayerList;
  }

  /**
   * Calcule la nouveau classement de joueur
   * @return le nouveau classement des joueurs (non enregistré en BDD)
   */
  public List<PlayerEntity> getNewPlayersRanking() {
    currentPlayerList.sort((player1, player2) -> Integer.compare(player2.getPoints(), player1.getPoints()));
    List<PlayerEntity> updatedPlayersList = new ArrayList<>();

    for (int index = 0; index < currentPlayerList.size(); index++) {
      PlayerEntity current = currentPlayerList.get(index);
      current.setRank(index + 1);
      updatedPlayersList.add(current);
    }

    return currentPlayerList;
  }
}
