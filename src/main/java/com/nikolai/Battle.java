package com.nikolai;

import java.util.List;

public class Battle {
    private List<BattlePokemon> team1;
    private List<BattlePokemon> team2;

    private BattlePokemon player1ActivePokemon;
    private BattlePokemon player2ActivePokemon;

    // private EntryHazards;



    private int turn = 0;

    


    public Battle(List<TeamPokemon> team1, List<TeamPokemon> team2) {
        this.team1 = team1;
        this.team2 = team2;
    }


}
