package com.turgul.soccer.manager.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.turgul.soccer.manager.domain.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", indexes = @Index(name = "email_idx", columnList = "email"))
public class Users implements UserDetails, Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  @Column(name = "first_name")
  private String firstName;

  @NotNull
  @Column(name = "last_name")
  private String lastName;

  @JsonIgnore
  @NotNull
  @Column(unique = true)
  private String email;

  @JsonIgnore @NotNull private String password;

  @JsonIgnore
  @NotNull
  @Enumerated(EnumType.STRING)
  private Role role;

  @EqualsAndHashCode.Exclude
  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  private Team team;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(role);
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }
}
