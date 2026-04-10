package com.nikolai.pokemon.core;

import java.util.List;

public abstract class PokemonVariant {
    // Abstract class for Pokemon types in code, like BattlePokemon, Pokemon.
    protected String name;
    protected List<String> moves;

    public abstract String getName();
    public abstract List<String> getMoves();
}