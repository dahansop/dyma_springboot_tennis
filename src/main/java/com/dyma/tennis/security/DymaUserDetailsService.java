package com.dyma.tennis.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.dyma.tennis.data.RoleEntity;
import com.dyma.tennis.data.UserEntity;
import com.dyma.tennis.repository.UserRepository;

@Component
public class DymaUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepositiory;

  @Override
  public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
    return userRepositiory.findOneWithRolesByLoginIgnoreCase(login)
        .map(this::createSecurityUser)
        .orElseThrow(() -> new UsernameNotFoundException("User with login = " + login + " does not exist !"));
  }

  private User createSecurityUser(UserEntity userEntity) {
    List<SimpleGrantedAuthority> grantedRoles = userEntity.getRoles().stream()
        .map(RoleEntity::getName)
        .map(SimpleGrantedAuthority::new)
        .toList();
    return new User(userEntity.getLogin(), userEntity.getPassword(), grantedRoles);
  }
}
