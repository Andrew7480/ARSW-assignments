package com.matrix.matrixgame.engine;

import com.matrix.matrixgame.board.Board;
import com.matrix.matrixgame.entity.Agent;
import com.matrix.matrixgame.entity.Phone;
import com.matrix.matrixgame.entity.Wall;
import com.matrix.matrixgame.factory.EntityFactory;

public class GameEngine {

    private final GameConfig config;

    private final Board board;

    public GameEngine(GameConfig config) {
        this.config = config;

        this.board = new Board(config.boardSize());
    }

    public Board getBoard() {
        return board;
    }

    public GameConfig getConfig() {
        return config;
    }

    public void initializeGame() {

        board.setNeo(EntityFactory.createNeo(board.randomEmptyPosition()));

        for (int i = 0; i < config.phoneCount(); i++) {
            Phone phone = EntityFactory.createPhone(board.randomEmptyPosition());
            board.getPhones().add(phone);
        }

        for (int i = 0; i < config.agentCount(); i++) {
            Agent agent = EntityFactory.createAgent(board.randomEmptyPosition());
            board.getAgents().add(agent);
        }

        for (int i = 0; i < config.wallCount(); i++) {
            Wall wall = EntityFactory.createWall(board.randomEmptyPosition());
            board.getWalls().add(wall);
        }
    }
}