package com.matrix.matrixgame.strategy;

import com.matrix.matrixgame.board.Board;
import com.matrix.matrixgame.board.Position;

public interface MovementStrategy {
    Position nextMove(Position currentPosition, Board board);
}
