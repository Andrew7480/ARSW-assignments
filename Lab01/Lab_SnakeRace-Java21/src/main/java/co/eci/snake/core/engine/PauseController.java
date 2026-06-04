package co.eci.snake.core.engine;

public final class PauseController {
  private final Object monitor = new Object();
  private boolean paused = false;
  
  public void pause() {
    synchronized (monitor) {
      paused = true;
    }
  }

  public void resume() {
    synchronized (monitor) {
      paused = false;
      monitor.notifyAll();
    }
  }

  public void waitIfPaused() throws InterruptedException {
    synchronized (monitor) {
      while (paused) {
        monitor.wait();
      }
    }
  }

  public boolean isPaused() {
    synchronized (monitor) {
      return paused;
    }
  }
}
