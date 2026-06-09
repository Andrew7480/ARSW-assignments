package com.matrix.matrixgame.worker;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import com.matrix.matrixgame.board.Board;
import com.matrix.matrixgame.board.BoardPrinter;
import com.matrix.matrixgame.engine.GameEngine;
import com.matrix.matrixgame.engine.GameState;
import com.matrix.matrixgame.engine.RoundGate;

public class RoundCoordinator extends Thread {

    private final GameEngine engine;
    private final Board board;
    private final CyclicBarrier barrier;
    private final RoundGate gate;

    public RoundCoordinator(GameEngine engine, Board board, CyclicBarrier barrier, RoundGate gate) {
        this.engine = engine;
        this.board = board;
        this.barrier = barrier;
        this.gate = gate;
    }

    @Override
    public void run() {
        try {
            while (engine.isRunning()) {
                barrier.await(); // phase 1: wait for all workers to calculate

                board.applyNeoMove();
                for (var agent : board.getAgents()) {
                    board.applyAgentMove(agent);
                }

                engine.evaluateState();
                BoardPrinter.print(board);
                engine.notifyObservers(); // update GUI

                if (engine.isRunning()) {
                    gate.waitForNext(); // wait for button/ENTER in GUI
                }

                barrier.await(); // phase 2: workers check state and proceed or exit
            }
        } catch (InterruptedException | BrokenBarrierException e) {
            Thread.currentThread().interrupt();
        }

        printResult();
    }

    private void printResult() {
        GameState state = engine.getState();
        if (state == GameState.NEO_WON) {
            System.out.println("Neo escapó por un teléfono.");
        } else if (state == GameState.AGENTS_WON) {
            System.out.println("Los agentes capturaron a Neo.");
        }
    }
}
