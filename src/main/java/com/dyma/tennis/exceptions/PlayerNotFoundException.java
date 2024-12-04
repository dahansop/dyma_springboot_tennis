package com.dyma.tennis.exceptions;

public class PlayerNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 8432330210468713766L;

  
  public PlayerNotFoundException(String lastName) {
    super("LastName " + lastName + " not found !");
  }
}
