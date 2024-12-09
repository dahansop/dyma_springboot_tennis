package com.dyma.tennis.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dyma.tennis.repository.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

  public Optional<UserEntity> findOneWithRolesByLoginIgnoreCase(String login);

}
