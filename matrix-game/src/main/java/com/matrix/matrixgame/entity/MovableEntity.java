package com.matrix.matrixgame.entity;

import com.matrix.matrixgame.board.Board;
import com.matrix.matrixgame.board.Position;
import com.matrix.matrixgame.strategy.MovementStrategy;

public abstract class MovableEntity extends Entity {

    protected final MovementStrategy strategy;

    protected MovableEntity(Position position, String symbol, MovementStrategy strategy) {
        super(position, symbol);
        this.strategy = strategy;
    }

    public Position calculateNextMove(Board board) {
        return strategy.nextMove(position, board);
    }
}