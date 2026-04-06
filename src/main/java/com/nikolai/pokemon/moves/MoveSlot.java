package com.nikolai.pokemon.moves;

public class MoveSlot {
    private Move move;
    private int currentPp;

    public MoveSlot(Move move) {
        this.move = move;
        this.currentPp = (move != null && move.getPp() > 0) ? move.getPp() : 0;
    }

    public Move getMove()      { return move; }
    public int  getCurrentPp() { return currentPp; }

    public void setMove(Move move) { this.move = move; }
    public void setCurrentPp(int pp) { this.currentPp = pp; }
}