package com.pokemon.service;

import com.google.gson.Gson;
import com.pokemon.model.Pokemon;
import com.pokemon.model.Type;
import com.pokemon.model.Evolution;
import com.pokemon.repository.PokemonRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import java.util.LinkedList;

@Service
public class PokemonService {
    private static final String BASE_URL = "https://pokeapi.co/api/v2";
    private final OkHttpClient client;
    private final Gson gson;

    @Autowired
    private PokemonRepository pokemonRepository;

    public PokemonService() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    // HTTP API Methods
    public Pokemon getPokemonFromAPI(int id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/pokemon/" + id)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro ao buscar Pokémon: " + response);
            }
            return gson.fromJson(response.body().string(), Pokemon.class);
        }
    }

    public Pokemon getPokemonFromAPI(String name) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/pokemon/" + name.toLowerCase())
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro ao buscar Pokémon: " + response);
            }
            return gson.fromJson(response.body().string(), Pokemon.class);
        }
    }

    public List<Pokemon> getPokemonListFromAPI(int limit, int offset) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + "/pokemon?limit=" + limit + "&offset=" + offset)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erro ao buscar lista de Pokémon: " + response);
            }

            String responseBody = response.body().string();
            PokemonListResponse listResponse = gson.fromJson(responseBody, PokemonListResponse.class);
            
            List<Pokemon> pokemons = new ArrayList<>();
            for (PokemonListItem item : listResponse.getResults()) {
                String pokemonUrl = item.getUrl();
                String pokemonId = pokemonUrl.substring(pokemonUrl.length() - 3).replace("/", "");
                pokemons.add(getPokemonFromAPI(Integer.parseInt(pokemonId)));
            }
            return pokemons;
        }
    }

    // Database Methods
    public Page<Pokemon> getAllPokemon(Pageable pageable) {
        return pokemonRepository.findAll(pageable);
    }

    public Optional<Pokemon> getPokemonById(int id) {
        return pokemonRepository.findById(id);
    }

    public Page<Pokemon> searchPokemon(String search, Pageable pageable) {
        return pokemonRepository.findByNameContainingIgnoreCase(search, pageable);
    }

    public Page<Pokemon> getPokemonByType(String type, Pageable pageable) {
        return pokemonRepository.findByTypesContaining(type, pageable);
    }

    public Set<String> getAllTypes() {
        return pokemonRepository.findAll().stream()
                .flatMap(pokemon -> pokemon.getTypes().stream())
                .map(Type::toString)
                .collect(Collectors.toSet());
    }

    @PostConstruct
    public void populateDatabaseIfEmpty() {
        if (pokemonRepository.count() == 0) {
            System.out.println("Populando banco de dados com os primeiros 151 Pokémon...");
            for (int i = 1; i <= 151; i++) {
                try {
                    Pokemon pokemon = getPokemonFromAPI(i);
                    // Buscar e preencher evoluções
                    List<Evolution> evolutions = fetchEvolutions(i);
                    pokemon.setEvolutionChain(evolutions);
                    if (pokemon.getMoves() == null) {
                        System.out.println("Pokémon " + pokemon.getName() + " não possui moves, pulando...");
                        continue;
                    }
                    pokemonRepository.save(pokemon);
                    System.out.println("Salvo: " + pokemon.getName());
                    Thread.sleep(200); // Pequeno delay para evitar sobrecarga
                } catch (Exception e) {
                    System.err.println("Erro ao importar Pokémon ID " + i + ": " + e.getMessage());
                }
            }
            System.out.println("População inicial concluída!");
        }
    }

    // Busca a cadeia de evolução para um Pokémon
    private List<Evolution> fetchEvolutions(int pokemonId) {
        List<Evolution> evolutions = new LinkedList<>();
        try {
            // 1. Buscar species
            Request speciesRequest = new Request.Builder()
                    .url(BASE_URL + "/pokemon-species/" + pokemonId)
                    .build();
            try (Response speciesResponse = client.newCall(speciesRequest).execute()) {
                if (!speciesResponse.isSuccessful()) return evolutions;
                String speciesBody = speciesResponse.body().string();
                var speciesJson = gson.fromJson(speciesBody, SpeciesResponse.class);
                if (speciesJson.evolution_chain == null || speciesJson.evolution_chain.url == null) return evolutions;
                // 2. Buscar evolution-chain
                String evoUrl = speciesJson.evolution_chain.url;
                Request evoRequest = new Request.Builder().url(evoUrl).build();
                try (Response evoResponse = client.newCall(evoRequest).execute()) {
                    if (!evoResponse.isSuccessful()) return evolutions;
                    String evoBody = evoResponse.body().string();
                    var evoJson = gson.fromJson(evoBody, EvolutionChainResponse.class);
                    // 3. Montar lista de evoluções (linear)
                    EvolutionChainLink link = evoJson.chain;
                    while (link != null) {
                        Evolution evo = new Evolution();
                        evo.setName(link.species.name);
                        evo.setId(extractIdFromUrl(link.species.url));
                        evo.setSprite(fetchSprite(evo.getId()));
                        evolutions.add(evo);
                        if (link.evolves_to != null && !link.evolves_to.isEmpty()) {
                            link = link.evolves_to.get(0);
                        } else {
                            link = null;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao buscar evoluções: " + e.getMessage());
        }
        return evolutions;
    }

    // Busca o sprite do Pokémon pelo id
    private String fetchSprite(int id) {
        try {
            Request req = new Request.Builder().url(BASE_URL + "/pokemon/" + id).build();
            try (Response resp = client.newCall(req).execute()) {
                if (!resp.isSuccessful()) return null;
                String body = resp.body().string();
                var poke = gson.fromJson(body, Pokemon.class);
                return poke.getSprites() != null ? poke.getSprites().getFront_default() : null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    // Extrai o id da URL da espécie
    private int extractIdFromUrl(String url) {
        String[] parts = url.split("/");
        for (int i = parts.length - 1; i >= 0; i--) {
            if (!parts[i].isEmpty()) {
                try {
                    return Integer.parseInt(parts[i]);
                } catch (NumberFormatException ignored) {}
            }
        }
        return -1;
    }

    // Classes auxiliares para JSON
    private static class SpeciesResponse {
        EvolutionChainLinkRef evolution_chain;
    }
    private static class EvolutionChainLinkRef {
        String url;
    }
    private static class EvolutionChainResponse {
        EvolutionChainLink chain;
    }
    private static class EvolutionChainLink {
        SpeciesRef species;
        List<EvolutionChainLink> evolves_to;
    }
    private static class SpeciesRef {
        String name;
        String url;
    }

    private static class PokemonListResponse {
        private int count;
        private String next;
        private String previous;
        private List<PokemonListItem> results;

        public List<PokemonListItem> getResults() {
            return results;
        }
    }

    private static class PokemonListItem {
        private String name;
        private String url;

        public String getUrl() {
            return url;
        }
    }
} 