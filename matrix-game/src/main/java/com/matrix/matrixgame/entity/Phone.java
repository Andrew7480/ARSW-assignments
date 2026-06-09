package com.matrix.matrixgame.entity;

import com.matrix.matrixgame.board.Position;

public class Phone extends Entity {

    public Phone(Position position) {
        super(position, "T");
    }

    public Position getPosition() {
        return position;
    }
}