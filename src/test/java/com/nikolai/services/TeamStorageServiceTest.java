package com.nikolai.services;

import com.nikolai.pokemon.team.PlayerTeam;
import com.nikolai.pokemon.team.TeamPokemon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TeamStorageServiceTest {

    @TempDir
    Path tempDir;

    private TeamStorageService service;

    @BeforeEach
    void setUp() {
        service = new TeamStorageService(tempDir.resolve("teams.txt").toString());
    }

    // Lagret lag skal kunne lastes tilbake med alle felt bevart
    @Test
    void saveAndLoadTeam_allFieldsPreserved() throws IOException {
        TeamPokemon p = new TeamPokemon("pikachu", "oran-berry",
                List.of("thunderbolt", "quick-attack"), "timid", 55);
        service.saveTeam(new PlayerTeam("MyTeam", List.of(p)));

        List<TeamPokemon> loaded = service.loadTeam("MyTeam");

        assertNotNull(loaded);
        assertEquals(1, loaded.size());
        assertEquals("pikachu",    loaded.get(0).getName());
        assertEquals("oran-berry", loaded.get(0).getHeldItem());
        assertEquals("timid",      loaded.get(0).getNature());
        assertEquals(55,           loaded.get(0).getLevel());
        assertEquals(List.of("thunderbolt", "quick-attack"), loaded.get(0).getMoves());
    }
}
