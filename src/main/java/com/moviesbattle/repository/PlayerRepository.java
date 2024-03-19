package com.moviesbattle.repository;

import java.util.Optional;


import com.moviesbattle.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Integer> {

    boolean existsByUsername(final String username);

    boolean existsByUsernameAndPassword(final String username, final String password);

    Optional<Player> findByUsername(final String username);

}
