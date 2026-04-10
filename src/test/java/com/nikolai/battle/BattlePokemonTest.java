package com.nikolai.battle;

import com.google.gson.Gson;
import com.nikolai.pokemon.core.Pokemon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BattlePokemonTest {

    private static final Gson GSON = new Gson();

    // base HP=100, level=50 → maxHp = floor(2*100*50/100) + 50 + 10 = 160
    private BattlePokemon buildPokemon(String... typeNames) {
        StringBuilder types = new StringBuilder("[");
        for (int i = 0; i < typeNames.length; i++) {
            if (i > 0) types.append(",");
            types.append("{\"type\":{\"name\":\"").append(typeNames[i]).append("\"}}");
        }
        types.append("]");
        String json = "{\"id\":1,\"name\":\"testmon\",\"types\":" + types
                + ",\"stats\":[{\"base_stat\":100,\"stat\":{\"name\":\"hp\"}}]"
                + ",\"abilities\":[],\"moves\":[]}";
        return new BattlePokemon(GSON.fromJson(json, Pokemon.class), 50, "hardy");
    }

    private BattlePokemon pokemon;

    @BeforeEach
    void setUp() {
        pokemon = buildPokemon("normal");
    }

    // Skade som overstiger HP skal sette HP=0 og drepe pokemon
    @Test
    void applyDamage_moreThanMaxHp() {
        pokemon.applyDamage(9999);
        assertEquals(0, pokemon.getCurrentHp());
        assertTrue(pokemon.isFainted());
    }

    // Healing over maxHp skal ikke overstige maxHp
    @Test
    void heal_cappedAtMaxHp() {
        pokemon.applyDamage(20);
        pokemon.heal(9999);
        assertEquals(pokemon.getMaxHp(), pokemon.getCurrentHp());
    }

    // Fire-type er immun mot burn
    @Test
    void applyStatus_fireTypeImmuneToburn() {
        BattlePokemon firemon = buildPokemon("fire");
        assertFalse(firemon.applyStatus(BattlePokemon.STATUS_BURN));
        assertTrue(firemon.hasNoStatus());
    }
}
