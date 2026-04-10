package com.nikolai.battle;

import com.nikolai.pokemon.core.Pokemon;
import com.nikolai.pokemon.stat.StatSlot;
import java.util.Map;

/**
 * Official Gen 3+ stat formula (no IVs/EVs — use base stat only):
 *   HP    = floor(2 * base * level / 100) + level + 10
 *   Other = floor(floor(2 * base * level / 100) + 5) * nature_modifier
 */
public class StatCalculator {

    private static final Map<String, String[]> NATURE_MODIFIERS = Map.ofEntries(
        Map.entry("hardy",   new String[]{null,null}),
        Map.entry("docile",  new String[]{null,null}),
        Map.entry("serious", new String[]{null,null}),
        Map.entry("bashful", new String[]{null,null}),
        Map.entry("quirky",  new String[]{null,null}),
        Map.entry("lonely",  new String[]{"attack","defense"}),
        Map.entry("brave",   new String[]{"attack","speed"}),
        Map.entry("adamant", new String[]{"attack","special-attack"}),
        Map.entry("naughty", new String[]{"attack","special-defense"}),
        Map.entry("bold",    new String[]{"defense","attack"}),
        Map.entry("relaxed", new String[]{"defense","speed"}),
        Map.entry("impish",  new String[]{"defense","special-attack"}),
        Map.entry("lax",     new String[]{"defense","special-defense"}),
        Map.entry("timid",   new String[]{"speed","attack"}),
        Map.entry("hasty",   new String[]{"speed","defense"}),
        Map.entry("jolly",   new String[]{"speed","special-attack"}),
        Map.entry("naive",   new String[]{"speed","special-defense"}),
        Map.entry("modest",  new String[]{"special-attack","attack"}),
        Map.entry("mild",    new String[]{"special-attack","defense"}),
        Map.entry("quiet",   new String[]{"special-attack","speed"}),
        Map.entry("rash",    new String[]{"special-attack","special-defense"}),
        Map.entry("calm",    new String[]{"special-defense","attack"}),
        Map.entry("gentle",  new String[]{"special-defense","defense"}),
        Map.entry("sassy",   new String[]{"special-defense","speed"}),
        Map.entry("careful", new String[]{"special-defense","special-attack"})
    );

    public static int calcHp(int base, int level) {
        return (int) Math.floor(2.0 * base * level / 100.0) + level + 10;
    }

    public static int calcStat(int base, int level, String stat, String nature) {
        double raw = Math.floor(2.0 * base * level / 100.0) + 5;
        return (int) Math.floor(raw * getNatureModifier(nature, stat));
    }
    // Returns the nature modifier for a given stat based on the nature. 1.1 if boosted, 0.9 if hindered, 1.0 otherwise.
    public static double getNatureModifier(String nature, String stat) {
        if (nature == null || stat == null) return 1.0;
        String[] mods = NATURE_MODIFIERS.get(nature.toLowerCase());
        if (mods == null) return 1.0;
        if (stat.equals(mods[0])) return 1.1;
        if (stat.equals(mods[1])) return 0.9;
        return 1.0;
    }
    // Utility method to get the base stat for a given stat name from a Pokemon object. Returns 1 if not found or invalid input.
    public static int getBase(Pokemon pokemon, String statName) {
        if (pokemon == null || pokemon.getStats() == null) return 1;
        return pokemon.getStats().stream()
                .filter(s -> s.getStat() != null && statName.equals(s.getStat().getName()))
                .findFirst()
                .map(StatSlot::getBaseStat)
                .orElse(1);
    }
    // Utility method to generate a summary string of all the stats for a Pokemon
    public static String statSummary(Pokemon pokemon, int level, String nature) {
        String[] names  = {"hp","attack","defense","special-attack","special-defense","speed"};
        String[] labels = {"HP","ATK","DEF","SP.ATK","SP.DEF","SPE"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < names.length; i++) {
            int base = getBase(pokemon, names[i]);
            int val  = "hp".equals(names[i])
                    ? calcHp(base, level)
                    : calcStat(base, level, names[i], nature);
            if (sb.length() > 0) sb.append(" / ");
            sb.append(labels[i]).append(":").append(val);
        }
        return sb.toString();
    }
}