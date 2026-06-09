package com.matrix.matrixgame.engine;

import java.util.concurrent.Semaphore;

public class RoundGate {

    private final Semaphore semaphore = new Semaphore(0);

    public void waitForNext() throws InterruptedException {
        semaphore.acquire();
    }

    public void releaseNext() {
        semaphore.release();
    }
}
