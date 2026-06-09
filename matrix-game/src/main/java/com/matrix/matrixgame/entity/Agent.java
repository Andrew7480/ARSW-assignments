package com.matrix.matrixgame.entity;

import com.matrix.matrixgame.board.Position;
import com.matrix.matrixgame.strategy.MovementStrategy;

public class Agent extends MovableEntity {

    public Agent(Position position, MovementStrategy strategy) {
        super(position, "A", strategy);
    }
}