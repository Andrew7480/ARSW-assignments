
## Requisitos

- **JDK 21** (Temurin recomendado)
- **Maven 3.9+**
- SO: Windows, macOS o Linux

---

## Cómo ejecutar

```bash
mvn clean verify
mvn -q -DskipTests exec:java
```


## Parte I — Solución del laboratorio

### Objetivo

Modificar `PrimeFinder` para que, cada `t` milisegundos, todos los hilos trabajadores se detengan, se muestre cuántos números primos se han encontrado y el programa espere ENTER para reanudar. La solución debe usar el modelo de monitores de Java sin espera activa.

### Implementación general

La coordinación se concentra en `Control` y en un único monitor compartido. Los workers verifican una condición de pausa antes de continuar el procesamiento, mientras que el hilo principal activa y desactiva dicha pausa en intervalos regulares.

### Diseño de sincronización

- **Monitor compartido**: el objeto `monitor` de `Control` es el único lock usado para coordinar la pausa y la reanudación.
- **Estado compartido**: la variable `paused` representa la condición de espera de los trabajadores.
- **Lectura consistente**: `isPaused()` sincroniza sobre `monitor`, por lo que lee `paused` con el mismo lock que usan `wait()` y `notifyAll()`.
- **Espera sin busy-waiting**: los workers ejecutan `while (control.isPaused()) { monitor.wait(); }`, liberando el monitor mientras esperan.
- **Reanudación segura**: el hilo de control pone `paused = false` dentro de `synchronized(monitor)` y luego invoca `monitor.notifyAll()` para despertar a todos los hilos.

### Flujo de ejecución

1. `Control` inicia los hilos trabajadores.
2. Cada `TMILISECONDS`, `Control` activa la pausa con `paused = true`.
3. Se imprime el total de primos encontrados.
4. El programa espera la entrada del usuario con `Scanner.nextLine()`.
5. Al presionar ENTER, `Control` desactiva la pausa y llama a `notifyAll()`.
6. Los workers retoman su ejecución desde el punto en el que quedaron suspendidos.

### Cómo se evitan errores de sincronización

- **Lost wakeups**: se usa un `while` alrededor de `wait()` para revalidar la condición después de cada despertar.
- **Inconsistencia del estado**: la lectura y escritura de `paused` se hacen bajo el mismo monitor.
- **Espera activa**: no se hace polling; los hilos quedan bloqueados dentro de `wait()` hasta que son despertados.


### Conclusión

La implementación aplica correctamente un esquema de pausa y reanudación basado en monitores en Java. El uso de un único lock compartido y de una condición explícita permite coordinar los hilos de manera segura, clara y sin consumo innecesario de CPU.
