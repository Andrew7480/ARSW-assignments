package com.matrix.matrixgame.entity;

import com.matrix.matrixgame.board.Position;
import com.matrix.matrixgame.strategy.MovementStrategy;

public class Neo extends MovableEntity  {

    public Neo(Position position, MovementStrategy strategy) {
        super(position, "N", strategy);
    }
}