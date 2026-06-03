
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


## Parte I — (Calentamiento) `wait/notify` en un programa multi-hilo

1. Toma el programa [**PrimeFinder**](https://github.com/ARSW-ECI/wait-notify-excercise).
2. Modifícalo para que **cada _t_ milisegundos**:
   - Se **pausen** todos los hilos trabajadores.
   - Se **muestre** cuántos números primos se han encontrado.
   - El programa **espere ENTER** para **reanudar**.
3. La sincronización debe usar **`synchronized`**, **`wait()`**, **`notify()` / `notifyAll()`** sobre el **mismo monitor** (sin _busy-waiting_).
4. Entrega en el reporte de laboratorio **las observaciones y/o comentarios** explicando tu diseño de sincronización (qué lock, qué condición, cómo evitas _lost wakeups_).

> Objetivo didáctico: practicar suspensión/continuación **sin** espera activa y consolidar el modelo de monitores en Java.

## Observaciones y diseño de sincronización (Parte I)

- Lock/monitor: se usa el objeto `monitor` definido en la clase `Control` (accesible via `getMonitor()`). Todos los `synchronized(monitor)` están usando el mismo monitor para coordinar estados.
- Condición: la bandera `paused` indica si los hilos deben detenerse. Los workers comprueban la condición con `while(control.isPaused()) { monitor.wait(); }`.
- Evitar *lost wakeups*: se usa un bucle `while` alrededor de `wait()` (en lugar de `if`) para volver a comprobar la condición tras cualquier wakeup; además `Control` invoca `monitor.notifyAll()` al reanudar para despertar a todos los hilos.
- No hay *busy-waiting*: los hilos no hacen polling activo; llaman a `wait()` y liberan el monitor hasta que `notifyAll()` los despierta.
- Visibilidad/memoria: las escrituras y lecturas de `paused` se realizan bajo el mismo monitor; en particular, `isPaused()` sincroniza con `synchronized(monitor)` para usar el mismo lock que `wait()`/`notifyAll()`.

Notas prácticas:

- El control principal (clase `Control`) duerme `TMILISECONDS` entre puntos de pausa y, cuando llega el momento, establece `paused=true` dentro de `synchronized(monitor)` y luego imprime el total de primos encontrados.
- Para reanudar, `Control` espera la entrada del usuario (`Scanner.nextLine()`), vuelve a poner `paused=false` dentro de `synchronized(monitor)` y llama a `monitor.notifyAll()`.

Este diseño cumple los requisitos: pausa todos los trabajadores periódicamente, muestra el conteo de primos, espera ENTER para continuar, usa `synchronized`/`wait`/`notifyAll` sobre el mismo monitor y evita busy-waiting y lost wakeups.
