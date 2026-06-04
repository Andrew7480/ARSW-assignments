package co.eci.snake.core.engine;

public final class PauseController {
  private final Object monitor = new Object();
  private boolean paused = false;
  private int activeWorkers = 0;
  private int workersInFlight = 0;
  
  public void pause() {
    synchronized (monitor) {
      paused = true;
      monitor.notifyAll();
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

  public void beginWork() {
    synchronized (monitor) {
      workersInFlight++;
      monitor.notifyAll();
    }
  }

  public void endWork() {
    synchronized (monitor) {
      if (workersInFlight > 0) {
        workersInFlight--;
      }
      monitor.notifyAll();
    }
  }

  public void registerWorker() {
    synchronized (monitor) {
      activeWorkers++;
      monitor.notifyAll();
    }
  }

  public void unregisterWorker() {
    synchronized (monitor) {
      if (activeWorkers > 0) {
        activeWorkers--;
      }
      monitor.notifyAll();
    }
  }

  public void awaitStablePause() throws InterruptedException {
    synchronized (monitor) {
      while (paused && workersInFlight > 0) {
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
