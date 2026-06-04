package co.eci.snake.core.engine;

public final class PauseController {
  private final Object monitor = new Object();
  private final int workerCount;
  private boolean paused = false;
  private int waitingWorkers = 0;
  private int activeWorkers = 0;

  public PauseController(int workerCount) {
    if (workerCount <= 0) {
      throw new IllegalArgumentException("workerCount must be > 0");
    }
    this.workerCount = workerCount;
  }

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

  public void workerStarted() {
    synchronized (monitor) {
      activeWorkers++;
      monitor.notifyAll();
    }
  }

  public void workerFinished() {
    synchronized (monitor) {
      if (activeWorkers > 0) {
        activeWorkers--;
      }
      monitor.notifyAll();
    }
  }

  public void waitIfPaused() throws InterruptedException {
    synchronized (monitor) {
      if (!paused) {
        return;
      }
      waitingWorkers++;
      monitor.notifyAll();
      while (paused) {
        try {
          monitor.wait();
        } catch (InterruptedException ie) {
          waitingWorkers--;
          monitor.notifyAll();
          throw ie;
        }
      }
      waitingWorkers--;
      monitor.notifyAll();
    }
  }

  public boolean awaitAllWorkersPaused() throws InterruptedException {
    synchronized (monitor) {
      while (paused && waitingWorkers < activeWorkers) {
        monitor.wait();
      }
      return paused && waitingWorkers >= activeWorkers;
    }
  }

  public boolean isPaused() {
    synchronized (monitor) {
      return paused;
    }
  }
}
