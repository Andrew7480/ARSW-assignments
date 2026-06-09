# Matrix Game

Simulación concurrente por turnos inspirada en la película Matrix. Neo intenta llegar a un teléfono para escapar; los agentes intentan capturarlo. Cada entidad se ejecuta en su propio hilo y todos se sincronizan por ronda mediante una `CyclicBarrier`.

## Tecnologías

- Java 21
- Maven
- Spring Boot 3.5.3

## Cómo ejecutar

```bash
mvn spring-boot:run
```

El tablero se imprime en consola al inicio de cada ronda. Presiona **ENTER** para avanzar a la siguiente.

## Tablero

```
. . . . T . . . . .
. # . . . . # . . .
. . . . . A . . . .
. . # . N . . . . .
. . . . . . . A . .
```

| Símbolo | Entidad   |
|---------|-----------|
| `N`     | Neo       |
| `A`     | Agente    |
| `T`     | Teléfono  |
| `#`     | Muro      |
| `.`     | Celda libre |

## Arquitectura concurrente

Cada ronda se divide en dos fases separadas por barreras:

```
NeoWorker      ──┐
AgentWorker 1  ──┤── barrier 1 (todos calcularon)
AgentWorker 2  ──┤                    │
AgentWorker 3  ──┘            RoundCoordinator
                                aplica movimientos
                                evalúa estado
                                imprime tablero
                                espera ENTER
NeoWorker      ──┐                    │
AgentWorker 1  ──┤── barrier 2 (todos pueden avanzar)
AgentWorker 2  ──┤
AgentWorker 3  ──┘
```

- **Fase 1:** cada worker calcula su próxima posición (`nextPosition`) pero no modifica el tablero.
- **Fase 2:** el `RoundCoordinator` aplica todos los movimientos, evalúa si el juego terminó e imprime el tablero.
- La barrera tiene `agentCount + 2` participantes (Neo + agentes + coordinador).

## Estrategias de movimiento

| Entidad | Estrategia           | Descripción                                        |
|---------|----------------------|----------------------------------------------------|
| Neo     | `NeoPathStrategy`    | BFS hacia el teléfono más cercano                  |
| Agentes | `AgentChaseStrategy` | BFS hacia la posición actual de Neo                |

Ambas estrategias extienden `BfsMovementStrategy`, que calcula el primer paso del camino más corto respetando muros y límites del tablero.

> Los agentes no pueden pararse sobre teléfonos — solo Neo puede ocupar esas celdas.

## Condiciones de victoria

- **Neo escapa:** Neo llega a la posición de un teléfono.
- **Agentes capturan:** un agente llega a la misma posición que Neo.

## Configuración

En `MatrixGameApplication.java`:

```java
GameConfig config = new GameConfig(
    10,  // tamaño del tablero (n x n)
    15,  // cantidad de muros
    3,   // cantidad de agentes
    2    // cantidad de teléfonos
);
```