package com.dyma.tennis.exceptions;

public class PlayerDataRetrievalException extends RuntimeException {

  private static final long serialVersionUID = 3842082758626078320L;

  /**
   * Constructeur
   * @param e
   */
  public PlayerDataRetrievalException(Exception e) {
    super("Could not retrieve player data", e);
  }
}
