package com.nikolai;

import java.util.List;

public class TeamPokemon extends PokemonVariant {
    private String heldItem;
    private String nature;

    public TeamPokemon(String name, String heldItem, List<String> moves, String nature) {
        this.name = name;
        this.heldItem = heldItem;
        this.moves = moves;
        this.nature = nature;
    }

    @Override
    public String getName() { return name; }

    @Override
    public List<String> getMoves() { return moves; }

    public String getHeldItem() { return heldItem; }
    public String getNature() { return nature; }
}