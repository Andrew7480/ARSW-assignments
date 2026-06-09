# Synchronization Workshop — Barrier Synchronization Pattern

**Institution:** Escuela Colombiana de Ingeniería  
**Course:** Software Architectures  
**Topic:** Barrier Synchronization Pattern

---

## Objective

Implement a barrier synchronization strategy to coordinate the execution of multiple threads, ensuring that the average execution time is calculated only after all threads have completed their work.

---

## Instructions

### 1. Import the project

Download and import the project:

* `BarrierSyncProblem.zip`

---

### 2. Analyze the initial behavior

Review the main program.

The example uses **N threads** that all execute the same task, but each at a different speed. The goal is to run all threads and, once they finish, compute the average of their execution times.

#### Activity

1. Run the program.
2. Observe the message:

```text
El tiempo promedio de la ejecución fue de ...
```

3. Answer the following questions:

* What result was obtained?
* Is the displayed value correct?
* Why does this result occur?

When the program is run, the threads begin executing their tasks and periodically print their progress percentage. However, the average time message shows **0**. It appears immediately after the threads are started, before they have finished their work.

An incorrect average execution time of 0 was obtained. This value does not represent the actual execution time of the threads, since they continue working after the average has already been computed and printed.

This happens because the main thread creates and starts the worker threads via `start()`, but does not wait for them to finish before computing the average. As a result, when the result section executes, most threads have not yet completed their `run()` method, so the `result` attribute still holds its initial value (0). This causes the average to be computed using incomplete data, producing an incorrect result.

![alt text](image.png)

---

### 3. Implement barrier synchronization

Apply a barrier synchronization strategy that ensures the average execution time is computed only after the last thread has completed its work.

#### Requirements

* The main program must remain blocked while the threads are running.
* The main program must continue only when all threads have finished.
* The average must be computed after the last thread completes its task.

A barrier was implemented using `CountDownLatch`. The main thread blocks via `await()` while the worker threads execute. Each thread decrements the barrier counter via `countDown()` when it finishes. When the last thread completes, the counter reaches zero and the main thread resumes to compute the average execution time.

---

### 4. Verification

Run the program again and verify that:

* All threads finish before the average is computed.
* The main program correctly waits for all threads to complete.
* The computed average reflects the actual execution times recorded by each thread.

![alt text](image.png)

![alt text](image-1.png)

---

## Expected Result

After implementing barrier synchronization:

* There must be no premature average computation.
* The main thread must wait for all worker threads to finish.
* The displayed average must correctly reflect the execution times recorded by each thread.
