package com.pokemon.model;

import javax.persistence.Embeddable;

@Embeddable
public class Type {
    private int slot;
    private TypeInfo type;

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public TypeInfo getType() {
        return type;
    }

    public void setType(TypeInfo type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type.getName();
    }
}

@Embeddable
class TypeInfo {
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