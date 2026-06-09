# Snake Race — ARSW Lab #2 (Java 21, Virtual Threads)

**Escuela Colombiana de Ingeniería – Software Architectures**  
Concurrent programming lab: race conditions, synchronization, and thread-safe collections.

Lab repository: https://github.com/DECSIS-ECI/Lab_SnakeRace-Java21

---

## Requirements

- **JDK 21** (Temurin recommended)
- **Maven 3.9+**
- OS: Windows, macOS or Linux

---

## How to run

```bash
mvn clean verify
mvn -q -DskipTests exec:java -Dsnakes=4
```

- `-Dsnakes=N` → starts the game with **N** snakes (default: 2).
- **Controls**:
  - **Arrow keys**: snake **0** (Player 1).
  - **WASD**: snake **1** (if it exists).
  - **Space** or **Action** button: Pause / Resume.

---

## Game Rules (summary)

- **N snakes** run autonomously (each in its own thread).
- **Mice**: eating one makes the snake **grow** and spawns a **new obstacle**.
- **Obstacles**: if the head enters an obstacle, the snake **bounces**.
- **Teleporters** (red arrows): entering one takes you **out through its pair**.
- **Lightning bolts (Turbo)**: stepping on one grants the snake a temporary **speed boost**.
- Movement uses **wrap-around** (the board "wraps" at the edges).

---

## Architecture (folders)

```
co.eci.snake
├─ app/                 # Application bootstrap (Main)
├─ core/                # Domain: Board, Snake, Direction, Position
├─ core/engine/         # GameClock (ticks, Pause/Resume)
├─ concurrency/         # SnakeRunner (per-snake logic with virtual threads)
└─ ui/legacy/           # Legacy-style Swing UI with grid and Action button
```

---

# Lab Activities

## Part I — (Warm-up) `wait/notify` in a multi-threaded program

We modified `PrimeFinder` so that every `t` milliseconds all worker threads stop, the number of primes found is displayed, and the program waits for ENTER to resume. The solution uses Java's monitor model without busy-waiting.

Coordination is concentrated in `Control` and a single shared monitor. Workers check a pause condition before continuing processing, while the main thread activates and deactivates that pause at regular intervals.

### Synchronization Design

- **Shared monitor**: the `monitor` object in `Control` is the only lock used to coordinate pausing and resuming.
- **Shared state**: the `paused` variable represents the workers' wait condition.
- **Consistent reads**: `isPaused()` synchronizes on `monitor`, so it reads `paused` under the same lock used by `wait()` and `notifyAll()`.
- **No busy-waiting**: workers execute `while (control.isPaused()) { monitor.wait(); }`, releasing the monitor while they wait.
- **Safe resume**: the control thread sets `paused = false` inside `synchronized(monitor)` and then calls `monitor.notifyAll()` to wake all threads.

### Execution Flow

1. `Control` starts the worker threads.
2. Every `TMILISECONDS`, `Control` activates the pause by setting `paused = true`.
3. The total number of primes found is printed.
4. The program waits for user input via `Scanner.nextLine()`.
5. When ENTER is pressed, `Control` deactivates the pause and calls `notifyAll()`.
6. Workers resume execution from the point where they were suspended.

### How Synchronization Errors Are Avoided

- **Lost wakeups**: a `while` loop wraps `wait()` to re-validate the condition after each wakeup.
- **State inconsistency**: reads and writes to `paused` are done under the same monitor.
- **Busy-waiting**: no polling is done; threads block inside `wait()` until they are woken up.

Using a single shared lock and an explicit condition allows threads to be coordinated safely, clearly, and without unnecessary CPU consumption.

---

## Part II — Concurrent SnakeRace (core of the lab)

### 1) Concurrency Analysis

The code gives each snake autonomy via a dedicated `SnakeRunner`. Each runner runs its own cycle: decides turns, advances, reacts to the movement result, and sleeps briefly before the next step. In other words, each snake "thinks" and moves on its own; there is no central thread that moves all of them.

The system has two main concurrent paths: the snake threads, which update the game model, and the UI, which repaints the board and queries state to display it. `GameClock` controls repaints and the visual state of the interface, but it is not the one that moves the snakes. This is why pausing the UI does not automatically stop the `SnakeRunner` threads.

### Possible Race Conditions

- `Board` is shared by all `SnakeRunner` instances. Without synchronization, two threads could simultaneously try to modify mice, obstacles, teleports, or turbo cells — for example, if two snakes eat the same mouse at the same time, both try to add a new obstacle and a new mouse, corrupting these collections. Similarly, `randomEmpty()` is not protected and can be accessed by multiple threads.

  This is why `Board.step(...)` is synchronized: it protects a snake's advance over the board and prevents two runners from altering the same state at the same time.

- `snapshot()` is used for UI reads, allowing the body to be read for rendering, but the risk arises if the UI reads while another thread is modifying the snake.
- The methods `head()` and `advance()` operate on the same internal state of the snake. If called from multiple threads without coordination, they could produce inconsistent reads or lost updates.
- The method `turn()` modifies the snake's direction while `SnakeRunner` reads it on each step.

### Unsafe Collections in a Concurrent Context

- `ArrayDeque` in `Snake.body`.
- `HashSet` in `Board.mice`, `Board.obstacles`, and `Board.turbo`.
- `HashMap` in `Board.teleports`.

These collections are not thread-safe but are partially protected by external synchronization.

### Busy-Waiting or Unnecessary Synchronization

There is no strong classic busy-wait because `SnakeRunner` does not enter a constant polling loop: after moving a snake it calls `Thread.sleep(...)` and does not waste CPU. There can also be unnecessary blocking since `step` is synchronized, which locks the entire board for all other snakes.

Additionally, execution control is decoupled between the UI and the runners. `GameClock.pause()` only changes the visual state of the interface, but the `SnakeRunner` threads keep running. This explains why pausing the UI can let the internal simulation keep advancing, making snakes appear to teleport.

### 2) Minimal Fixes and Critical Sections

- **Eliminate** busy-waiting by replacing it with **signals** / **states** or concurrency library mechanisms.

To eliminate busy-waiting, we use a `PauseController` that manages a shared pause/resume state. The solution uses Java's monitor model (`synchronized`, `wait()`, and `notifyAll()`) to block and resume `SnakeRunner` threads without active polling. This avoids unnecessary CPU consumption and coordinates suspension and resumption safely.

- Protect **only** the **strictly necessary critical sections** (avoid broad locks).

Only sections that modify shared state are synchronized; local variables and computations that do not share memory are left unprotected. Access to `Board` must be atomic because it is a globally shared resource, while the UI must not lock the entire game and therefore reads defensive copies of the board.

The same logic applies to `Snake.snapshot()`: the UI works on a copy of the body for rendering, but for full read-write consistency that copy should also be coordinated with additional synchronization.

For this reason `step(...)` must be `synchronized`, since that is where shared board state is read and modified. In contrast, `randomEmpty()` does not need additional synchronization in this design because it is only called from `step(...)` and from the constructor — in both cases it is already covered: `step(...)` holds the `Board` lock, and the constructor runs before the object is shared with other threads.

The method `createTeleportPairs(...)` is also used only during board construction, so it does not need an additional lock today either. If in the future it is called from other threads or outside the constructor, its synchronization would need to be revisited.

Each change is justified by identifying the risk and the applied solution: if a critical section shared mutable state it could produce inconsistencies or races; therefore only access that actually modifies shared state was protected, and local variables and non-shared computations were left outside.

### 3) Safe Execution Control (UI)

The complete execution cycle with **Start / Pause / Resume** was implemented using the **Action** button, `GameClock`, and a `PauseController` shared by all `SnakeRunner` instances.

![summary](docs/board.png)

When the user presses **Pause**, the UI does not compute the summary immediately. It first changes the state to _"Waiting for a stable pause state..."_ and then waits for a **stable pause**: this means all active runners have reached their wait point (`wait`) and none is currently applying a `step(...)` to the board.

![summary](docs/waiting.png)

Only when that condition is met does the UI build and publish the summary:

- **Longest living snake**: the snake with `isAlive() == true` and the greatest length (`snapshot().size()`).
- **Worst snake**: the one that died first, using an incremental death order assigned in `Board.killSnake(...)`.

![summary](docs/resume1.png)
![summary](docs/resume2.png)

This strategy avoids state tearing when pausing, because the summary is never computed in the middle of a partial advance.

### 4) Robustness Under Load

When running the game with a high number of snakes (e.g., N = 20), no visible race conditions or stability issues occur. This is because all modifications to shared board state are made inside synchronized critical sections, preventing unsafe concurrent access to the shared collections.

![summary](docs/board20.png)

![summary](docs/running20.png)

---

## Deliverables

1. **Source code** running on **Java 21**.
2. Everything clearly documented in the **lab report** with:
   - Data races found and their solution.
   - Misused collections and how they were protected (or replaced).
   - Busy-waits eliminated and the mechanism used.
   - Critical sections defined and justification of their **minimum scope**.
3. UI with **Start / Pause / Resume** and the requested statistics on pause.

---

## Evaluation Criteria (10 pts)

- (3) **Correct concurrency**: no data races; synchronization well-localized.
- (2) **Pause/Resume**: visual and state consistency.
- (2) **Robustness**: runs **with high N** without concurrency exceptions.
- (1.5) **Quality**: clear structure, names, comments; no obvious _code smells_.
- (1.5) **Documentation**: clear, reproducible **lab report**.

---

## Useful Tips and Configuration

- **Number of snakes**: `-Dsnakes=N` at runtime.
- **Board size**: change the `new Board(width, height)` constructor.
- **Teleports / Turbo**: edit `Board.java` (initialization methods and rules in `step(...)`).
- **Speed**: adjust `GameClock` (tick) or the `sleep` in `SnakeRunner` (includes turbo mode).

---

## How to Run Tests

```bash
mvn clean verify
```

Includes compilation and execution of JUnit tests. If you have static analysis configured, run it in the `verify` or `site` phase according to your `pom.xml`.

---

## Credits

This lab is a modernized adaptation of the **SnakeRace** exercise from ARSW. The activity statements are preserved to maintain the pedagogical goals of the course.

**Base built by Eng. Javier Toquica.**
