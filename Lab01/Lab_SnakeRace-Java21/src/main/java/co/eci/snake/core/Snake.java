package co.eci.snake.core;

import java.util.ArrayDeque;
import java.util.Deque;

public final class Snake {
  private final Deque<Position> body = new ArrayDeque<>();
  private volatile Direction direction;
  private int maxLength = 5;
  private volatile boolean alive = true;

  private final int snakeNumber;
  private int deathRank = 0;

  private Snake(Position start, Direction dir, int snakeNumber) {
    body.addFirst(start);
    this.direction = dir;
    this.snakeNumber = snakeNumber;
  }

  public static Snake of(int x, int y, Direction dir, int snakeNumber) {
    return new Snake(new Position(x, y), dir, snakeNumber);
  }

  public Direction direction() { return direction; }

  public int snakeNumber() { return snakeNumber; }

  public synchronized void turn(Direction dir) {
    if ((direction == Direction.UP && dir == Direction.DOWN) ||
        (direction == Direction.DOWN && dir == Direction.UP) ||
        (direction == Direction.LEFT && dir == Direction.RIGHT) ||
        (direction == Direction.RIGHT && dir == Direction.LEFT)) {
      return;
    }
    this.direction = dir;
  }

  public synchronized Position head() { return body.peekFirst(); }

  public synchronized Deque<Position> snapshot() { return new ArrayDeque<>(body); }

  public synchronized boolean isAlive() { return alive; }

  public synchronized int deathRank() { return deathRank; }

  public synchronized void die(int deathRank) {
    if (!alive) {
      return;
    }
    alive = false;
    this.deathRank = deathRank;
    body.clear();
  }

  public synchronized void advance(Position newHead, boolean grow) {
    if (!alive) {
      return;
    }
    body.addFirst(newHead);
    if (grow) maxLength++;
    while (body.size() > maxLength) body.removeLast();
  }
}
