package com.pokemon.repository;

import com.pokemon.model.Pokemon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Integer> {
    Page<Pokemon> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Pokemon> findByTypesContaining(String type, Pageable pageable);
} 