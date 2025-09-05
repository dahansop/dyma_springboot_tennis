package com.dyma.tennis.exceptions;

import java.util.UUID;

public class PlayerNotFoundException extends RuntimeException {

  private static final long serialVersionUID = 8432330210468713766L;


  public PlayerNotFoundException(UUID identifier) {
    super("Player with identifier " + identifier + " not found !");
  }
}
