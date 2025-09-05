package com.dyma.tennis.exceptions;

public class TournamentRegistrationException extends RuntimeException {

  private static final long serialVersionUID = 7577981537386495786L;

  public TournamentRegistrationException(String reason) {
    super(reason);
  }
}
