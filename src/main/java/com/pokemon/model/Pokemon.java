package com.pokemon.model;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "pokemon")
public class Pokemon {
    @Id
    private int id;
    private String name;
    private int height;
    private int weight;

    @ElementCollection
    private List<Type> types;

    @ElementCollection
    private List<Ability> abilities;

    @Embedded
    private Sprites sprites;

    @ElementCollection
    private List<Stat> stats;

    @ElementCollection
    private List<Move> moves;

    @ElementCollection
    private List<Evolution> evolutionChain;

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }

    public Sprites getSprites() {
        return sprites;
    }

    public void setSprites(Sprites sprites) {
        this.sprites = sprites;
    }

    public List<Stat> getStats() {
        return stats;
    }

    public void setStats(List<Stat> stats) {
        this.stats = stats;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    @JsonProperty("evolution_chain")
    public List<Evolution> getEvolutionChain() {
        return evolutionChain;
    }

    public void setEvolutionChain(List<Evolution> evolutionChain) {
        this.evolutionChain = evolutionChain;
    }

    @Override
    public String toString() {
        return "Pokemon{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", types=" + types +
                '}';
    }
} 