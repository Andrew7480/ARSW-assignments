package com.matrix.matrixgame.strategy;

import java.util.List;
import java.util.stream.Collectors;

import com.matrix.matrixgame.board.Board;
import com.matrix.matrixgame.board.Position;

public class NeoPathStrategy extends BfsMovementStrategy {

    @Override
    protected List<Position> getTargets(Board board) {
        return board.getPhones().stream()
                .map(phone -> phone.getPosition())
                .collect(Collectors.toList());
    }
}
