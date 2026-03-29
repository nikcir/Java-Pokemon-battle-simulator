package com.nikolai;

public class SpeedChecker {
    
    public boolean P1FasterThanP2(BattlePokemon P1, BattlePokemon P2) {
        int P1Speed = P1.getPokemon().getStats().stream()
                .filter(stat -> stat.getStat().getName().equals("speed"))
                .findFirst()
                .map(StatSlot::getBaseStat)
                .orElse(0);

        int P2Speed = P2.getPokemon().getStats().stream()
                .filter(stat -> stat.getStat().getName().equals("speed"))
                .findFirst()
                .map(StatSlot::getBaseStat)
                .orElse(0);

        return P1Speed > P2Speed;
    };

}
