package com.nikolai.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Battle {

    private List<BattlePokemon> team1;
    private List<BattlePokemon> team2;
    private BattlePokemon player1ActivePokemon;
    private BattlePokemon player2ActivePokemon;
    private int turn = 0;
    private int forfeitedPlayer = 0;

    private final List<String> pendingLog = new ArrayList<>();
    private static final Random RNG = new Random();

    public Battle(List<BattlePokemon> team1, List<BattlePokemon> team2) {
        this.team1 = team1;
        this.team2 = team2;
    }

    // ── Lead / forced-switch ──────────────────────────────────────────────

    public void setLead(int playerNum, int teamSlotIndex) {
        List<BattlePokemon> team = playerNum == 1 ? team1 : team2;
        if (team == null || teamSlotIndex < 0 || teamSlotIndex >= team.size()) return;
        BattlePokemon target = team.get(teamSlotIndex);
        if (target == null || target.isFainted()) return;
        if (playerNum == 1) player1ActivePokemon = target;
        else                player2ActivePokemon = target;
        pendingLog.add("P" + playerNum + " sends out " + target.getName() + "!");
    }

    public boolean bothPlayersReady() {
        return player1ActivePokemon != null && !player1ActivePokemon.isFainted()
            && player2ActivePokemon != null && !player2ActivePokemon.isFainted();
    }

    public boolean player1NeedsReplacement() {
        return player1ActivePokemon == null || player1ActivePokemon.isFainted();
    }

    public boolean player2NeedsReplacement() {
        return player2ActivePokemon == null || player2ActivePokemon.isFainted();
    }

    // ── Round resolution ──────────────────────────────────────────────────

    public void resolveRound(int p1Action, int p2Action) {
        pendingLog.clear();
        pendingLog.add("══ Turn " + turn + " ══");

        if (p1Action == 11) { forfeitedPlayer = 1; pendingLog.add("Player 1 forfeits!"); turn++; return; }
        if (p2Action == 11) { forfeitedPlayer = 2; pendingLog.add("Player 2 forfeits!"); turn++; return; }

        if (isSwitch(p1Action)) applySwitch(1, p1Action);
        if (isSwitch(p2Action)) applySwitch(2, p2Action);

        // Move priority + speed order
        boolean p1First;

        if (isMove(p1Action) && isMove(p2Action)) {
            var p1Move = player1ActivePokemon.getMove(p1Action - 1);
            var p2Move = player2ActivePokemon.getMove(p2Action - 1);
            int p1Priority = p1Move != null ? p1Move.getPriority() : 0;
            int p2Priority = p2Move != null ? p2Move.getPriority() : 0;
            
            if (p1Priority != p2Priority) {
                p1First = p1Priority > p2Priority;
            } else {
                int cmp = player1ActivePokemon.compareSpeedTo(player2ActivePokemon);
                if (cmp == 0) { p1First = RNG.nextBoolean(); pendingLog.add("Speed tie — order randomised!"); }
                else { p1First = cmp > 0; }
            }
        } else {
            p1First = isMove(p1Action);
        }

        if (p1First) {
            executeMove(1, p1Action);
            if (player2ActivePokemon != null && !player2ActivePokemon.isFainted()) executeMove(2, p2Action);
        } else {
            executeMove(2, p2Action);
            if (player1ActivePokemon != null && !player1ActivePokemon.isFainted()) executeMove(1, p1Action);
        }

        // End-of-turn status damage (burn, poison, bad-poison)
        processEndOfTurnStatuses();

        turn++;
    }

    // ── Move execution ────────────────────────────────────────────────────

    private void executeMove(int actorNum, int action) {
        if (!isMove(action)) return;
        BattlePokemon attacker = actorNum == 1 ? player1ActivePokemon : player2ActivePokemon;
        BattlePokemon defender = actorNum == 1 ? player2ActivePokemon : player1ActivePokemon;
        if (attacker == null || attacker.isFainted() || defender == null) return;

        int moveIdx = action - 1;
        var slots = attacker.getPokemon().getMoves();
        if (moveIdx >= slots.size()) return;
        var slot = slots.get(moveIdx);
        if (slot == null || slot.getMove() == null) return;

        String moveName = slot.getMove().getName();
        pendingLog.add("P" + actorNum + " " + attacker.getName() + " used " + moveName + "!");

        // Status check before move executes
        String statusBlock = attacker.checkStatusBeforeMove();
        if (statusBlock != null) {
            pendingLog.add("  " + statusBlock);
            if (BattlePokemon.isBlockedByStatus(statusBlock)) return;
        }

        DamageCalculator.Result result = DamageCalculator.calculateAndApply(attacker, moveIdx, defender);

        if (!result.hit) { pendingLog.add("  But it missed!"); return; }

        // Damage
        if (result.damage > 0) {
            double pct = defender.getMaxHp() > 0 ? result.damage * 100.0 / defender.getMaxHp() : 0;
            String eff  = effectivenessText(result.typeMultiplier);
            String stab = result.stab ? " (STAB)" : "";
            pendingLog.add(String.format("  Dealt %d dmg (%.1f%% of %s's HP)%s%s",
                    result.damage, pct, defender.getName(), stab,
                    eff.isEmpty() ? "" : " — " + eff));
        }

        // Healing / recoil
        if (result.healedHp > 0) {
            pendingLog.add("  " + attacker.getName() + " restored " + result.healedHp + " HP!");
        } else if (result.healedHp < 0) {
            pendingLog.add("  " + attacker.getName() + " took " + (-result.healedHp) + " recoil damage!");
        }

        // Status inflicted
        if (result.statusInflicted != null) {
            BattlePokemon affected = slot.getMove().targetsUser() ? attacker : defender;
            pendingLog.add("  " + affected.getName() + " was " + statusVerb(result.statusInflicted) + "!");
        }

        // Stat stage changes
        for (var sc : result.statStageChanges) {
            BattlePokemon affected = sc.requested >= 0 ? attacker : defender;
            String who = "P" + (affected == attacker ? actorNum : 3 - actorNum) + " " + affected.getName();
            if (sc.applied == 0) {
                pendingLog.add("  " + who + "'s " + sc.statName + " can't go " + (sc.requested > 0 ? "higher" : "lower") + "!");
            } else {
                String dir = sc.applied > 0 ? "rose" : "fell";
                String amt = Math.abs(sc.applied) >= 2 ? " sharply" : "";
                pendingLog.add("  " + who + "'s " + sc.statName + amt + " " + dir
                        + " (" + (sc.applied > 0 ? "+" : "") + sc.applied + ")");
            }
        }

        // Fainted?
        if (defender.isFainted()) pendingLog.add("  " + defender.getName() + " fainted!");
        if (attacker.isFainted()) pendingLog.add("  " + attacker.getName() + " fainted from recoil!");
    }

    // ── End-of-turn status ────────────────────────────────────────────────

    private void processEndOfTurnStatuses() {
        processStatus(player1ActivePokemon);
        processStatus(player2ActivePokemon);
    }

    private void processStatus(BattlePokemon bp) {
        if (bp == null || bp.isFainted()) return;
        String msg = bp.processEndOfTurnStatus();
        if (msg != null) {
            pendingLog.add(msg);
            if (bp.isFainted()) pendingLog.add("  " + bp.getName() + " fainted from status!");
        }
    }

    // ── Switch ────────────────────────────────────────────────────────────

    private void applySwitch(int playerNum, int action) {
        int idx = (action - 4) - 1; // action 5→idx 0, action 10→idx 5
        List<BattlePokemon> team = playerNum == 1 ? team1 : team2;
        if (team == null || idx < 0 || idx >= team.size()) return;
        BattlePokemon target = team.get(idx);
        if (target == null || target.isFainted()) return;
        BattlePokemon prev = playerNum == 1 ? player1ActivePokemon : player2ActivePokemon;
        if (prev != null) prev.resetStatStages();
        if (playerNum == 1) player1ActivePokemon = target;
        else                player2ActivePokemon = target;
        pendingLog.add("P" + playerNum + " switched to " + target.getName() + "!");
    }

    // ── Log ───────────────────────────────────────────────────────────────

    public List<String> drainLog() {
        List<String> copy = new ArrayList<>(pendingLog);
        pendingLog.clear();
        return copy;
    }

    // ── Win conditions ────────────────────────────────────────────────────

    public boolean isTeam1Wiped() { return team1 != null && team1.stream().allMatch(BattlePokemon::isFainted); }
    public boolean isTeam2Wiped() { return team2 != null && team2.stream().allMatch(BattlePokemon::isFainted); }
    public int getForfeitedPlayer() { return forfeitedPlayer; }

    // ── Utility ───────────────────────────────────────────────────────────

    private static boolean isMove(int a)   { return a >= 1 && a <= 4; }
    private static boolean isSwitch(int a) { return a >= 5 && a <= 10; }

    private static String effectivenessText(double m) {
        if (m == 0)   return "No effect!";
        if (m < 0.9)  return "Not very effective...";
        if (m > 1.1)  return "Super effective!";
        return "";
    }

    private static String statusVerb(String status) {
        return switch (status) {
            case BattlePokemon.STATUS_BURN       -> "burned";
            case BattlePokemon.STATUS_PARALYSIS  -> "paralyzed";
            case BattlePokemon.STATUS_POISON     -> "poisoned";
            case BattlePokemon.STATUS_BAD_POISON -> "badly poisoned";
            case BattlePokemon.STATUS_SLEEP      -> "put to sleep";
            case BattlePokemon.STATUS_FREEZE     -> "frozen solid";
            default                              -> "inflicted with " + status;
        };
    }

    // ── Getters / setters ─────────────────────────────────────────────────

    public List<BattlePokemon> getTeam1()                 { return team1; }
    public List<BattlePokemon> getTeam2()                 { return team2; }
    public BattlePokemon getPlayer1ActivePokemon()        { return player1ActivePokemon; }
    public BattlePokemon getPlayer2ActivePokemon()        { return player2ActivePokemon; }
    public int getTurn()                                  { return turn; }
    public void setPlayer1ActivePokemon(BattlePokemon p)  { player1ActivePokemon = p; }
    public void setPlayer2ActivePokemon(BattlePokemon p)  { player2ActivePokemon = p; }
    @Deprecated public void incrementTurn() { turn++; }
}