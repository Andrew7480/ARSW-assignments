package com.matrix.matrixgame.engine;

import com.matrix.matrixgame.board.Board;

public interface BoardObserver {
    void onRoundEnd(Board board, GameState state);
}
