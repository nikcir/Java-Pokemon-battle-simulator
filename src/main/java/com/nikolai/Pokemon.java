package com.nikolai;

import java.util.List;
import javafx.scene.image.Image;

public class Pokemon {
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