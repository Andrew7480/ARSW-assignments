package com.matrix.matrixgame.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;

import com.matrix.matrixgame.board.Board;
import com.matrix.matrixgame.board.BoardPrinter;
import com.matrix.matrixgame.board.Position;
import com.matrix.matrixgame.entity.Agent;
import com.matrix.matrixgame.entity.Phone;
import com.matrix.matrixgame.entity.Wall;
import com.matrix.matrixgame.factory.EntityFactory;
import com.matrix.matrixgame.worker.AgentWorker;
import com.matrix.matrixgame.worker.NeoWorker;
import com.matrix.matrixgame.worker.RoundCoordinator;

public class GameEngine {

    private final GameConfig config;

    private final Board board;

    private volatile GameState gameState = GameState.RUNNING;

    private final List<BoardObserver> observers = new CopyOnWriteArrayList<>();

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

    public void addObserver(BoardObserver observer) {
        observers.add(observer);
    }

    public synchronized void notifyObservers() {
        GameState state = gameState;
        observers.forEach(o -> o.onRoundEnd(board, state));
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

    public void startGame(RoundGate gate) {
        BoardPrinter.print(board);

        int parties = board.getAgents().size() + 2; // Neo + agents + coordinator
        CyclicBarrier barrier = new CyclicBarrier(parties);

        NeoWorker neoWorker = new NeoWorker(board.getNeo(), board, this, barrier);

        List<AgentWorker> agentWorkers = new ArrayList<>();
        for (Agent agent : board.getAgents()) {
            agentWorkers.add(new AgentWorker(agent, board, this, barrier));
        }

        RoundCoordinator coordinator = new RoundCoordinator(this, board, barrier, gate);

        neoWorker.start();
        agentWorkers.forEach(Thread::start);
        coordinator.start();

        try {
            neoWorker.join();
            for (AgentWorker w : agentWorkers) w.join();
            coordinator.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public synchronized boolean isRunning() {
        return gameState == GameState.RUNNING;
    }

    public synchronized void evaluateState() {
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
