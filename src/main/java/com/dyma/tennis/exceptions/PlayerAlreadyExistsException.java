package com.dyma.tennis.exceptions;

import java.time.LocalDate;

public class PlayerAlreadyExistsException extends RuntimeException {

  /**
   *
   */
  private static final long serialVersionUID = 9109467528812942984L;

  public PlayerAlreadyExistsException(String firstName, String lastName, LocalDate birthDate) {
    super("Player with firstName " + firstName
        + " lastName " + lastName
        + " and birthDate " + birthDate
        + " already exists.");
  }

}