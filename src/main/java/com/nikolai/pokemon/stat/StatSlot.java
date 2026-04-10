package com.nikolai.pokemon.stat;

public class StatSlot {
    // Class representing a stat slot for a Pokemon, containing the base stat and the stat name. Used in BattlePokemon.
    private Integer base_stat;
    private Stat stat;
    public Stat getStat() { return stat; }
    public int getBaseStat() { return base_stat; }
}