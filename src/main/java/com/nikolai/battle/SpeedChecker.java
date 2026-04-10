package com.nikolai.battle;

import java.util.Random;

public class SpeedChecker {
    
    // Utility class for comparing the speed of two combatants to determine move order in battle. 
    // Uses Combatant compareSpeedTo method, and decides ties randomly.
    private static final Random RNG = new Random();

    public static boolean isFaster(Combatant a, Combatant b) { return a.compareSpeedTo(b) > 0; }
    public static boolean isTie(Combatant a, Combatant b)    { return a.compareSpeedTo(b) == 0; }

    public static Combatant faster(Combatant a, Combatant b) {
        int cmp = a.compareSpeedTo(b);
        if (cmp > 0) return a;
        if (cmp < 0) return b;
        return RNG.nextBoolean() ? a : b;
    }

}