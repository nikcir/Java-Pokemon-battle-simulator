package com.nikolai.ui.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;

import com.nikolai.battle.Battle;
import com.nikolai.battle.BattlePokemon;
import com.nikolai.pokemon.core.Pokemon;
import com.nikolai.pokemon.moves.MoveSlot;
import com.nikolai.services.PokeApiService;
import com.nikolai.services.TeamStorageService;
import com.nikolai.ui.App;

public class TeamSelectorController {

    @FXML private ComboBox<String> player1TeamCombo;
    @FXML private ComboBox<String> player2TeamCombo;

    @FXML
    private void initialize() {
        try {
            List<String> teams = new TeamStorageService().getTeamNames();
            player1TeamCombo.setItems(FXCollections.observableArrayList(teams));
            player2TeamCombo.setItems(FXCollections.observableArrayList(teams));
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void goBack() throws IOException { App.switchTo("main.fxml"); }

    public void startBattle() throws IOException {
        String p1Team = player1TeamCombo.getValue();
        String p2Team = player2TeamCombo.getValue();
        if (p1Team == null || p2Team == null) return;

        try {
            PokeApiService api = new PokeApiService();
            TeamStorageService storage = new TeamStorageService();

            List<BattlePokemon> team1 = buildTeam(api, storage, p1Team);
            List<BattlePokemon> team2 = buildTeam(api, storage, p2Team);

            Battle battle = new Battle(team1, team2);

            FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/nikolai/pokemonBattle.fxml"));
            Parent root = loader.load();
            BattleController controller = loader.getController();
            controller.bindBattle(battle);

            Scene scene = new Scene(root, App.getScreenWidth() / 2, App.getScreenHeight() * 2 / 3);
            App.getPrimaryStage().setScene(scene);

        } catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Fetches pokemon base data, then fetches ONLY the 4 chosen moves (not all 100+).
     * Passes level and nature so BattlePokemon uses correct stat formula.
     * The chosen move slots replace the pokemon's full learnable move list.
     * 1/20 chance for each pokemon to be shiny (front/back sprites use shiny versions).
     */
    private List<BattlePokemon> buildTeam(PokeApiService api,
                                           TeamStorageService storage,
                                           String teamName) throws IOException {
        Random random = new Random();
        return storage.loadTeam(teamName).stream()
                .map(tp -> {
                    try {
                        // 1. Fetch base pokemon data (stats, types, sprite)
                        Pokemon pokemon = api.getPokemon(tp.getName());

                        // 2. Fetch the 4 chosen moves with full data (pp, power, damage_class, etc.)
                        //    tp.getMoves() contains the move names from teams.json
                        List<MoveSlot> chosenMoves = api.buildMoveSlots(tp.getMoves());

                        // 3. Replace the pokemon's full learnable move list with just the 4 chosen
                        pokemon.getMoves().clear();
                        pokemon.getMoves().addAll(chosenMoves);

                        // 4. 1/20 chance for shiny — randomly choose between default and shiny sprites
                        if (random.nextInt(3) == 0) {
                            var sprite = pokemon.getSprite();
                            if (sprite != null) {
                                sprite.setFront(sprite.getFrontShinyUrl());
                                sprite.setBack(sprite.getBackShinyUrl());
                            }
                        } else {
                            var sprite = pokemon.getSprite();
                            if (sprite != null) {
                                sprite.setFront(sprite.getFrontDefaultUrl());
                                sprite.setBack(sprite.getBackDefaultUrl());
                            }
                        }

                        return new BattlePokemon(pokemon, tp.getLevel(), tp.getNature());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .filter(bp -> bp != null)
                .collect(Collectors.toList());
    }
}