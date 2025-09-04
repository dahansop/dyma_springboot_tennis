package com.dyma.tennis.exceptions;

import java.util.UUID;

public class TournamentNotFoundException extends RuntimeException {
  
  private static final long serialVersionUID = 1276116904198378685L;

  public TournamentNotFoundException(UUID identifier) {
    super("Tournament with identifier " + identifier + " not found !");
  }
}
