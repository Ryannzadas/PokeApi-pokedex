package com.pokemon.controller;

import com.pokemon.model.Pokemon;
import com.pokemon.service.PokemonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

@RestController
@RequestMapping("/api/pokemon")
@Tag(name = "Pokemon", description = "Endpoints para consulta de Pokémon")
public class PokemonController {

    @Autowired
    private PokemonService pokemonService;

    @GetMapping("/{id}")
    @Operation(summary = "Busca um Pokémon pelo ID")
    public ResponseEntity<Pokemon> getPokemonById(@PathVariable int id) {
        // Primeiro tenta buscar no banco local
        return pokemonService.getPokemonById(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> {
                try {
                    Pokemon pokemon = pokemonService.getPokemonFromAPI(id);
                    return ResponseEntity.ok(pokemon);
                } catch (IOException e) {
                    return ResponseEntity.notFound().build();
                }
            });
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Busca um Pokémon pelo nome")
    public ResponseEntity<Pokemon> getPokemonByName(@PathVariable String name) {
        try {
            Pokemon pokemon = pokemonService.getPokemonFromAPI(name);
            return ResponseEntity.ok(pokemon);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @Operation(summary = "Lista Pokémon com paginação")
    public ResponseEntity<Page<Pokemon>> getPokemonList(Pageable pageable) {
        return ResponseEntity.ok(pokemonService.getAllPokemon(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Busca Pokémon por nome")
    public ResponseEntity<Page<Pokemon>> searchPokemon(
            @RequestParam String query,
            Pageable pageable) {
        return ResponseEntity.ok(pokemonService.searchPokemon(query, pageable));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Busca Pokémon por tipo")
    public ResponseEntity<Page<Pokemon>> getPokemonByType(
            @PathVariable String type,
            Pageable pageable) {
        return ResponseEntity.ok(pokemonService.getPokemonByType(type, pageable));
    }

    @GetMapping("/types")
    @Operation(summary = "Lista todos os tipos de Pokémon")
    public ResponseEntity<Set<String>> getAllTypes() {
        return ResponseEntity.ok(pokemonService.getAllTypes());
    }
} 