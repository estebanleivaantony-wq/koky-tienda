package com.koky.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.koky.web.domain.Game;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Importar Optional para findByName

@Repository
public interface GameRepo extends JpaRepository<Game, Long> {

    List<Game> findByReleaseDateAfterOrderByReleaseDateDesc(LocalDate date);

    List<Game> findByDiscountPercentageGreaterThan(Integer discountPercentage);

    List<Game> findByNameContainingIgnoreCase(String name);

    Optional<Game> findByName(String name); // Añadido para la verificación en DataLoader
}