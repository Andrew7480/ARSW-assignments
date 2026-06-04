package co.eci.snake.concurrency;

import co.eci.snake.core.Board;
import co.eci.snake.core.Direction;
import co.eci.snake.core.Snake;
import co.eci.snake.core.engine.PauseController;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public final class SnakeRunner implements Runnable {
  private final Snake snake;
  private final Board board;
  private final int baseSleepMs = 80;
  private final int turboSleepMs = 40;
  private int turboTicks = 0;

  private final PauseController pauseController;
  private final AtomicInteger deathCounter;

  public SnakeRunner(Snake snake, Board board, PauseController pauseController, AtomicInteger deathCounter) {
    this.snake = snake;
    this.board = board;
    this.pauseController = pauseController;
    this.deathCounter = deathCounter;
  }

  @Override
  public void run() {
    pauseController.workerStarted();
    try {
      while (!Thread.currentThread().isInterrupted()) {
        if (!snake.isAlive()) {
          break;
        }

        try { 
          pauseController.waitIfPaused();
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          break;
        }

        maybeTurn();
        var res = board.step(snake, deathCounter);
        if (res == Board.MoveResult.HIT_OBSTACLE) {
          randomTurn();
        } else if (res == Board.MoveResult.ATE_TURBO) {
          turboTicks = 100;
        } else if (res == Board.MoveResult.DIED) {
          break;
        }
        int sleep = (turboTicks > 0) ? turboSleepMs : baseSleepMs;
        if (turboTicks > 0) turboTicks--;
        Thread.sleep(sleep);
      }
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
    } finally {
      pauseController.workerFinished();
    }
  }

  private void maybeTurn() {
    double p = (turboTicks > 0) ? 0.05 : 0.10;
    if (ThreadLocalRandom.current().nextDouble() < p) randomTurn();
  }

  private void randomTurn() {
    var dirs = Direction.values();
    snake.turn(dirs[ThreadLocalRandom.current().nextInt(dirs.length)]);
  }
}
