package com.pokemon.model;

import javax.persistence.Embeddable;

@Embeddable
public class Move {
    private MoveInfo move;

    public MoveInfo getMove() {
        return move;
    }
    public void setMove(MoveInfo move) {
        this.move = move;
    }
}

@Embeddable
class MoveInfo {
    private String name;
    private String url;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
} 