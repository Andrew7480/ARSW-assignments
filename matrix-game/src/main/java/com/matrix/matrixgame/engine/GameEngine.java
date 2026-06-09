package com.matrix.matrixgame.engine;

import com.matrix.matrixgame.board.Board;
import com.matrix.matrixgame.board.BoardPrinter;
import com.matrix.matrixgame.board.Position;
import com.matrix.matrixgame.entity.Agent;
import com.matrix.matrixgame.entity.Phone;
import com.matrix.matrixgame.entity.Wall;
import com.matrix.matrixgame.factory.EntityFactory;

public class GameEngine {

    private final GameConfig config;

    private final Board board;

    private volatile GameState gameState = GameState.RUNNING;

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

        int createdWalls = 0;
        while (createdWalls < config.wallCount()) {

            Wall wall = EntityFactory.createWall(board.randomEmptyPosition());
            board.getWalls().add(wall);
            if (board.pathExists()) {
                createdWalls++;
            } else {
                board.getWalls().remove(wall);
            }
        }
    }

    public void startGame() {
        BoardPrinter.print(board);
        
        while (gameState == GameState.RUNNING) {

            board.moveNeo();

            for (Agent agent : board.getAgents()) {
                board.moveAgent(agent);
            }

            evaluateGameState();

            BoardPrinter.print(board);
        }
        if (gameState == GameState.NEO_WON) {
            System.out.println("Neo escaped through a phone.");
        }

        if (gameState == GameState.AGENTS_WON) {
            System.out.println("Agents captured Neo.");
        }
    }

    private void evaluateGameState() {

        Position neoPosition = board.getNeo().getPosition();

        boolean reachedPhone = board.getPhones()
                .stream()
                .anyMatch(phone -> phone.getPosition()
                        .equals(neoPosition));

        if (reachedPhone) {
            gameState = GameState.NEO_WON;
            return;
        }

        boolean captured = board.getAgents()
                .stream()
                .anyMatch(agent -> agent.getPosition()
                        .equals(neoPosition));

        if (captured) {
            gameState = GameState.AGENTS_WON;
        }
    }

    public GameState getState() {
        return gameState;
    }

    public void setState(GameState gameState) {
        this.gameState = gameState;
    }
}