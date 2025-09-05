package com.dyma.tennis.mapper;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dyma.tennis.data.PlayerEntity;
import com.dyma.tennis.model.Player;
import com.dyma.tennis.model.PlayerDescription;
import com.dyma.tennis.model.Rank;
import com.dyma.tennis.model.TournamentDescription;

@Component
public class PlayerMapper {

  /**
   * Converti Plauer Entity en Player
   * @param playerEntity entité
   * @return Player
   */
  public Player playerEntityToPlayer(PlayerEntity playerEntity) {
    PlayerDescription description =  new PlayerDescription(
        playerEntity.getIdentifier(),
        playerEntity.getFirstName(),
        playerEntity.getLastName(),
        playerEntity.getBirthDate(),
        new Rank(playerEntity.getRank(), playerEntity.getPoints()));

    Set<TournamentDescription> tournaments = playerEntity.getTournaments().stream()
        .map(tournamentEntity ->
            new TournamentDescription(
                tournamentEntity.getIdentifier(),
                tournamentEntity.getName(),
                tournamentEntity.getStartDate(),
                tournamentEntity.getEndDate(),
                tournamentEntity.getPrizeMoney(),
                tournamentEntity.getCapacity()
            )
        ).collect(Collectors.toSet());

    return new Player(description, tournaments);
  }
}
