package com.pokemon;

import com.google.gson.Gson;
import com.pokemon.model.Pokemon;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PokemonAPI {
    private static final String BASE_URL = "https://pokeapi.co/api/v2";
    private final OkHttpClient client;
    private final Gson gson;

    public PokemonAPI() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    public Pokemon getPokemon(int id) throws IOException {
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

    public Pokemon getPokemon(String name) throws IOException {
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

    public List<Pokemon> getPokemonList(int limit, int offset) throws IOException {
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
                pokemons.add(getPokemon(Integer.parseInt(pokemonId)));
            }
            return pokemons;
        }
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