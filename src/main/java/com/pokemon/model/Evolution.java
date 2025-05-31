package com.pokemon.model;

import javax.persistence.Embeddable;

@Embeddable
public class Evolution {
    private int id;
    private String name;
    private String sprite;

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
    public String getSprite() {
        return sprite;
    }
    public void setSprite(String sprite) {
        this.sprite = sprite;
    }
} 