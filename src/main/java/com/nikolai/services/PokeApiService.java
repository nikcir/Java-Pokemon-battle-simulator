package com.nikolai.services;

import com.google.gson.Gson;
import com.nikolai.pokemon.core.Pokemon;
import com.nikolai.pokemon.moves.Move;
import com.nikolai.pokemon.moves.MoveSlot;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class PokeApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    // API base URL for fetching Pokemon and move data
    private static final String BASE_URL = "https://pokeapi.co/api/v2/";

    // Fetches full Pokemon data for a specific named Pokemon or ID, including types, stats, abilities, moves, and sprites.
    public Pokemon getPokemon(String nameOrId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "pokemon/" + nameOrId))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(res.body(), Pokemon.class);
    }

    // Fetches full move data for a specific named move or ID, including type, power, accuracy, PP, and effects.
    public Move getMoveData(String moveName) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "move/" + moveName))
                .build();
        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return gson.fromJson(res.body(), Move.class);
    }

    /**
     * Builds a list of exactly the 4 move slots for a pokemon, fetching full
     * move data for each chosen move name. 
     * @param chosenMoveNames the 4 move names from teams.json (e.g. ["earthquake","u-turn",…])
     * @return list of MoveSlot with fully populated Move objects
     */
    public List<MoveSlot> buildMoveSlots(List<String> chosenMoveNames) throws Exception {
        List<MoveSlot> slots = new ArrayList<>();
        for (String moveName : chosenMoveNames) {
            if (moveName == null || moveName.isBlank()) continue;
            try {
                Move fullMove = getMoveData(moveName.trim());
                MoveSlot slot = new MoveSlot(fullMove);
                slots.add(slot);
            } catch (Exception e) {
                System.err.println("Failed to fetch move: " + moveName + " — " + e.getMessage());
            }
        }
        return slots;
    }
}