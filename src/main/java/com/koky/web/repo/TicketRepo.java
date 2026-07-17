package com.koky.web.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.koky.web.domain.Ticket;

import java.util.List;

@Repository
public interface TicketRepo extends JpaRepository<Ticket, Long> {
    // Busca por email y ordena por fecha (el más reciente primero)
    List<Ticket> findByEmailOrderByCreatedAtDesc(String email);
}