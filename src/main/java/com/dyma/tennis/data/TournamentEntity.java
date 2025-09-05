package com.dyma.tennis.data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "TOURNAMENT", schema = "PUBLIC")
public class TournamentEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  @Column(name = "identifier", nullable = false, unique = true)
  private UUID identifier;

  @Column(name = "name", length = 100, nullable = false, unique = true)
  private String name;

  @Column(name = "START_DATE", nullable = false)
  private LocalDate startDate;

  @Column(name = "END_DATE", nullable = false)
  private LocalDate endDate;

  @Column(name = "PRIZE_MONEY", nullable = true)
  private Integer prizeMoney;

  @Column(name = "CAPACITY", nullable = false)
  private Integer capacity;

  @ManyToMany(mappedBy = "tournaments", fetch = FetchType.EAGER)
  private Set<PlayerEntity> players = new HashSet<>();

  public TournamentEntity() {

  }

  public TournamentEntity(UUID identifier, String name, LocalDate startDate, LocalDate endDate,
      Integer prizeMoney, Integer capacity) {
    super();
    this.identifier = identifier;
    this.name = name;
    this.startDate = startDate;
    this.endDate = endDate;
    this.prizeMoney = prizeMoney;
    this.capacity = capacity;
  }

  public Long getId() {
    return id;
  }

  public UUID getIdentifier() {
    return identifier;
  }

  public void setIdentifier(UUID identifier) {
    this.identifier = identifier;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public Integer getPrizeMoney() {
    return prizeMoney;
  }

  public void setPrizeMoney(Integer prizeMoney) {
    this.prizeMoney = prizeMoney;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  public Set<PlayerEntity> getPlayers() {
    return players;
  }

  public boolean isFull() {
    return capacity == players.size();
  }

  public boolean hasPlayer(PlayerEntity playerToRegister) {
    return players.contains(playerToRegister);
  }
}
