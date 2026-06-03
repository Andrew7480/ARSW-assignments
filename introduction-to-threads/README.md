# Introducción a Hilos y Goroutines

Este ejercicio tiene como objetivo comparar el comportamiento de la concurrencia en **Java** y **Go** mediante una prueba sencilla de rendimiento. Para ello, se ejecuta una operación repetitiva con **5.000.000 de iteraciones**, variando la cantidad de hilos (threads) en Java y goroutines en Go, y registrando el tiempo total de ejecución.

La idea no es encontrar el lenguaje "más rápido", sino observar cómo cada uno administra la concurrencia y qué impacto tiene aumentar la cantidad de tareas ejecutándose al mismo tiempo.

## Experimento

* **Carga de trabajo:** 5.000.000 de iteraciones (valor configurable en el código fuente).
* **Métrica evaluada:** tiempo total de ejecución en milisegundos (ms).
* **Configuraciones probadas:** 1, 2, 100, 400, 800 y 1000 hilos/goroutines.

## Resultados

### Java

| Hilos | Tiempo                    |
| ----- | ------------------------- |
| 1     | 119,408.87 ms (1m 59.41s) |
| 2     | 151,904.65 ms (2m 31.90s) |
| 100   | 116,537.11 ms (1m 56.54s) |
| 400   | 113,182.89 ms (1m 53.18s) |
| 800   | 123,897.40 ms (2m 03.90s) |
| 1000  | 111,490.43 ms (1m 51.49s) |

### Go

| Goroutines | Tiempo                 |
| ---------- | ---------------------- |
| 1          | 99,877 ms (1m 39.88s)  |
| 2          | 130,434 ms (2m 10.43s) |
| 100        | 119,721 ms (1m 59.72s) |
| 400        | 118,108 ms (1m 58.11s) |
| 800        | 119,627 ms (1m 59.63s) |
| 1000       | 118,924 ms (1m 58.92s) |

> Estos resultados corresponden a una ejecución específica del experimento. Los tiempos pueden variar dependiendo del hardware, la carga del sistema operativo y otros procesos que estén ejecutándose en segundo plano.

## Cómo ejecutar

### Java

```bash
javac HiloContador.java
java HiloContador
```

### Go

```bash
go run main.go
```

## Análisis de resultados

A primera vista se puede observar que **Go obtuvo mejores tiempos en la mayoría de las pruebas**, especialmente cuando se ejecutó con una cantidad reducida de goroutines.

Un aspecto interesante es que **aumentar el número de hilos o goroutines no produjo una mejora proporcional en el rendimiento**. De hecho, en varios casos los tiempos aumentaron. Esto ocurre porque el problema evaluado es principalmente **CPU-bound**, es decir, depende más de la capacidad de procesamiento que de operaciones de entrada y salida.

Cuando se crean demasiados hilos o tareas concurrentes, el sistema debe dedicar más tiempo a coordinarlas, programarlas y alternar entre ellas. Ese trabajo adicional genera una sobrecarga que puede terminar anulando los beneficios de la concurrencia.

En el caso de Go, las goroutines son muy ligeras y son administradas por el runtime del lenguaje, lo que reduce significativamente el costo de creación y planificación. Java también cuenta con mecanismos eficientes para la concurrencia, pero los threads tradicionales suelen requerir más recursos y tienen un costo mayor de administración.

## Conclusión

Los resultados muestran que la concurrencia no siempre implica una ejecución más rápida. La cantidad óptima de hilos depende del tipo de tarea, de los recursos disponibles y de cómo cada lenguaje implementa su modelo de ejecución.

En este experimento, **Go presentó tiempos ligeramente mejores que Java**, lo que puede atribuirse al bajo costo de las goroutines y a la eficiencia de su runtime para gestionar grandes cantidades de tareas concurrentes. Sin embargo, ambos lenguajes evidencian que agregar más hilos de forma indiscriminada no garantiza una mejora de rendimiento y, en algunos casos, incluso puede empeorar los resultados.
