package com.nikolai.battle;


public interface Combatant {

    String getName();
    int getCurrentHp();
    int getMaxHp();
    double getPercentHp();
    boolean isFainted();
    void applyDamage(int damage);

    /**
     * Effective speed including stat stages and status modifiers.
     * Used by Battle.resolveRound() for turn ordering.
     */
    int getEffectiveSpeed();

    /**
     * Compares speed to another combatant.
     * Positive = this is faster, negative = slower, 0 = tie.
     * Override for priority-move or other custom ordering.
     */
    default int compareSpeedTo(Combatant other) {
        return Integer.compare(this.getEffectiveSpeed(), other.getEffectiveSpeed());
    }
}