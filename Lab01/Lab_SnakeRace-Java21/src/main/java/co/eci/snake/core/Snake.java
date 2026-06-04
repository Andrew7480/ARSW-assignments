package co.eci.snake.core;

import java.util.ArrayDeque;
import java.util.Deque;

public final class Snake {
  private final Deque<Position> body = new ArrayDeque<>();
  private volatile Direction direction;
  private int maxLength = 5;
  private volatile boolean alive = true;

  private final int deathOrder;

  private Snake(Position start, Direction dir, int deathOrder) {
    body.addFirst(start);
    this.direction = dir;
    this.deathOrder = deathOrder;
  }

  public static Snake of(int x, int y, Direction dir, int deathOrder) {
    return new Snake(new Position(x, y), dir, deathOrder);
  }

  public Direction direction() { return direction; }

  public int deathOrder() { return deathOrder; }

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

  public synchronized void die() {
    alive = false;
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
