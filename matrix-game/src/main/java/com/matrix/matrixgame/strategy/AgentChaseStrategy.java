package com.matrix.matrixgame.strategy;

import java.util.Collections;
import java.util.List;

import com.matrix.matrixgame.board.Board;
import com.matrix.matrixgame.board.Position;

public class AgentChaseStrategy extends BfsMovementStrategy {

    @Override
    protected List<Position> getTargets(Board board) {
        if (board.getNeo() == null) return Collections.emptyList();
        return List.of(board.getNeo().getPosition());
    }
}
