package com.dyma.tennis.repository.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "PLAYER", schema = "PUBLIC")
public class PlayerEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;
  
  @Column(name = "LAST_NAME", length = 50, nullable = false)
  private String lastName;
  
  @Column(name = "FIRST_NAME", length = 50, nullable = false)
  private String firstName;
  
  @Column(name = "BIRTH_DATE", nullable = false)
  private LocalDate birthDate;
  
  @Column(name = "POINTS", nullable = false)
  private Integer points;
  
  @Column(name = "RANK", nullable = false)
  private Integer rank;
  
  public PlayerEntity() {
    
  }
  
  public PlayerEntity(String firstName, String lastName, LocalDate birthDate, Integer points, Integer rank) {
    this.lastName = lastName;
    this.firstName = firstName;
    this.birthDate = birthDate;
    this.points = points;
    this.rank = rank;
  }
  
  public Long getId() {
    return id;
  }
  
  public String getLastName() {
    return lastName;
  }
  
  // c'est l'identifieur pour trouv les joueur on peut donc pas le modifier
//  public void setLastName(String lastName) {
//    this.lastName = lastName;
//  }
  
  public String getFirstName() {
    return firstName;
  }
  
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  public LocalDate getBirthDate() {
    return birthDate;
  }
  
  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }
  
  public Integer getPoints() {
    return points;
  }
  
  public void setPoints(Integer points) {
    this.points = points;
  }
  
  public Integer getRank() {
    return rank;
  }
  
  public void setRank(Integer rank) {
    this.rank = rank;
  }
}
