# Matrix Game

## Descripción

Matrix Game es una simulación concurrente inspirada en la película Matrix. El juego se desarrolla sobre un tablero de tamaño `n x n`, donde Neo debe encontrar un teléfono para escapar mientras evita ser capturado por los agentes.

Cada entidad del juego se ejecuta en su propio hilo de ejecución. Durante cada ronda, todos los personajes realizan sus movimientos de forma concurrente, mientras una barrera de sincronización garantiza que ningún hilo avance a la siguiente ronda hasta que todos hayan completado la actual.

La aplicación se desarrolla utilizando **Java 21, Maven y Spring Boot**, proporcionando una interfaz gráfica que permite visualizar en tiempo real el estado del tablero, los movimientos de los personajes y el avance de la simulación.

Este proyecto tiene como objetivo aplicar conceptos de concurrencia, sincronización mediante barreras, programación orientada a objetos y desarrollo de aplicaciones con interfaz visual.