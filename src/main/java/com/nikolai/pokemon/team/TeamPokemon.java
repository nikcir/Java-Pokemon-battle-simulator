package com.nikolai.pokemon.team;

import java.util.List;
import com.nikolai.pokemon.core.PokemonVariant;

public class TeamPokemon extends PokemonVariant {
    private String heldItem;
    private String nature;
    private int level = 50;

    public TeamPokemon(String name, String heldItem, List<String> moves, String nature) {
        this.name = name; this.heldItem = heldItem; this.moves = moves; this.nature = nature;
    }

    public TeamPokemon(String name, String heldItem, List<String> moves, String nature, int level) {
        this(name, heldItem, moves, nature);
        this.level = level;
    }

    @Override public String getName()        { return name; }
    @Override public List<String> getMoves() { return moves; }

    public String getHeldItem() { return heldItem; }
    public String getNature()   { return nature; }
    public int    getLevel()    { return level > 0 ? level : 50; }
}