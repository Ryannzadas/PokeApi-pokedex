package com.pokemon.model;

import javax.persistence.Embeddable;

@Embeddable
public class Ability {
    private boolean is_hidden;
    private int slot;
    private AbilityInfo ability;

    public boolean isIs_hidden() {
        return is_hidden;
    }

    public void setIs_hidden(boolean is_hidden) {
        this.is_hidden = is_hidden;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public AbilityInfo getAbility() {
        return ability;
    }

    public void setAbility(AbilityInfo ability) {
        this.ability = ability;
    }

    @Override
    public String toString() {
        return ability.getName();
    }
}

@Embeddable
class AbilityInfo {
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