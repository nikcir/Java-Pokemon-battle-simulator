package com.nikolai.ui.controllers;

import com.nikolai.ui.App;

import javafx.fxml.FXML;

public class MainController {

    // FXML event handlers for the main menu buttons to navigate to different screens: Pokemon search, team creator, team loader, and team selector.

    @FXML
    public void showPokemonSearch() throws Exception {
        App.switchTo("pokemon.fxml");
    }

    @FXML
    public void createTeam() throws Exception {
        App.switchTo("teamCreator.fxml");
    }

    @FXML
    public void loadTeam() throws Exception {
        App.switchTo("teamLoader.fxml");
    }

    @FXML 
    public void teamSelector() throws Exception {
        App.switchTo("teamSelector.fxml");
    }
}