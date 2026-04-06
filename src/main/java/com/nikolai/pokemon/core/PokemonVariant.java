package com.nikolai.pokemon.core;

import java.util.List;

public abstract class PokemonVariant {
    protected String name;
    protected List<String> moves;

    public abstract String getName();
    public abstract List<String> getMoves();
}