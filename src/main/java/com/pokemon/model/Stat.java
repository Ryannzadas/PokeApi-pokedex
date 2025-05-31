package com.pokemon.model;

import javax.persistence.Embeddable;

@Embeddable
public class Stat {
    private int base_stat;
    private int effort;
    private StatInfo stat;

    public int getBase_stat() {
        return base_stat;
    }

    public void setBase_stat(int base_stat) {
        this.base_stat = base_stat;
    }

    public int getEffort() {
        return effort;
    }

    public void setEffort(int effort) {
        this.effort = effort;
    }

    public StatInfo getStat() {
        return stat;
    }

    public void setStat(StatInfo stat) {
        this.stat = stat;
    }

    @Override
    public String toString() {
        return stat.getName() + ": " + base_stat;
    }
}

@Embeddable
class StatInfo {
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