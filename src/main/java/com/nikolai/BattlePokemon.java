package com.nikolai;

import java.util.List;

public class BattlePokemon extends PokemonVariant {
    private Pokemon pokemon;
    private int currentHp;
    private int maxHp;
    private StatusCondition statusCondition;
    private boolean isFainted;

    public class StatusCondition {
        private String name;
        public String getName() { return name; }
    }

    public BattlePokemon(Pokemon pokemon) {
        this.pokemon = pokemon;
        this.name = pokemon.getName();
        this.maxHp = pokemon.getStats().stream()
                .filter(stat -> stat.getStat().getName().equals("hp"))
                .findFirst()
                .map(StatSlot::getBaseStat)
                .orElse(100);
        this.currentHp = maxHp;
        this.statusCondition = null;
        this.isFainted = false;
        this.moves = pokemon.getMoves().stream()
                .map(MoveSlot::getMove)
                .map(Move::getName)
                .toList();
    }

    @Override
    public String getName() { return name; }

    @Override
    public List<String> getMoves() { return moves; }

    public Pokemon getPokemon() { return pokemon; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public StatusCondition getStatusCondition() { return statusCondition; }
    public boolean isFainted() { return isFainted; }

    public void applyDamage(int damage) {
        currentHp -= damage;
        if (currentHp <= 0) {
            currentHp = 0;
            isFainted = true;
        }
    }

    public void applyStatusCondition(String condition) {
        this.statusCondition = new StatusCondition();
        this.statusCondition.name = condition;
    }

    public double getPercentHp() {
        return (double) currentHp / maxHp;
    }
}
