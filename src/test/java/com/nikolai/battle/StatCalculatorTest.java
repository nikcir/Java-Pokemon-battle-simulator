package com.nikolai.battle;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StatCalculatorTest {

    // HP-formelen: floor(2*base*level/100) + level + 10
    // base=45, level=50 → floor(45) + 60 = 105
    @Test
    void calcHp_returnsCorrectValue() {
        assertEquals(105, StatCalculator.calcHp(45, 50));
    }

    // Adamant forsterker attack med 1.1x
    // base=55, level=50 → floor(60 * 1.1) = 66
    @Test
    void calcStat_boostingNature_increasesValue() {
        assertEquals(66, StatCalculator.calcStat(55, 50, "attack", "adamant"));
    }
}
