package com.dyma.tennis.exceptions;

public class TournamentAlreadyExistsException extends RuntimeException {

  private static final long serialVersionUID = -8292899502816809608L;

  public TournamentAlreadyExistsException(String name) {
    super("Tournament with name " + name + " already exists.");
  }

}