package com.nikolai.battle;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.nikolai.pokemon.core.Pokemon;
import com.nikolai.pokemon.core.PokemonVariant;
import com.nikolai.pokemon.moves.Move;
import com.nikolai.pokemon.moves.MoveSlot;

public class BattlePokemon extends PokemonVariant implements Combatant {

    // Stat change stage indices
    public static final int STAGE_ATK      = 0;
    public static final int STAGE_DEF      = 1;
    public static final int STAGE_SP_ATK   = 2;
    public static final int STAGE_SP_DEF   = 3;
    public static final int STAGE_SPEED    = 4;
    public static final int STAGE_ACCURACY = 5;
    public static final int STAGE_EVASION  = 6;
    private static final int NUM_STAGES    = 7;

    // Status condition constants
    public static final String STATUS_NONE       = "none";
    public static final String STATUS_BURN       = "burn";
    public static final String STATUS_PARALYSIS  = "paralysis";
    public static final String STATUS_POISON     = "poison";
    public static final String STATUS_BAD_POISON = "badly-poisoned";
    public static final String STATUS_SLEEP      = "sleep";
    public static final String STATUS_FREEZE     = "freeze";

    private static final Random RNG = new Random();

    // Core data

    private final Pokemon pokemon;
    private final int level;
    private final String nature;

    private int currentHp;
    private int maxHp;
    private boolean isFainted;
    private int[] statStages = new int[NUM_STAGES];

    // Status condition counters

    private String statusCondition = STATUS_NONE;

    private int sleepTurnsRemaining = 0;

    private int badPoisonCounter = 0;

    // Constructed from a Pokemon + level + nature, extracts stats and moves

    public BattlePokemon(Pokemon pokemon, int level, String nature) {
        this.pokemon = pokemon;
        this.level   = level;
        this.nature  = nature != null ? nature.toLowerCase() : "hardy";
        this.name    = pokemon.getName();

        int baseHp  = StatCalculator.getBase(pokemon, "hp");
        this.maxHp  = StatCalculator.calcHp(baseHp, level);
        this.currentHp = maxHp;
        Arrays.fill(statStages, 0);

        this.moves = pokemon.getMoves().stream()
                .map(MoveSlot::getMove)
                .map(Move::getName)
                .toList();
    }

    // Combatant interface

    @Override public String getName()       { return name; }
    @Override public int getCurrentHp()    { return currentHp; }
    @Override public int getMaxHp()        { return maxHp; }
    @Override public boolean isFainted()   { return isFainted; }
    @Override public double getPercentHp() { return maxHp == 0 ? 0 : (double) currentHp / maxHp; }

    @Override
    public void applyDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);
        if (currentHp == 0) isFainted = true;
    }

    // Heals the pokemon by the given HP amount, capped at max HP
    public int heal(int amount) {
        if (isFainted) return 0;
        int before = currentHp;
        currentHp = Math.min(maxHp, currentHp + amount);
        return currentHp - before; // actual healed
    }

    @Override
    public int getEffectiveSpeed() {
        int base = StatCalculator.getBase(pokemon, "speed");
        int adj  = StatCalculator.calcStat(base, level, "speed", nature);
        double stageMult = getStatMultiplier(STAGE_SPEED);
        // Paralysis halves speed
        double paraFactor = STATUS_PARALYSIS.equals(statusCondition) ? 0.5 : 1.0;
        return (int) (adj * stageMult * paraFactor);
    }

    // Returns true if this pokemon has no status condition
    public boolean hasNoStatus() {
        return STATUS_NONE.equals(statusCondition) || statusCondition == null || statusCondition.isBlank();
    }

    // Attempts to apply the given status condition. Returns true if successful, false if it failed (already has a status or is immune).

    public boolean applyStatus(String status) {
        if (!hasNoStatus()) return false; // already has a status
        if (!canBeAffected(status)) return false;

        this.statusCondition = status;
        if (STATUS_SLEEP.equals(status)) {
            sleepTurnsRemaining = 1 + RNG.nextInt(3); // 1–3 turns
        }
        if (STATUS_BAD_POISON.equals(status)) {
            badPoisonCounter = 0;
        }
        return true;
    }

    // Cures the pokemon's status condition entirely.
    public void cureStatus() {
        statusCondition = STATUS_NONE;
        sleepTurnsRemaining = 0;
        badPoisonCounter = 0;
    }

    // applies end-of-turn damage from burn/poison and returns a log message describing the effect, or null if no damage/status.

    public String processEndOfTurnStatus() {
        if (hasNoStatus() || isFainted) return null;

        switch (statusCondition) {
            case STATUS_BURN -> {
                int dmg = Math.max(1, maxHp / 16);
                applyDamage(dmg);
                return name + " is hurt by its burn! (-" + dmg + " HP)";
            }
            case STATUS_POISON -> {
                int dmg = Math.max(1, maxHp / 8);
                applyDamage(dmg);
                return name + " is hurt by poison! (-" + dmg + " HP)";
            }
            case STATUS_BAD_POISON -> {
                badPoisonCounter++;
                int dmg = Math.max(1, maxHp * badPoisonCounter / 16);
                applyDamage(dmg);
                return name + " is badly hurt by poison! (-" + dmg + " HP, turn " + badPoisonCounter + ")";
            }
            default -> { return null; }
        }
    }

    // Checks if the pokemon can act this turn based on its status condition. 
    // Returns a log message if the move is blocked by status, or null if it can act.

    public String checkStatusBeforeMove() {
        if (hasNoStatus()) return null;

        return switch (statusCondition) {
            case STATUS_SLEEP -> {
                if (sleepTurnsRemaining > 0) {
                    sleepTurnsRemaining--;
                    if (sleepTurnsRemaining == 0) {
                        cureStatus();
                        yield name + " woke up!";
                    }
                    yield name + " is fast asleep!";
                }
                cureStatus();
                yield name + " woke up!";
            }
            case STATUS_PARALYSIS -> {
                if (RNG.nextInt(4) == 0) { // 25% chance
                    yield name + " is fully paralyzed and can't move!";
                }
                yield null; // can act
            }
            case STATUS_FREEZE -> {
                if (RNG.nextInt(5) == 0) { // 20% thaw chance
                    cureStatus();
                    yield name + " thawed out!";
                }
                yield name + " is frozen solid!";
            }
            default -> null; // burn/poison dont block the move
        };
    }

    // Checks if a status message indicates that the pokemon is currently blocked by a status condition: 
    // ("is fast asleep", "is paralyzed and can't move", "is frozen solid"). 
    // Returns false for messages indicating the pokemon just woke up or thawed out.

    public static boolean isBlockedByStatus(String statusMessage) {
        if (statusMessage == null) return false;
        return !statusMessage.endsWith("woke up!") && !statusMessage.endsWith("thawed out!");
    }

    // Converts the status condition to 3-letter string for display, or empty string if no status.

    public String getStatusDisplayName() {
        return switch (statusCondition) {
            case STATUS_BURN       -> "BRN";
            case STATUS_PARALYSIS  -> "PAR";
            case STATUS_POISON     -> "PSN";
            case STATUS_BAD_POISON -> "TOX";
            case STATUS_SLEEP      -> "SLP";
            case STATUS_FREEZE     -> "FRZ";
            default                -> "";
        };
    }

    // Checks if the pokemon can be affected by the given status condition based on its types. Returns true if it can be affected, false if it is immune.

    private boolean canBeAffected(String status) {
        if (pokemon == null || pokemon.getTypes() == null) return true;
        List<String> types = pokemon.getTypes().stream()
                .filter(ts -> ts.getType() != null)
                .map(ts -> ts.getType().getName().toLowerCase())
                .toList();
        return switch (status) {
            case STATUS_BURN       -> !types.contains("fire");
            case STATUS_PARALYSIS  -> !types.contains("electric");
            case STATUS_POISON, STATUS_BAD_POISON -> !types.contains("poison") && !types.contains("steel");
            case STATUS_FREEZE     -> !types.contains("ice");
            default -> true;
        };
    }

    // Stat stages

    public int getStatStage(int idx) {
        return (idx >= 0 && idx < NUM_STAGES) ? statStages[idx] : 0;
    }

    public int applyStatStage(int idx, int delta) {
        if (idx < 0 || idx >= NUM_STAGES) return 0;
        int before = statStages[idx];
        statStages[idx] = Math.max(-6, Math.min(6, before + delta));
        return statStages[idx] - before;
    }

    public double getStatMultiplier(int idx) {
        int s = getStatStage(idx);
        return s >= 0 ? (2.0 + s) / 2.0 : 2.0 / (2.0 - s);
    }

    public double getAccuracyMultiplier() {
        int s = getStatStage(STAGE_ACCURACY);
        return s >= 0 ? (3.0 + s) / 3.0 : 3.0 / (3.0 - s);
    }

    public double getEvasionMultiplier() {
        int s = getStatStage(STAGE_EVASION);
        return s >= 0 ? (3.0 + s) / 3.0 : 3.0 / (3.0 - s);
    }

    public void resetStatStages() { Arrays.fill(statStages, 0); }

    // For example, "+1 ATK / -2 DEF"
    public String getStatStageDisplay() {
        String[] labels = {"ATK","DEF","SP.ATK","SP.DEF","SPE","ACC","EVA"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < NUM_STAGES; i++) {
            if (statStages[i] != 0) {
                if (sb.length() > 0) sb.append(" / ");
                sb.append(statStages[i] > 0 ? "+" : "").append(statStages[i]).append(" ").append(labels[i]);
            }
        }
        return sb.toString();
    }

    // Level and nature adjustments for stats

    public int getAdjustedStat(String statName) {
        int base = StatCalculator.getBase(pokemon, statName);
        if ("hp".equals(statName)) return StatCalculator.calcHp(base, level);
        return StatCalculator.calcStat(base, level, statName, nature);
    }

    public String statSummary() {
        return StatCalculator.statSummary(pokemon, level, nature);
    }

    // Getters

    public Pokemon getPokemon()           { return pokemon; }
    public int     getLevel()             { return level; }
    public String  getNature()            { return nature; }
    public String  getStatusCondition()   { return statusCondition; }

    // returns the Move object for the move in the given move slot index (0-3)
    public Move getMove(int index) {
        var slots = pokemon.getMoves();
        if (index < 0 || index >= slots.size()) return null;
        MoveSlot slot = slots.get(index);
        return slot != null ? slot.getMove() : null;
    }

    @Override public List<String> getMoves() { return moves; }

}