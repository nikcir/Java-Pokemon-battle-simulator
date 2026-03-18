package com.nikolai;

import java.util.List;

public class TeamPokemon {
    private String name;
    private String heldItem;
    private List<String> moves;
    private String nature;

    
    // private Pokemon pokemon;

    // private final PokeApiService pokeApiService = new PokeApiService();

    public TeamPokemon(String name, String heldItem, List<String> moves, String nature) {
        this.name = name;
        this.heldItem = heldItem;
        this.moves = moves;
        this.nature = nature;

        // this.pokemon = pokeApiService.getPokemon(name);
    }

    public String getName() { return name; }
    public String getHeldItem() { return heldItem; }
    public List<String> getMoves() { return moves; }
    public String getNature() { return nature; }
}