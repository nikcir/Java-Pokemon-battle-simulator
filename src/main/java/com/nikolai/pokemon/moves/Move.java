package com.nikolai.pokemon.moves;

import com.google.gson.annotations.SerializedName;
import com.nikolai.pokemon.type.Type;
import java.util.Collections;
import java.util.List;


public class Move {
    // Core Move class, represents a Pokemon move with all its properties and effects.
    private int accuracy;
    private int power;
    private int pp;
    private int priority;
    private String name;
    private Type type;

    @SerializedName("damage_class")
    private DamageClass damageClass;

    @SerializedName("stat_changes")
    private List<MoveStatChange> statChanges;

    private Meta meta;

    @SerializedName("effect_entries")
    private List<EffectEntry> effectEntries;

    private Target target;

    // Nested classes for move properties and effects

    public static class DamageClass {
        private String name;
        public String getName() { return name; }
    }

    public static class MoveStatChange {
        private int change;
        private StatRef stat;
        public int getChange()   { return change; }
        public StatRef getStat() { return stat; }
    }

    public static class StatRef {
        private String name;
        public String getName() { return name; }
    }

    public static class Meta {
        // Meta information about the move, such as ailment inflicted, healing, drain, etc.
        private Ailment ailment;

        @SerializedName("ailment_chance")
        private int ailmentChance;

        // % of max HP this move heals the user. 0 if no healing.
        private int healing;

        // % of damage dealt that drains back. 0 if no drain. Negative means recoil.
        private int drain;

        // Minimum number of turns (for multi-turn moves like sleep, freeze)
        @SerializedName("min_turns")
        private Integer minTurns;

        @SerializedName("max_turns")
        private Integer maxTurns;

        public Ailment getAilment()    { return ailment; }
        public int getAilmentChance()  { return ailmentChance; }
        public int getHealing()        { return healing; }
        public int getDrain()          { return drain; }
        public Integer getMinTurns()   { return minTurns; }
        public Integer getMaxTurns()   { return maxTurns; }
    }

    public static class Ailment {
        private String name; // "paralysis","burn","poison","badly-poisoned","sleep","freeze","none",""
        public String getName() { return name != null ? name : "none"; }
    }

    public static class EffectEntry {
        private String effect;

        @SerializedName("short_effect")
        private String shortEffect;

        private LanguageRef language;

        public String getEffect()       { return effect; }
        public String getShortEffect()  { return shortEffect; }
        public LanguageRef getLanguage(){ return language; }
    }

    public static class LanguageRef {
        private String name;
        public String getName() { return name; }
    }

    public static class Target {
        // Target of the move, "user" for self-targeting moves, 
        // "selected-pokemon" for normal moves, "all-opponents" for spread moves, 
        // "random-opponent" for moves that target a random opponent

        private String name; // "user","selected-pokemon","all-opponents","random-opponent",…
        public String getName() { return name != null ? name : "selected-pokemon"; }
    }

    // Getters for Move properties

    public int    getAccuracy()  { return accuracy; }
    public int    getPower()     { return power; }
    public int    getPp()        { return pp; }
    public int    getPriority()  { return priority; }
    public String getName()      { return name; }
    public Type   getType()      { return type; }

    public DamageClass           getDamageClass()  { return damageClass; }
    public List<MoveStatChange>  getStatChanges()  { return statChanges != null ? statChanges : Collections.emptyList(); }
    public Meta                  getMeta()         { return meta; }
    public Target                getTarget()       { return target; }

    // Returns the English effect description of the move, preferring the short effect if available. Returns empty string if no English entry found.
    public String getEnglishEffect() {
        if (effectEntries == null) return "";
        return effectEntries.stream()
                .filter(e -> e.getLanguage() != null && "en".equals(e.getLanguage().getName()))
                .findFirst()
                .map(e -> e.getShortEffect() != null && !e.getShortEffect().isBlank()
                        ? e.getShortEffect() : e.getEffect())
                .orElse("");
    }

    // Helper methods for move type

    public boolean isPhysical() { return damageClass != null && "physical".equals(damageClass.getName()); }
    public boolean isSpecial()  { return damageClass != null && "special".equals(damageClass.getName()); }
    public boolean isStatus()   { return damageClass == null || "status".equals(damageClass.getName()); }

    // Meta Helper methods

    // % of max HP this move heals the user. 0 if no healing.
    public int getHealPercent()  { return meta != null ? meta.getHealing() : 0; }

    // % of damage dealt that drains back. 0 if no drain. Negative means recoil.
    public int getDrainPercent() { return meta != null ? meta.getDrain() : 0; }

    // Name of ailment inflicted "paralysis". "none" or "" if none.
    public String getAilmentName() {
        if (meta == null || meta.getAilment() == null) return "none";
        return meta.getAilment().getName();
    }

    // Chance of ailment being inflicted, 0 if no ailment.
    public int getAilmentChance() { return meta != null ? meta.getAilmentChance() : 0; }

    // Returns true if this move targets the user (for healing, stat boosts, etc), false if it targets the opponent.
    public boolean targetsUser() {
        return target != null && "user".equals(target.getName());
    }

    public int getMinTurns() { return meta != null && meta.getMinTurns() != null ? meta.getMinTurns() : 0; }
    public int getMaxTurns() { return meta != null && meta.getMaxTurns() != null ? meta.getMaxTurns() : 0; }
}