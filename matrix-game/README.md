# Matrix Game

A turn-based concurrent simulation inspired by the Matrix movie. Neo tries to reach a phone to escape; agents try to capture him. Each entity runs in its own thread and all of them synchronize per round via a `CyclicBarrier`.

## Technologies

- Java 21
- Maven
- Spring Boot 3.5.3

## How to run

```bash
mvn spring-boot:run
```

The board is printed to the console at the start of each round. Press **ENTER** to advance to the next one.

## Board

```
. . . . T . . . . .
. # . . . . # . . .
. . . . . A . . . .
. . # . N . . . . .
. . . . . . . A . .
```

| Symbol | Entity      |
|--------|-------------|
| `N`    | Neo         |
| `A`    | Agent       |
| `T`    | Telephone   |
| `#`    | Wall        |
| `.`    | Empty cell  |

## Concurrent Architecture

Each round is divided into two phases separated by barriers:

```
NeoWorker      ──┐
AgentWorker 1  ──┤── barrier 1 (all computed next move)
AgentWorker 2  ──┤                    │
AgentWorker 3  ──┘            RoundCoordinator
                                applies movements
                                evaluates game state
                                prints board
                                waits for ENTER
NeoWorker      ──┐                    │
AgentWorker 1  ──┤── barrier 2 (all may proceed)
AgentWorker 2  ──┤
AgentWorker 3  ──┘
```

- **Phase 1:** each worker computes its next position (`nextPosition`) without modifying the board.
- **Phase 2:** the `RoundCoordinator` applies all movements, evaluates whether the game has ended, and prints the board.
- The barrier has `agentCount + 2` participants (Neo + agents + coordinator).

## Movement Strategies

| Entity | Strategy             | Description                                          |
|--------|----------------------|------------------------------------------------------|
| Neo    | `NeoPathStrategy`    | BFS toward the nearest telephone                     |
| Agents | `AgentChaseStrategy` | BFS toward Neo's current position                    |

Both strategies extend `BfsMovementStrategy`, which computes the first step of the shortest path while respecting walls and board boundaries.

> Agents cannot stand on telephone cells — only Neo can occupy them.

## Win Conditions

- **Neo escapes:** Neo reaches a telephone cell.
- **Agents capture:** an agent reaches the same position as Neo.

## Configuration

In `MatrixGameApplication.java`:

```java
GameConfig config = new GameConfig(
    10,  // board size (n x n)
    15,  // number of walls
    3,   // number of agents
    2    // number of telephones
);
```
