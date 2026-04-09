package com.nikolai.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

import com.nikolai.battle.Battle;
import com.nikolai.battle.BattlePokemon;
import com.nikolai.pokemon.core.Pokemon;
import com.nikolai.pokemon.moves.Move;
import com.nikolai.pokemon.moves.MoveSlot;

public class BattleController {

    // ── Action encoding ───────────────────────────────────────────────────
    public static final int ACTION_MOVE_1      = 1;
    public static final int ACTION_MOVE_2      = 2;
    public static final int ACTION_MOVE_3      = 3;
    public static final int ACTION_MOVE_4      = 4;
    public static final int ACTION_SWITCH_BASE = 4;
    public static final int ACTION_FORFEIT     = 11;

    // ── State ─────────────────────────────────────────────────────────────

    private Battle battle;
    private int currentPlayerTurn   = 1;
    private int player1ChosenAction = -1;
    private int player2ChosenAction = -1;

    private enum Mode { LEAD_P1, LEAD_P2, NORMAL, FAINT_P1, FAINT_P2 }
    private Mode mode = Mode.LEAD_P1;

    // ── FXML nodes ────────────────────────────────────────────────────────

    @FXML private Label turnLabel;

    // Opponent HUD (top-right)
    @FXML private Label     oppPoke1NameLabel, oppPoke1LevelLabel, oppPoke1StatusLabel;
    @FXML private Label     oppPoke1VolatileLabel, oppPoke1HpNumLabel;
    @FXML private Rectangle oppPoke1HpTrack, oppPoke1HpFill;
    @FXML private ImageView oppPoke1Sprite;

    // Player HUD (bottom-left)
    @FXML private Label     plyrPoke1NameLabel, plyrPoke1LevelLabel, plyrPoke1StatusLabel;
    @FXML private Label     plyrPoke1VolatileLabel, plyrPoke1HpNumLabel;
    @FXML private Rectangle plyrPoke1HpTrack, plyrPoke1HpFill;
    @FXML private ImageView plyrPoke1Sprite;
    @FXML private ImageView playerTrainerSprite;

    // Battle log
    @FXML private TextArea battleLogArea;

    // Prompt bar
    @FXML private Label promptPokemonNameLabel, promptLabel, plyrPoke1HpStatusBar;

    // Move button VBoxes (for dynamic type colour) + their labels
    @FXML private VBox  move1Btn, move2Btn, move3Btn, move4Btn;
    @FXML private Label move1Name, move1Type, move1PP;
    @FXML private Label move2Name, move2Type, move2PP;
    @FXML private Label move3Name, move3Type, move3PP;
    @FXML private Label move4Name, move4Type, move4PP;

    // Panels
    @FXML private VBox attackPanel, switchPanel, itemPanel, forfeitOverlay;

    // "My" party row
    @FXML private ImageView playerSlot1Icon, playerSlot2Icon, playerSlot3Icon;
    @FXML private ImageView playerSlot4Icon, playerSlot5Icon, playerSlot6Icon;
    @FXML private Button    playerSlot1, playerSlot2, playerSlot3;
    @FXML private Button    playerSlot4, playerSlot5, playerSlot6;

    // Opponent party row
    @FXML private ImageView oppSlot1Icon, oppSlot2Icon, oppSlot3Icon;
    @FXML private ImageView oppSlot4Icon, oppSlot5Icon, oppSlot6Icon;
    @FXML private Button    oppSlot1, oppSlot2, oppSlot3;
    @FXML private Button    oppSlot4, oppSlot5, oppSlot6;

    // Switch panel slots
    @FXML private Button    switchSlot1, switchSlot2, switchSlot3;
    @FXML private Button    switchSlot4, switchSlot5, switchSlot6;
    @FXML private ImageView switchSlot1Icon, switchSlot2Icon, switchSlot3Icon;
    @FXML private ImageView switchSlot4Icon, switchSlot5Icon, switchSlot6Icon;
    @FXML private Label     switchSlot1Name, switchSlot2Name, switchSlot3Name;
    @FXML private Label     switchSlot4Name, switchSlot5Name, switchSlot6Name;
    @FXML private Label     switchSlot1HP,   switchSlot2HP,   switchSlot3HP;
    @FXML private Label     switchSlot4HP,   switchSlot5HP,   switchSlot6HP;

    // ── Init & bind ───────────────────────────────────────────────────────

    @FXML private void initialize() { }

    public void bindBattle(Battle battle) {
        this.battle = battle;
        if (battle == null) return;
        player1ChosenAction = -1;
        player2ChosenAction = -1;
        mode = Mode.LEAD_P1;
        enterLeadOrFaintMode(1);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LEAD / FAINT SELECTION
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Shows the switch panel for the given player to choose a pokemon.
     * Used both for battle-start lead selection and forced replacements after faints.
     * Pass myActive=null so the currently-out slot is not blocked.
     */
    private void enterLeadOrFaintMode(int playerNum) {
        currentPlayerTurn = playerNum;
        List<BattlePokemon> myTeam = playerTeam(playerNum);

        String modeLabel = (mode == Mode.NORMAL || mode == Mode.FAINT_P1 || mode == Mode.FAINT_P2)
                ? "P" + playerNum + " — choose a replacement!"
                : "P" + playerNum + " — choose your lead Pokémon!";

        if (promptLabel        != null) promptLabel.setText(modeLabel);
        if (promptPokemonNameLabel != null) promptPokemonNameLabel.setText("");

        // null myActive so every healthy pokemon is selectable
        updateSwitchPanel(myTeam, null);
        refreshPartyRows();
        showPanel(switchPanel);
        hideForfeitOverlay();
    }


    private void handleLeadOrReplacementChosen(int slotIndex0) {
        battle.setLead(currentPlayerTurn, slotIndex0);
        for (String line : battle.drainLog()) appendBattleLog(line);

        if (mode == Mode.LEAD_P1) {
            mode = Mode.LEAD_P2;
            enterLeadOrFaintMode(2);
        } else if (mode == Mode.LEAD_P2) {
            mode = Mode.NORMAL;
            currentPlayerTurn = 1;
            renderFromPerspective(1);
        } else if (mode == Mode.FAINT_P1) {
            if (battle.player2NeedsReplacement()) {
                mode = Mode.FAINT_P2;
                
                enterLeadOrFaintMode(2);
            } else {
                mode = Mode.NORMAL;
                currentPlayerTurn = 1;
                renderFromPerspective(1);

            }
        } else if (mode == Mode.FAINT_P2) {
            mode = Mode.NORMAL;
            currentPlayerTurn = 1;
            renderFromPerspective(1);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NORMAL TURN FLOW
    // ═══════════════════════════════════════════════════════════════════════

    private void renderFromPerspective(int playerNum) {
        if (battle == null) return;

        updateTurnDisplay();
        updatePrompt(playerNum, playerActive(playerNum));
        refreshPartyRows();
        updatePlayerSide(playerActive(playerNum));
        updateOpponentSide(opponentActive(playerNum));
        updateMovesDisplay(playerActive(playerNum));
        updateSwitchPanel(playerTeam(playerNum), playerActive(playerNum));

        showPanel(attackPanel);
        hideForfeitOverlay();
    }

    /** Refreshes both party icon rows and their tooltips. */
    private void refreshPartyRows() {
        updatePartyDisplay(playerTeam(1),   playerSlotIcons(), playerSlotButtons());
        updatePartyDisplay(playerTeam(2),   oppSlotIcons(),    oppSlotButtons());
        attachPartyTooltips(playerTeam(1),  playerSlotButtons());
        attachPartyTooltips(playerTeam(2),  oppSlotButtons());
    }

    private void handleAction(int action) {
        if (currentPlayerTurn == 1) {
            player1ChosenAction = action;
            currentPlayerTurn = 2;
            renderFromPerspective(2);
        } else {
            player2ChosenAction = action;
            resolveRound();
        }
    }

    private void resolveRound() {
        if (battle == null) return;
        battle.resolveRound(player1ChosenAction, player2ChosenAction);
        for (String line : battle.drainLog()) appendBattleLog(line);

        if (battle.getForfeitedPlayer() == 1) { appendBattleLog("Player 2 wins!"); return; }
        if (battle.getForfeitedPlayer() == 2) { appendBattleLog("Player 1 wins!"); return; }
        if (battle.isTeam1Wiped()) { appendBattleLog("Player 2 wins!"); return; }
        if (battle.isTeam2Wiped()) { appendBattleLog("Player 1 wins!"); return; }

        player1ChosenAction = -1;
        player2ChosenAction = -1;

        // Check for faint replacements
        if (battle.player1NeedsReplacement() && battle.player2NeedsReplacement()) {
            mode = Mode.FAINT_P1;
            enterLeadOrFaintMode(1);
            return;
        }
        if (battle.player1NeedsReplacement()) {
            mode = Mode.FAINT_P1;
            enterLeadOrFaintMode(1);
            return;
        }
        if (battle.player2NeedsReplacement()) {
            mode = Mode.FAINT_P2;
            enterLeadOrFaintMode(2);
            return;
        }

        currentPlayerTurn = 1;
        renderFromPerspective(1);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PERSPECTIVE HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private BattlePokemon playerActive(int p)         { return p==1 ? battle.getPlayer1ActivePokemon() : battle.getPlayer2ActivePokemon(); }
    private BattlePokemon opponentActive(int p)       { return p==1 ? battle.getPlayer2ActivePokemon() : battle.getPlayer1ActivePokemon(); }
    private List<BattlePokemon> playerTeam(int p)     { return p==1 ? battle.getTeam1() : battle.getTeam2(); }
    private List<BattlePokemon> opponentTeam(int p)   { return p==1 ? battle.getTeam2() : battle.getTeam1(); }

    // ═══════════════════════════════════════════════════════════════════════
    // FIELD SIDE UPDATES — status and stat stages always shown
    // ═══════════════════════════════════════════════════════════════════════

    private void updatePlayerSide(BattlePokemon mine) {
        if (mine == null) return;
        Pokemon poke = mine.getPokemon();
        if (poke == null) return;

        if (plyrPoke1NameLabel  != null) plyrPoke1NameLabel.setText(poke.getName());
        if (plyrPoke1LevelLabel != null) plyrPoke1LevelLabel.setText("L" + mine.getLevel());
        updateStatusLabel(plyrPoke1StatusLabel, mine);
        updateVolatileLabel(plyrPoke1VolatileLabel, mine);
        updateHpBar(plyrPoke1HpTrack, plyrPoke1HpFill, mine);
        updateHpNumLabel(plyrPoke1HpNumLabel, mine);
        if (plyrPoke1HpStatusBar != null)
            plyrPoke1HpStatusBar.setText("HP " + mine.getCurrentHp() + "/" + mine.getMaxHp());
        setSprite(plyrPoke1Sprite, poke);
    }

    private void updateOpponentSide(BattlePokemon opp) {
        if (opp == null) return;
        Pokemon poke = opp.getPokemon();
        if (poke == null) return;

        if (oppPoke1NameLabel  != null) oppPoke1NameLabel.setText(poke.getName());
        if (oppPoke1LevelLabel != null) oppPoke1LevelLabel.setText("L" + opp.getLevel());
        updateStatusLabel(oppPoke1StatusLabel, opp);
        updateVolatileLabel(oppPoke1VolatileLabel, opp);
        updateHpBar(oppPoke1HpTrack, oppPoke1HpFill, opp);
        updateHpNumLabel(oppPoke1HpNumLabel, opp);
        setSprite(oppPoke1Sprite, poke);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MOVE DISPLAY — dynamic type colours + tooltips
    // ═══════════════════════════════════════════════════════════════════════

    private void updateMovesDisplay(BattlePokemon myActive) {
        VBox[]  btns  = {move1Btn,  move2Btn,  move3Btn,  move4Btn};
        Label[] names = {move1Name, move2Name, move3Name, move4Name};
        Label[] types = {move1Type, move2Type, move3Type, move4Type};
        Label[] pps   = {move1PP,   move2PP,   move3PP,   move4PP};

        if (myActive == null || myActive.getPokemon() == null) {
            for (int i = 0; i < 4; i++) {
                if (names[i] != null) names[i].setText("—");
                if (types[i] != null) types[i].setText("");
                if (pps[i]   != null) pps[i].setText("");
            }
            return;
        }

        List<MoveSlot> moves = myActive.getPokemon().getMoves();
        for (int i = 0; i < 4; i++) {
            if (i < moves.size() && moves.get(i) != null && moves.get(i).getMove() != null) {
                Move move     = moves.get(i).getMove();
                String typeName = move.getType() != null
                        ? move.getType().getName().toLowerCase() : "normal";

                if (names[i] != null) names[i].setText(move.getName());
                if (types[i] != null) types[i].setText(typeName);
                if (pps[i]   != null) pps[i].setText(moves.get(i).getCurrentPp() + "/" + move.getPp());

                // Swap type colour class on the VBox wrapper
                if (btns[i] != null) {
                    btns[i].getStyleClass().removeIf(c -> c.startsWith("move-btn-") && !c.equals("move-btn-wrapper"));
                    btns[i].getStyleClass().add("move-btn-" + typeName);
                }

                // Move tooltip: description, power, accuracy, category
                installMoveTooltip(btns[i], move, moves.get(i));
            } else {
                if (names[i] != null) names[i].setText("—");
                if (types[i] != null) types[i].setText("");
                if (pps[i]   != null) pps[i].setText("");
            }
        }
    }

    /**
     * Installs a tooltip on a move button VBox showing:
     *   - English description from the API
     *   - Category (Physical / Special / Status)
     *   - Base power, accuracy, PP, priority
     *
     * Uses smart positioning:
     *   - Detects if button is in top or bottom half of screen
     *   - Shows tooltip BELOW if button is in top half
     *   - Shows tooltip ABOVE if button is in bottom half
     *   - Positioned to the left to avoid overlapping the button
     */
    private void installMoveTooltip(VBox btn, Move move, MoveSlot slot) {
        if (btn == null || move == null) return;

        StringBuilder sb = new StringBuilder();

        // Description
        String desc = move.getEnglishEffect();
        if (desc != null && !desc.isBlank()) {
            // Replace "$effect_chance%" placeholder from PokeAPI with actual value
            int chance = move.getAilmentChance();
            desc = desc.replace("$effect_chance%", chance + "%");
            sb.append(desc).append("\n\n");
        }

        // Stats line
        String category = move.isPhysical() ? "Physical" : move.isSpecial() ? "Special" : "Status";
        sb.append("Category:  ").append(category).append("\n");
        sb.append("Type:      ").append(move.getType() != null ? move.getType().getName() : "—").append("\n");
        sb.append("Power:     ").append(move.getPower() > 0 ? move.getPower() : "—").append("\n");
        sb.append("Accuracy:  ").append(move.getAccuracy() > 0 ? move.getAccuracy() + "%" : "—").append("\n");
        sb.append("PP:        ").append(slot.getCurrentPp()).append("/").append(move.getPp()).append("\n");
        if (move.getPriority() != 0)
            sb.append("Priority:  ").append(move.getPriority() > 0 ? "+" : "").append(move.getPriority());

        Tooltip tip = new Tooltip(sb.toString());
        tip.setStyle("-fx-font-size: 11px; -fx-font-family: monospace; -fx-max-width: 320px; -fx-wrap-text: true;");
        tip.setShowDelay(Duration.millis(600));
        tip.setShowDuration(Duration.seconds(15));
        tip.setHideDelay(Duration.millis(100));

        btn.setOnMouseEntered(e -> {

            double tooltipX = btn.localToScreen(0, 0).getX();
            double tooltipY = btn.localToScreen(0, 0).getY() - btn.getHeight() - 100;

            tip.show(btn, tooltipX, tooltipY);
        });
        btn.setOnMouseExited(e -> tip.hide());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PARTY ICONS + TOOLTIPS
    // ═══════════════════════════════════════════════════════════════════════

    private void updatePartyDisplay(List<BattlePokemon> team, ImageView[] icons, Button[] buttons) {
        if (team == null) return;
        for (int i = 0; i < 6; i++) {
            if (i >= team.size()) break;
            BattlePokemon bp = team.get(i);
            if (bp == null) continue;
            if (icons[i]   != null) setSprite(icons[i], bp.getPokemon());
            if (buttons[i] != null) {
                boolean f = bp.isFainted();
                buttons[i].getStyleClass().remove(f ? "party-icon-alive" : "party-icon-fainted");
                buttons[i].getStyleClass().add(   f ? "party-icon-fainted" : "party-icon-alive");
            }
        }
    }

    /**
     * Attaches Tooltips to party icon buttons with smart positioning.
     *
     * Smart positioning:
     *   - Detects if button is in top or bottom half of screen
     *   - Shows tooltip BELOW if button is in top half
     *   - Shows tooltip ABOVE if button is in bottom half
     *   - Positioned to the right to avoid overlapping the button
     *   - Set showDelay=500ms so brief hover-overs don't trigger.
     *   - The tooltip shows the pokemon's BATTLE moves (the 4 from teams.json),
     *     level-adjusted stats, and types.
     */
    private void attachPartyTooltips(List<BattlePokemon> team, Button[] buttons) {
        if (team == null) return;
        for (int i = 0; i < 6 && i < team.size(); i++) {
            BattlePokemon bp = team.get(i);
            if (bp == null || buttons[i] == null) continue;

            Pokemon poke = bp.getPokemon();
            StringBuilder sb = new StringBuilder();

            sb.append(poke.getName())
              .append("  Lv.").append(bp.getLevel())
              .append("  (").append(bp.getNature()).append(")\n");

            // Types
            if (poke.getTypes() != null) {
                String typeStr = poke.getTypes().stream()
                        .filter(ts -> ts.getType() != null)
                        .map(ts -> ts.getType().getName())
                        .reduce((a, b) -> a + " / " + b).orElse("?");
                sb.append("Type: ").append(typeStr).append("\n");
            }

            // Status if any
            if (!bp.hasNoStatus()) {
                sb.append("Status: ").append(bp.getStatusDisplayName()).append("\n");
            }

            // Level-adjusted stats — only the 4 battle moves

            sb.append(bp.getCurrentHp()).append("/").append(bp.getMaxHp()).append(" HP\n");

            sb.append(bp.statSummary()).append("\n\nMoves:");
            List<MoveSlot> slots = poke.getMoves();
            if (slots != null) {
                for (MoveSlot ms : slots) {
                    if (ms == null || ms.getMove() == null) continue;
                    Move m = ms.getMove();
                    String typeName = m.getType() != null ? " [" + m.getType().getName() + "]" : "";
                    String pwr      = m.getPower() > 0 ? " Pwr:" + m.getPower() : "";
                    sb.append("\n  ").append(m.getName()).append(typeName).append(pwr)
                      .append("  PP:").append(ms.getCurrentPp()).append("/").append(m.getPp());
                }
            }

            Tooltip tip = new Tooltip(sb.toString());
            tip.setStyle("-fx-font-size: 11px; -fx-font-family: monospace;");
            tip.setShowDelay(Duration.millis(500));
            tip.setShowDuration(Duration.seconds(20));
            tip.setHideDelay(Duration.millis(100));

            final int buttonIndex = i;  // Capture for lambda
            buttons[i].setOnMouseEntered(e -> {
                // Get screen dimensions and button position
                double screenHeight = buttons[buttonIndex].getScene().getWindow().getHeight();
                double buttonScreenY = buttons[buttonIndex].localToScreen(0, 0).getY();
                double buttonHeight = buttons[buttonIndex].getHeight();

                // Position: show above if button is in bottom half, below if in top half
                double tooltipX = buttons[buttonIndex].localToScreen(buttons[buttonIndex].getWidth() + 5, 0).getX();
                double tooltipY;

                if (buttonScreenY + buttonHeight > screenHeight / 2) {
                    // Bottom half: show ABOVE the button
                    tooltipY = buttons[buttonIndex].localToScreen(0, 0).getY() - 5;
                } else {
                    // Top half: show BELOW the button
                    tooltipY = buttons[buttonIndex].localToScreen(0, buttonHeight + 5).getY();
                }

                tip.show(buttons[buttonIndex], tooltipX, tooltipY);
            });
            buttons[i].setOnMouseExited(e -> tip.hide());
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SWITCH PANEL
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Populates the switch panel slots with sprite, name, and HP.
     * myActive=null during lead/faint mode so all healthy pokemon are available.
     * In normal switch mode, the current active pokemon is disabled.
     */
    private void updateSwitchPanel(List<BattlePokemon> myTeam, BattlePokemon myActive) {
        if (myTeam == null) return;

        Button[]    btns  = {switchSlot1, switchSlot2, switchSlot3, switchSlot4, switchSlot5, switchSlot6};
        ImageView[] icons = {switchSlot1Icon, switchSlot2Icon, switchSlot3Icon,
                             switchSlot4Icon, switchSlot5Icon, switchSlot6Icon};
        Label[]     names = {switchSlot1Name, switchSlot2Name, switchSlot3Name,
                             switchSlot4Name, switchSlot5Name, switchSlot6Name};
        Label[]     hps   = {switchSlot1HP,   switchSlot2HP,   switchSlot3HP,
                             switchSlot4HP,   switchSlot5HP,   switchSlot6HP};

        for (int i = 0; i < 6; i++) {
            if (i >= myTeam.size()) {
                if (btns[i] != null) { btns[i].setVisible(false); btns[i].setManaged(false); }
                continue;
            }

            BattlePokemon bp   = myTeam.get(i);
            Pokemon       poke = bp != null ? bp.getPokemon() : null;

            if (btns[i]  != null) { btns[i].setVisible(true); btns[i].setManaged(true); }
            if (names[i] != null) names[i].setText(poke != null ? poke.getName() : "—");
            if (hps[i]   != null) hps[i].setText(bp != null
                    ? bp.getCurrentHp() + "/" + bp.getMaxHp() : "—");

            // Set sprite — the ImageView is inside the Button's graphic VBox
            if (icons[i] != null) setSprite(icons[i], poke);

            boolean isCurrent = myActive != null && bp == myActive;
            boolean isFainted  = bp != null && bp.isFainted();
            if (btns[i] != null) {
                btns[i].setDisable(isCurrent || isFainted);
                btns[i].setOpacity(isCurrent || isFainted ? 0.4 : 1.0);
            }
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PANEL VISIBILITY
    // ═══════════════════════════════════════════════════════════════════════

    private void showPanel(VBox panel) {
        for (VBox p : new VBox[]{attackPanel, switchPanel, itemPanel}) {
            if (p != null) { p.setVisible(false); p.setManaged(false); }
        }
        if (panel != null) { panel.setVisible(true); panel.setManaged(true); }
    }

    private void showForfeitOverlay() {
        if (forfeitOverlay != null) { forfeitOverlay.setVisible(true); forfeitOverlay.setManaged(true); }
    }

    private void hideForfeitOverlay() {
        if (forfeitOverlay != null) { forfeitOverlay.setVisible(false); forfeitOverlay.setManaged(false); }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // LOW-LEVEL HELPERS
    // ═══════════════════════════════════════════════════════════════════════

    private void updateTurnDisplay() {
        if (turnLabel != null && battle != null) turnLabel.setText("Turn " + battle.getTurn());
    }

    private void updatePrompt(int playerNum, BattlePokemon myActive) {
        String pokeName = (myActive != null && myActive.getPokemon() != null)
                ? myActive.getPokemon().getName() : "???";
        if (promptPokemonNameLabel != null) promptPokemonNameLabel.setText(pokeName);
        if (promptLabel != null)
            promptLabel.setText(" (P" + playerNum + ") — choose your move!");
    }

    /** Updates the status badge (BRN / PAR / PSN / etc.) — always visible when set. */
    private void updateStatusLabel(Label label, BattlePokemon bp) {
        if (label == null || bp == null) return;
        String display = bp.getStatusDisplayName();
        boolean has    = !display.isBlank();
        label.setText(has ? display : "");
        label.setVisible(has);
        label.setManaged(has);
    }

    /** Updates the stat-stage overlay (+2 ATK / -1 DEF) — always visible when non-zero. */
    private void updateVolatileLabel(Label label, BattlePokemon bp) {
        if (label == null || bp == null) return;
        String stages = bp.getStatStageDisplay();
        boolean has   = !stages.isBlank();
        label.setText(has ? stages : "");
        label.setVisible(has);
        label.setManaged(has);
    }

    private void updateHpBar(Rectangle track, Rectangle fill, BattlePokemon bp) {
        if (fill == null || bp == null) return;
        double pct    = bp.getPercentHp();
        double trackW = track != null ? track.getWidth() : 130;
        fill.setWidth(trackW * Math.max(0, Math.min(1, pct)));
        fill.getStyleClass().clear();
        if      (pct > 0.5)  fill.getStyleClass().add("hp-bar-green");
        else if (pct > 0.25) fill.getStyleClass().add("hp-bar-yellow");
        else                 fill.getStyleClass().add("hp-bar-red");
    }

    private void updateHpNumLabel(Label label, BattlePokemon bp) {
        if (label != null && bp != null) label.setText(bp.getCurrentHp() + "/" + bp.getMaxHp());
    }

    /**
     * Sets a sprite safely. Guards against null Sprite, null/empty URL,
     * and Image.isError() which would crash or blank the ImageView.
     */
    private void setSprite(ImageView view, Pokemon poke) {
        if (view == null || poke == null) return;
        var sprite = poke.getSprite();
        if (sprite == null) return;
        try {
            Image img = sprite.getFront();
            if (img != null && !img.isError()) view.setImage(img);
        } catch (Exception ignored) { }
    }

    // Node arrays
    private ImageView[] playerSlotIcons()  { return new ImageView[]{playerSlot1Icon,playerSlot2Icon,playerSlot3Icon,playerSlot4Icon,playerSlot5Icon,playerSlot6Icon}; }
    private Button[]    playerSlotButtons(){ return new Button[]   {playerSlot1,    playerSlot2,    playerSlot3,    playerSlot4,    playerSlot5,    playerSlot6};    }
    private ImageView[] oppSlotIcons()     { return new ImageView[]{oppSlot1Icon,   oppSlot2Icon,   oppSlot3Icon,   oppSlot4Icon,   oppSlot5Icon,   oppSlot6Icon};   }
    private Button[]    oppSlotButtons()   { return new Button[]   {oppSlot1,       oppSlot2,       oppSlot3,       oppSlot4,       oppSlot5,       oppSlot6};       }

    // ── Battle log ────────────────────────────────────────────────────────

    public void appendBattleLog(String msg) {
        if (battleLogArea != null) battleLogArea.appendText(msg + "\n");
    }

    public void clearBattleLog() {
        if (battleLogArea != null) battleLogArea.clear();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // FXML EVENT HANDLERS
    // ═══════════════════════════════════════════════════════════════════════

    @FXML private void onMove1Clicked() { handleAction(ACTION_MOVE_1); }
    @FXML private void onMove2Clicked() { handleAction(ACTION_MOVE_2); }
    @FXML private void onMove3Clicked() { handleAction(ACTION_MOVE_3); }
    @FXML private void onMove4Clicked() { handleAction(ACTION_MOVE_4); }

    /**
     * All 6 switch slot buttons call this. Routes to lead/faint selection
     * or normal in-battle switching depending on current mode.
     */
    @FXML private void onSwitchSlot1() { dispatchSwitchSlot(0); }
    @FXML private void onSwitchSlot2() { dispatchSwitchSlot(1); }
    @FXML private void onSwitchSlot3() { dispatchSwitchSlot(2); }
    @FXML private void onSwitchSlot4() { dispatchSwitchSlot(3); }
    @FXML private void onSwitchSlot5() { dispatchSwitchSlot(4); }
    @FXML private void onSwitchSlot6() { dispatchSwitchSlot(5); }

    private void dispatchSwitchSlot(int slotIndex0) {
        if (mode != Mode.NORMAL) {
            handleLeadOrReplacementChosen(slotIndex0);
        } else {
            handleAction(ACTION_SWITCH_BASE + slotIndex0 + 1);
        }
    }

    @FXML private void onTabAttack()  { if (mode == Mode.NORMAL) showPanel(attackPanel); }
    @FXML private void onTabSwitch()  {
        if (mode == Mode.NORMAL) {
            updateSwitchPanel(playerTeam(currentPlayerTurn), playerActive(currentPlayerTurn));
            showPanel(switchPanel);
        }
    }
    @FXML private void onTabItem()    { if (mode == Mode.NORMAL) showPanel(itemPanel); }
    @FXML private void onTabForfeit() { if (mode == Mode.NORMAL) showForfeitOverlay(); }

    @FXML private void onForfeitConfirmed() { hideForfeitOverlay(); handleAction(ACTION_FORFEIT); }
    @FXML private void onForfeitCancelled() { hideForfeitOverlay(); showPanel(attackPanel); }

    @FXML private void onPartySlot1() {} @FXML private void onPartySlot2() {}
    @FXML private void onPartySlot3() {} @FXML private void onPartySlot4() {}
    @FXML private void onPartySlot5() {} @FXML private void onPartySlot6() {}
    @FXML private void onOppSlot1()   {} @FXML private void onOppSlot2()   {}
    @FXML private void onOppSlot3()   {} @FXML private void onOppSlot4()   {}
    @FXML private void onOppSlot5()   {} @FXML private void onOppSlot6()   {}
}