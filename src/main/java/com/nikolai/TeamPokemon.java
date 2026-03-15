package com.nikolai;

import java.util.List;

public class TeamPokemon {
    private String name;
    private String heldItem;
    private List<String> moves;

    public TeamPokemon(String name, String heldItem, List<String> moves) {
        this.name = name;
        this.heldItem = heldItem;
        this.moves = moves;
    }

    public String getName() { return name; }
    public String getHeldItem() { return heldItem; }
    public List<String> getMoves() { return moves; }
}