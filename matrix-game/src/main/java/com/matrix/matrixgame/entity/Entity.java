package com.matrix.matrixgame.entity;

import com.matrix.matrixgame.board.Position;

public abstract class Entity {

    protected Position position;

    protected final String symbol;

    protected Entity(Position position, String symbol) {
        this.position = position;
        this.symbol = symbol;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getSymbol() {
        return symbol;
    }
}