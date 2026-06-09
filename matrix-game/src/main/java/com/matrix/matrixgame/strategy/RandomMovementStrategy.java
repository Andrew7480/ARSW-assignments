package com.matrix.matrixgame.strategy;


import java.util.Random;

import com.matrix.matrixgame.board.Board;
import com.matrix.matrixgame.board.Position;

public class RandomMovementStrategy implements MovementStrategy {

    private final Random random = new Random();

    @Override
    public Position nextMove(Position currentPosition, Board board) {
        int[][] moves = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        int[] move = moves[random.nextInt(moves.length)];

        int row = Math.max(0, Math.min(board.getSize() - 1, currentPosition.row() + move[0]));

        int col = Math.max(0, Math.min(board.getSize() - 1, currentPosition.col() + move[1]));

        return new Position(row, col);
    }

}
