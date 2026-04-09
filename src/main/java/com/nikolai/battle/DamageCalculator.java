package com.nikolai.battle;

import com.nikolai.pokemon.moves.Move;
import com.nikolai.pokemon.type.TypeSlot;

import java.util.*;

public class DamageCalculator {

    private static final Random RNG = new Random();

    // ── Result ────────────────────────────────────────────────────────────

    public static class Result {
        public final boolean hit;
        public final int damage;
        public final int healedHp;        // HP healed to attacker (drain/recovery)
        public final double typeMultiplier;
        public final boolean stab;
        public final String statusInflicted;  // null if none
        public final List<StatStageChange> statStageChanges;

        public Result(boolean hit, int damage, int healedHp, double typeMultiplier,
                      boolean stab, String statusInflicted, List<StatStageChange> statStageChanges) {
            this.hit = hit;
            this.damage = damage;
            this.healedHp = healedHp;
            this.typeMultiplier = typeMultiplier;
            this.stab = stab;
            this.statusInflicted = statusInflicted;
            this.statStageChanges = statStageChanges;
        }

        public static Result miss() {
            return new Result(false, 0, 0, 1.0, false, null, Collections.emptyList());
        }
    }

    public static class StatStageChange {
        public final String statName;
        public final int requested;
        public final int applied;
        public StatStageChange(String n, int r, int a) { statName=n; requested=r; applied=a; }
    }

    // ── Public API ────────────────────────────────────────────────────────

    /**
     * Calculates and applies all effects of a move:
     *   - Accuracy check (with stage modifiers)
     *   - Damage (physical/special, with level-adjusted stats and stage multipliers)
     *   - Drain (e.g. Drain Punch heals attacker for 50% of damage)
     *   - Recoil (e.g. Flare Blitz hurts attacker)
     *   - HP recovery (e.g. Recover, Roost)
     *   - Stat stage changes (e.g. Swords Dance, Growl)
     *   - Status infliction (e.g. Scald burns, Thunder Wave paralyzes)
     *
     * Burn halves physical attack; status immunity is checked in BattlePokemon.
     */
    public static Result calculateAndApply(BattlePokemon attacker, int moveSlotIdx, BattlePokemon defender) {
        if (attacker == null || defender == null) return Result.miss();
        if (attacker.getPokemon() == null || defender.getPokemon() == null) return Result.miss();

        var slots = attacker.getPokemon().getMoves();
        if (slots == null || moveSlotIdx >= slots.size()) return Result.miss();

        var slot = slots.get(moveSlotIdx);
        if (slot == null || slot.getCurrentPp() <= 0) return Result.miss();

        Move move = slot.getMove();
        if (move == null) return Result.miss();

        slot.setCurrentPp(slot.getCurrentPp() - 1);

        // Accuracy check
        if (!accuracyCheck(move, attacker, defender)) return Result.miss();

        // Determine actual target: self-targeting moves act on the attacker
        BattlePokemon directTarget = move.targetsUser() ? attacker : defender;

        int damage = 0;
        int healedHp = 0;
        double typeMultiplier = 1.0;
        boolean stab = false;
        String statusInflicted = null;

        // ── Status / non-damaging moves ───────────────────────────────────
        if (move.getPower() <= 0 || move.isStatus()) {

            // Only HP recovery (Recover, Roost, Soft-Boiled)
            if (move.getHealPercent() > 0) {
                int healAmt = (int) Math.max(1, attacker.getMaxHp() * move.getHealPercent() / 100.0);
                healedHp = attacker.heal(healAmt);
            }

            // Stat stage changes
            var stages = applyMoveStatChanges(move, attacker, defender);

            // Status infliction (Toxic, Thunder Wave, etc.)
            statusInflicted = tryInflictStatus(move, attacker, directTarget);

            return new Result(true, 0, healedHp, 1.0, false, statusInflicted, stages);
        }

        // ── Damage moves ──────────────────────────────────────────────────
        boolean isSpecial = move.isSpecial() || (!move.isPhysical() && isSpecialByType(move));

        String atkStat = isSpecial ? "special-attack" : "attack";
        String defStat = isSpecial ? "special-defense" : "defense";

        double atkVal = attacker.getAdjustedStat(atkStat)
                      * attacker.getStatMultiplier(isSpecial ? BattlePokemon.STAGE_SP_ATK : BattlePokemon.STAGE_ATK);

        // Burn halves physical attack
        if (!isSpecial && BattlePokemon.STATUS_BURN.equals(attacker.getStatusCondition())) {
            atkVal *= 0.5;
        }

        double defVal = Math.max(1,
                defender.getAdjustedStat(defStat)
                * defender.getStatMultiplier(isSpecial ? BattlePokemon.STAGE_SP_DEF : BattlePokemon.STAGE_DEF));

        typeMultiplier = typeEffectiveness(move, defender);
        stab = hasStab(attacker, move);
        double roll = (85 + RNG.nextInt(16)) / 100.0;

        double base = (Math.floor(2.0 * attacker.getLevel() / 5.0 + 2)
                * move.getPower() * atkVal / defVal) / 50.0 + 2.0;
        damage = (int) Math.max(1, Math.floor(base * (stab ? 1.5 : 1.0) * typeMultiplier * roll));

        // Don't overkill beyond current HP
        damage = Math.min(damage, defender.getCurrentHp());
        defender.applyDamage(damage);

        // Drain (Drain Punch, Leech Life, Giga Drain)
        if (move.getDrainPercent() > 0 && damage > 0) {
            int drainAmt = (int) Math.max(1, damage * move.getDrainPercent() / 100.0);
            healedHp = attacker.heal(drainAmt);
        }

        // Recoil (Flare Blitz, Double-Edge, Head Smash)
        if (move.getDrainPercent() < 0 && damage > 0) {
            int recoil = (int) Math.max(1, damage * (-move.getDrainPercent()) / 100.0);
            attacker.applyDamage(recoil);
            healedHp = -recoil; // negative = recoil in result
        }

        // Status from damage moves (Scald burn, Thunder paralysis, Blizzard freeze, etc.)
        statusInflicted = tryInflictStatus(move, attacker, defender);

        return new Result(true, damage, healedHp, typeMultiplier, stab, statusInflicted, Collections.emptyList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private static String tryInflictStatus(Move move, BattlePokemon attacker, BattlePokemon target) {
        String ailment = move.getAilmentName();
        if (ailment == null || ailment.isBlank() || "none".equals(ailment)) return null;

        int chance = move.getAilmentChance();
        // chance == 0 means "always inflicts" for primary-effect status moves (Thunder Wave, etc.)
        if (chance == 0) chance = 100;

        if (RNG.nextInt(100) < chance) {
            boolean applied = target.applyStatus(ailment);
            return applied ? ailment : null;
        }
        return null;
    }

    private static List<StatStageChange> applyMoveStatChanges(Move move, BattlePokemon attacker, BattlePokemon defender) {
        var changes = move.getStatChanges();
        if (changes == null || changes.isEmpty()) return Collections.emptyList();

        var results = new ArrayList<StatStageChange>();
        for (var sc : changes) {
            if (sc.getStat() == null) continue;
            int idx = statNameToIndex(sc.getStat().getName());
            if (idx < 0) continue;
            // Positive change = buff (apply to attacker), negative = debuff (apply to defender)
            BattlePokemon target = sc.getChange() >= 0 ? attacker : defender;
            int applied = target.applyStatStage(idx, sc.getChange());
            results.add(new StatStageChange(sc.getStat().getName(), sc.getChange(), applied));
        }
        return results;
    }

    private static boolean accuracyCheck(Move move, BattlePokemon attacker, BattlePokemon defender) {
        int acc = move.getAccuracy();
        if (acc <= 0 || acc > 100) return true;
        double effective = acc * attacker.getAccuracyMultiplier() / defender.getEvasionMultiplier();
        return RNG.nextDouble() * 100 < effective;
    }

    private static double typeEffectiveness(Move move, BattlePokemon defender) {
        if (move.getType() == null) return 1.0;
        String mt = move.getType().getName().toLowerCase();
        var types = defender.getPokemon().getTypes();
        if (types == null || types.isEmpty()) return 1.0;
        double mult = 1.0;
        for (TypeSlot ts : types) {
            if (ts.getType() == null) continue;
            int raw = CHART.getOrDefault(mt, Collections.emptyMap())
                          .getOrDefault(ts.getType().getName().toLowerCase(), 100);
            mult *= raw / 100.0;
        }
        return mult;
    }

    private static boolean hasStab(BattlePokemon attacker, Move move) {
        if (move.getType() == null) return false;
        String mt = move.getType().getName().toLowerCase();
        var types = attacker.getPokemon().getTypes();
        if (types == null) return false;
        return types.stream().filter(ts -> ts.getType() != null)
                .anyMatch(ts -> mt.equals(ts.getType().getName().toLowerCase()));
    }

    private static boolean isSpecialByType(Move move) {
        if (move.getType() == null) return false;
        return switch (move.getType().getName().toLowerCase()) {
            case "fire","water","electric","grass","ice","psychic","dragon","dark","fairy" -> true;
            default -> false;
        };
    }

    public static int statNameToIndex(String name) {
        if (name == null) return -1;
        return switch (name.toLowerCase()) {
            case "attack"          -> BattlePokemon.STAGE_ATK;
            case "defense"         -> BattlePokemon.STAGE_DEF;
            case "special-attack"  -> BattlePokemon.STAGE_SP_ATK;
            case "special-defense" -> BattlePokemon.STAGE_SP_DEF;
            case "speed"           -> BattlePokemon.STAGE_SPEED;
            case "accuracy"        -> BattlePokemon.STAGE_ACCURACY;
            case "evasion"         -> BattlePokemon.STAGE_EVASION;
            default                -> -1;
        };
    }

    // ── Type chart ────────────────────────────────────────────────────────

    private static final Map<String, Map<String, Integer>> CHART = new HashMap<>();
    static {
        add("normal","rock",50);add("normal","ghost",0);add("normal","steel",50);
        add("fire","fire",50);add("fire","water",50);add("fire","grass",200);add("fire","ice",200);
        add("fire","bug",200);add("fire","rock",50);add("fire","dragon",50);add("fire","steel",200);
        add("water","fire",200);add("water","water",50);add("water","grass",50);add("water","ground",200);
        add("water","rock",200);add("water","dragon",50);
        add("electric","water",200);add("electric","electric",50);add("electric","grass",50);
        add("electric","ground",0);add("electric","flying",200);add("electric","dragon",50);
        add("grass","fire",50);add("grass","water",200);add("grass","grass",50);add("grass","poison",50);
        add("grass","ground",200);add("grass","flying",50);add("grass","bug",50);add("grass","rock",200);
        add("grass","dragon",50);add("grass","steel",50);
        add("ice","water",50);add("ice","grass",200);add("ice","ice",50);add("ice","ground",200);
        add("ice","flying",200);add("ice","dragon",200);add("ice","steel",50);
        add("fighting","normal",200);add("fighting","ice",200);add("fighting","poison",50);
        add("fighting","flying",50);add("fighting","psychic",50);add("fighting","bug",50);
        add("fighting","rock",200);add("fighting","ghost",0);add("fighting","dark",200);
        add("fighting","steel",200);add("fighting","fairy",50);
        add("poison","grass",200);add("poison","poison",50);add("poison","ground",50);
        add("poison","rock",50);add("poison","ghost",50);add("poison","steel",0);add("poison","fairy",200);
        add("ground","fire",200);add("ground","electric",200);add("ground","grass",50);
        add("ground","poison",200);add("ground","flying",0);add("ground","bug",50);
        add("ground","rock",200);add("ground","steel",200);
        add("flying","electric",50);add("flying","grass",200);add("flying","fighting",200);
        add("flying","bug",200);add("flying","rock",50);add("flying","steel",50);
        add("psychic","fighting",200);add("psychic","poison",200);add("psychic","psychic",50);
        add("psychic","dark",0);add("psychic","steel",50);
        add("bug","fire",50);add("bug","grass",200);add("bug","fighting",50);add("bug","flying",50);
        add("bug","psychic",200);add("bug","ghost",50);add("bug","dark",200);add("bug","steel",50);
        add("bug","fairy",50);
        add("rock","fire",200);add("rock","ice",200);add("rock","fighting",50);add("rock","ground",50);
        add("rock","flying",200);add("rock","bug",200);add("rock","steel",50);
        add("ghost","normal",0);add("ghost","psychic",200);add("ghost","ghost",200);add("ghost","dark",50);
        add("dragon","steel",50);add("dragon","dragon",200);
        add("dark","fighting",50);add("dark","psychic",200);add("dark","ghost",200);
        add("dark","dark",50);add("dark","fairy",50);
        add("steel","fire",50);add("steel","water",50);add("steel","electric",50);add("steel","ice",200);
        add("steel","rock",200);add("steel","steel",50);add("steel","fairy",200);
        add("fairy","fighting",200);add("fairy","poison",50);add("fairy","bug",200);
        add("fairy","dragon",200);add("fairy","dark",200);add("fairy","steel",50);
    }
    private static void add(String a,String d,int v){CHART.computeIfAbsent(a,k->new HashMap<>()).put(d,v);}
}