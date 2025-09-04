package com.dyma.tennis.exceptions;

public class TournamentDataRetrievalException extends RuntimeException {

  private static final long serialVersionUID = 5729613272098265338L;

  /**
   * Constructeur
   * @param e
   */
  public TournamentDataRetrievalException(Exception e) {
    super("Could not retrieve tournament data", e);
  }
}
