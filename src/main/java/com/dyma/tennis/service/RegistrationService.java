package com.dyma.tennis.service;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dyma.tennis.data.PlayerEntity;
import com.dyma.tennis.data.TournamentEntity;
import com.dyma.tennis.exceptions.TournamentRegistrationException;
import com.dyma.tennis.repository.PlayerRepository;
import com.dyma.tennis.repository.TournamentRepository;

@Service
public class RegistrationService {

  private final Logger LOGGER = LoggerFactory.getLogger(RegistrationService.class);

  @Autowired
  private final TournamentRepository tournamentRepository;

  @Autowired
  private final PlayerRepository playerRepository;

  /**
   * Constructeur
   * @param tournamentRepository
   * @param playerRepository
   */
  public RegistrationService(TournamentRepository tournamentRepository, PlayerRepository playerRepository) {
    this.tournamentRepository = tournamentRepository;
    this.playerRepository = playerRepository;
  }

  /**
   * Inscrit un joueur a un tournoi
   * @param tournamentIdentifier Identifiant du tournoi
   * @param playerToRegister Identifiant du joueur
   */
  public void register(UUID tournamentIdentifier, UUID playerToRegister) {
    LOGGER.info("Invoking register()");
    Optional<TournamentEntity> existingTournament = tournamentRepository.findOneByIdentifier(tournamentIdentifier);
    if (existingTournament.isEmpty()) {
      LOGGER.warn("Could not find tournament {} to register player", tournamentIdentifier);
      throw new TournamentRegistrationException("Tournament with identifier " + tournamentIdentifier + " does not exist");
    }

    if (existingTournament.get().isFull()) {
      LOGGER.warn("Tournament {} is full", tournamentIdentifier);
      throw new TournamentRegistrationException("Tournament with identifier " + tournamentIdentifier + " is full");
    }

    Optional<PlayerEntity> existingPlayer = playerRepository.findOneByIdentifier(playerToRegister);
    if (existingPlayer.isEmpty()) {
      LOGGER.warn("Could not find player {} to register", playerToRegister);
      throw new TournamentRegistrationException("Player with identifier " + playerToRegister + " does not exist");
    }

    if (existingTournament.get().hasPlayer(existingPlayer.get())) {
      LOGGER.warn("Player {} is already registred to tournament {}", playerToRegister, tournamentIdentifier);
      throw new TournamentRegistrationException("Player with identifier " + playerToRegister + " is already registred to tournament identifier " + tournamentIdentifier);
    }

    existingPlayer.get().addTournament(existingTournament.get());
    playerRepository.save(existingPlayer.get());
  }
}
