package com.dyma.tennis.data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

  @Column(name = "identifier", nullable = false, unique = true)
  private UUID identifier;

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "player_tournament",
      joinColumns = {@JoinColumn(name = "player_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "tournament_id", referencedColumnName = "id")})
  private Set<TournamentEntity> tournaments = new HashSet<>();

  public PlayerEntity() {

  }

  public PlayerEntity(String lastName, String firstName, UUID identifier, LocalDate birthDate, Integer points, Integer rank) {
    this.lastName = lastName;
    this.firstName = firstName;
    this.identifier = identifier;
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

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

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

  public UUID getIdentifier() {
    return identifier;
  }

  public void setIdentifier(UUID identifier) {
    this.identifier = identifier;
  }

  public Set<TournamentEntity> getTournaments() {
    return tournaments;
  }

  public void addTournament(TournamentEntity tournamententity) {
    this.tournaments.add(tournamententity);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    PlayerEntity other = (PlayerEntity) obj;
    return Objects.equals(id, other.id);
  }
}
