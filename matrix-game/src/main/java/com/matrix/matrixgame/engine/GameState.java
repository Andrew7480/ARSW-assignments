package com.matrix.matrixgame.engine;

public class GameState {

    private GameStatus status = GameStatus.RUNNING;

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }
}