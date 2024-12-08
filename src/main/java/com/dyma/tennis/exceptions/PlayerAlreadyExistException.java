package com.dyma.tennis.exceptions;

public class PlayerAlreadyExistException extends RuntimeException {
  
  /**
   * 
   */
  private static final long serialVersionUID = 9109467528812942984L;

  public PlayerAlreadyExistException(String lastName) {
    super("LastName " + lastName + " laready exists !");
  }

}