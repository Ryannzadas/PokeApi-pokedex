package com.pokemon;

import com.pokemon.model.Pokemon;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        PokemonAPI api = new PokemonAPI();

        try {
            // Exemplo 1: Buscar Pokémon por ID
            System.out.println("=== Buscando Pokémon por ID ===");
            Pokemon pikachu = api.getPokemon(25);
            System.out.println("Nome: " + pikachu.getName());
            System.out.println("Tipos: " + pikachu.getTypes());
            System.out.println("Altura: " + pikachu.getHeight());
            System.out.println("Peso: " + pikachu.getWeight());
            System.out.println("Habilidades: " + pikachu.getAbilities());
            System.out.println("Estatísticas: " + pikachu.getStats());
            System.out.println();

            // Exemplo 2: Buscar Pokémon por nome
            System.out.println("=== Buscando Pokémon por nome ===");
            Pokemon charizard = api.getPokemon("charizard");
            System.out.println("Nome: " + charizard.getName());
            System.out.println("Tipos: " + charizard.getTypes());
            System.out.println();

            // Exemplo 3: Listar Pokémon
            System.out.println("=== Listando primeiros 5 Pokémon ===");
            List<Pokemon> pokemons = api.getPokemonList(5, 0);
            for (Pokemon pokemon : pokemons) {
                System.out.println(pokemon.getName() + " - Tipos: " + pokemon.getTypes());
            }

        } catch (IOException e) {
            System.err.println("Erro ao acessar a API: " + e.getMessage());
        }
    }
} 