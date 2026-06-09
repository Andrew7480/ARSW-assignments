package com.matrix.matrixgame.engine;

import com.matrix.matrixgame.board.Position;
import com.matrix.matrixgame.entity.Entity;

public class MoveIntent {

    private final Entity entity;

    private final Position target;

    public MoveIntent(Entity entity, Position target) {
        this.entity = entity;
        this.target = target;
    }

    public Entity getEntity() {
        return entity;
    }

    public Position getTarget() {
        return target;
    }
}