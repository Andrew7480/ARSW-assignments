package com.matrix.matrixgame.factory;

import com.matrix.matrixgame.board.Position;
import com.matrix.matrixgame.entity.Agent;
import com.matrix.matrixgame.entity.Neo;
import com.matrix.matrixgame.entity.Phone;
import com.matrix.matrixgame.entity.Wall;
import com.matrix.matrixgame.strategy.RandomMovementStrategy;

public class EntityFactory {

    public static Neo createNeo(Position position) {
        return new Neo(position, new RandomMovementStrategy());
    }

    public static Agent createAgent(Position position) {
        return new Agent(position, new RandomMovementStrategy());
    }

    public static Phone createPhone(Position position) {
        return new Phone(position);
    }

    public static Wall createWall(Position position) {
        return new Wall(position);
    }
}