package com.nikolai;

import java.util.List;

// import java.util.Arrays;

// import com.nikolai.Pokemon.TypeSlot;
import javafx.scene.image.Image;




public class Pokemon {
    private int id;
    private String name;
    private List<TypeSlot> types;
    private List<StatSlot> stats;

    // private int maxHp;
    // private int hp;
    // private int attack;
    // private int defense;
    // private int specialAttack;
    // private int specialDefense;
    // private int speed;

    private List<AbilitySlot> abilities;
    private List<MoveSlot> moves;

    // private SpriteFront spriteUrl;
    // private SpriteFrontShiny spriteUrlShiny;

    private Sprite sprites;

    public int getId() { return id; }
    public String getName() { return name; }
    public List<TypeSlot> getTypes() { return types; }

    public List<StatSlot> getStats() { return stats; }

    // public List<Integer> getStats() { return Arrays.asList(maxHp, hp, attack, defense, specialAttack, specialDefense, speed); }

    // public int getMaxHp() { return maxHp; }
    // public int getHp() { return hp; }
    // public int getAttack() { return attack; }
    // public int getDefense() { return defense; }
    // public int getSpecialAttack() { return specialAttack; }
    // public int getSpecialDefense() { return specialDefense; }
    // public int getSpeed() { return speed; }

    public List<AbilitySlot> getAbilities() { return abilities; }
    public List<MoveSlot> getMoves() { return moves; }

    // public Sprite getSpriteUrl() { return spriteUrl}
    // public Image getSpriteUrlShiny() { return spriteUrlShiny}

    public Sprite getSprite() { return sprites;}
    // public Image getSpriteShiny() { return sprite.getFrontShiny(); }

    public static class TypeSlot {
        private Type type;
        public Type getType() { return type; }
    }

    public static class Type {
        private String name;
        public String getName() { return name; }
    }

    public static class StatSlot {
        private Integer base_stat;
        private Stat stat;
        public Stat getStat() { return stat; }
        public int getBaseStat() { return base_stat; }
    }

    public static class Stat {
        private String name;
        public String getName() { return name; }
    }


    public static class AbilitySlot {
        private Ability ability;
        public Ability getAbility() { return ability; }
    }

    public static class Ability {
        private String name;
        public String getName() { return name; }
    }

    public static class MoveSlot {
        private Move move;
        public Move getMove() { return move; }
    }

    // public static class Move {
    //     private String name;
    //     public String getName() { return name; }
    // }

    // public static class SpriteFront {
    //     private String front_default;
    //     public Image getFrontDefault() { return new Image(front_default); }
    // }

    // public static class SpriteFrontShiny {
    //     private String front_shiny;
    //     public Image getFrontShiny() { return new Image(front_shiny); }
    // }

    public static class Sprite {
        private String front_default;
        private String front_shiny;

        public Image getFrontDefault() { return new Image(front_default); }
        public Image getFrontShiny() { return new Image(front_shiny); }
    }
    
}