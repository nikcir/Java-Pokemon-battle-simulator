package com.nikolai.battle;

 
public interface Combatant {

    // Interface for pokemon in battle, used by SpeedChecker and Battle. Implemented by BattlePokemon.

    String getName();
    int getCurrentHp();
    int getMaxHp();
    double getPercentHp();
    boolean isFainted();

    void applyDamage(int damage);

    int getEffectiveSpeed();

    // Default method for comparing speed, used by SpeedChecker. Returns positive if this is faster, negative if slower, 0 if tie.
    default int compareSpeedTo(Combatant other) {
        return Integer.compare(this.getEffectiveSpeed(), other.getEffectiveSpeed());
    }
}