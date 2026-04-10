package com.nikolai.pokemon.core;

import java.util.List;

import com.nikolai.battle.AbilitySlot;
import com.nikolai.pokemon.moves.MoveSlot;
import com.nikolai.pokemon.stat.StatSlot;
import com.nikolai.pokemon.type.TypeSlot;
import com.nikolai.ui.Sprite;

public class Pokemon {
    // Core Pokemon class
    private int id;
    private String name;
    private List<TypeSlot> types;
    private List<StatSlot> stats;
    private List<AbilitySlot> abilities;
    private List<MoveSlot> moves;
    private Sprite sprites;

    public int getId() { return id; }
    public String getName() { return name; }
    public List<TypeSlot> getTypes() { return types; }
    public List<StatSlot> getStats() { return stats; }
    public List<AbilitySlot> getAbilities() { return abilities; }
    public List<MoveSlot> getMoves() { return moves; }
    public Sprite getSprite() { return sprites; }
}