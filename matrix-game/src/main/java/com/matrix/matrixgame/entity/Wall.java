package com.matrix.matrixgame.entity;

import com.matrix.matrixgame.board.Position;

public class Wall extends Entity {

    public Wall(Position position) {
        super(position, "W");
    }

    public Position getPosition() {
        return position;
    }
}