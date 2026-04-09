package com.nikolai.pokemon.moves;

import com.google.gson.annotations.SerializedName;
import com.nikolai.pokemon.type.Type;
import java.util.Collections;
import java.util.List;

/**
 * Maps to the PokeAPI /move/{name} endpoint.
 *
 * Key fields used in battle:
 *   damage_class   physical / special / status
 *   stat_changes   e.g. Swords Dance: [{change:+2, stat:{name:"attack"}}]
 *   meta.heal      % of max HP healed (positive = heals user, e.g. Recover = 50)
 *   meta.drain     % of damage dealt drained back to user (e.g. Drain Punch = 50)
 *   meta.ailment   status condition inflicted (paralysis, burn, poison, sleep, …)
 *   meta.ailment_chance  % chance to inflict the ailment
 *   effect_entries English short_effect description shown in tooltip
 *   target         user / selected-pokemon / etc.
 */
public class Move {

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

    // ── Inner types ───────────────────────────────────────────────────────

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
        private Ailment ailment;

        @SerializedName("ailment_chance")
        private int ailmentChance;

        /** % of max HP healed. 50 = Recover, 25 = Roost, etc. Positive heals user. */
        private int healing;

        /**
         * % of damage dealt drained back. 50 = Drain Punch, 75 = Leech Life.
         * Negative = user takes recoil (e.g. -25 = 25% recoil).
         */
        private int drain;

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
// healing fra api
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
        private String name; // "user","selected-pokemon","all-opponents","random-opponent",…
        public String getName() { return name != null ? name : "selected-pokemon"; }
    }

    // ── Getters ───────────────────────────────────────────────────────────

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

    /** English short_effect, falling back to long effect, then empty string. */
    public String getEnglishEffect() {
        if (effectEntries == null) return "";
        return effectEntries.stream()
                .filter(e -> e.getLanguage() != null && "en".equals(e.getLanguage().getName()))
                .findFirst()
                .map(e -> e.getShortEffect() != null && !e.getShortEffect().isBlank()
                        ? e.getShortEffect() : e.getEffect())
                .orElse("");
    }

    // ── Damage class helpers ──────────────────────────────────────────────

    public boolean isPhysical() { return damageClass != null && "physical".equals(damageClass.getName()); }
    public boolean isSpecial()  { return damageClass != null && "special".equals(damageClass.getName()); }
    public boolean isStatus()   { return damageClass == null || "status".equals(damageClass.getName()); }

    // ── Meta helpers ──────────────────────────────────────────────────────

    /** % of max HP this move heals the user. 0 if no healing. */
    public int getHealPercent()  { return meta != null ? meta.getHealing() : 0; }

    /** % of damage dealt that drains back. 0 if no drain. Negative = recoil. */
    public int getDrainPercent() { return meta != null ? meta.getDrain() : 0; }

    /** Name of ailment inflicted, e.g. "paralysis". "none" or "" if none. */
    public String getAilmentName() {
        if (meta == null || meta.getAilment() == null) return "none";
        return meta.getAilment().getName();
    }

    /** % chance to inflict the ailment. 0 = never, 100 = always. */
    public int getAilmentChance() { return meta != null ? meta.getAilmentChance() : 0; }

    /** True if this move targets the user (self-targeting moves like Recover). */
    public boolean targetsUser() {
        return target != null && "user".equals(target.getName());
    }

    /** Minimum number of turns (for multi-turn moves like sleep, bind). */
    public int getMinTurns() { return meta != null && meta.getMinTurns() != null ? meta.getMinTurns() : 0; }
    public int getMaxTurns() { return meta != null && meta.getMaxTurns() != null ? meta.getMaxTurns() : 0; }
}