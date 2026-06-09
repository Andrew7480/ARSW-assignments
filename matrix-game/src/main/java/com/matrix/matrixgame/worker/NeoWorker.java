package com.matrix.matrixgame.worker;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import com.matrix.matrixgame.board.Board;
import com.matrix.matrixgame.board.Position;
import com.matrix.matrixgame.engine.GameEngine;
import com.matrix.matrixgame.entity.Neo;

public class NeoWorker extends Thread {

    private final Neo neo;
    private final Board board;
    private final GameEngine engine;
    private final CyclicBarrier barrier;

    public NeoWorker(Neo neo, Board board, GameEngine engine, CyclicBarrier barrier) {
        this.neo = neo;
        this.board = board;
        this.engine = engine;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            while (engine.isRunning()) {
                Position next = neo.calculateNextMove(board);
                neo.setNextPosition(next);
                barrier.await(); // phase 1: all calculated
                barrier.await(); // phase 2: coordinator applied moves
            }
        } catch (InterruptedException | BrokenBarrierException e) {
            Thread.currentThread().interrupt();
        }
    }
}
