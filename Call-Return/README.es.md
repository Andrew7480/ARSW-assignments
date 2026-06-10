# Lab 01 - Introducción a Esquemas de Nombres, Redes, Clientes y Servicios con Java

**Curso:** Arquitecturas de Software (ARSW) 2026-i  
**Escuela Colombiana de Ingeniería**  

---

## Objetivo

Comprender e implementar los fundamentos de la comunicación en red mediante Java, abordando progresivamente desde el manejo de URLs y sockets TCP, hasta datagramas UDP e invocación remota de métodos (RMI). Al finalizar el laboratorio, el estudiante es capaz de diseñar e implementar aplicaciones distribuidas cliente-servidor usando las abstracciones que provee el paquete `java.net`.

---

## Estructura del laboratorio

```
Call-Return/
│
├── exercise1/          URL - Propiedades de un objeto URL
├── exercise2/          URL - Mini-browser que descarga páginas
├── exercise3/          Sockets TCP - Servidor cuadrado y servidor de funciones
├── exercise4/          Sockets TCP - Servidor HTTP con soporte de múltiples archivos
├── exercise5/          UDP - Cliente/servidor de hora con tolerancia a fallos
├── exercise6/          RMI - Echo Server distribuido
└── exercise7/          RMI - Chat distribuido peer-to-peer
```

Cada carpeta contiene su propio `README.md` con la descripción detallada del ejercicio, capturas de ejecución y explicación de la implementación.

---

## Ejercicios

### Exercise 1 — Propiedades de un objeto URL

Se creó un objeto `URL` en Java y se imprimieron en pantalla los valores retornados por los ocho métodos de inspección: `getProtocol`, `getAuthority`, `getHost`, `getPort`, `getPath`, `getQuery`, `getFile` y `getRef`.

**Conceptos clave:** clase `java.net.URL`, estructura de una URL, manejo de `MalformedURLException`.

---

### Exercise 2 — Mini-browser

Se implementó una aplicación que solicita al usuario una dirección URL, descarga el contenido HTML de esa dirección mediante `openStream()` y lo almacena en un archivo local llamado `resultado.html`. El archivo generado puede abrirse directamente en el navegador.

**Conceptos clave:** `URLConnection`, `BufferedReader`, escritura de archivos con `FileWriter`.

---

### Exercise 3 — Servidores TCP con Sockets

Se implementaron dos servidores sobre sockets TCP:

1. **Servidor cuadrado:** recibe un número entero y responde con su cuadrado. Si el valor recibido no es numérico, el servidor responde con un mensaje de error.

2. **Servidor de funciones trigonométricas:** recibe números y calcula una función matemática (seno, coseno o tangente). La función activa puede cambiarse en tiempo de ejecución enviando un mensaje con el prefijo `fun:` (por ejemplo, `fun:sin`). Por defecto inicia calculando coseno.

   La lógica de intercambio de operaciones fue implementada de dos formas:
   - Usando el **patrón de diseño Strategy** con clases independientes (`Sen`, `Cos`, `Tan`).
   - Usando **expresiones lambda**, lo que simplificó el código manteniendo el mismo comportamiento.

**Conceptos clave:** `Socket`, `ServerSocket`, `BufferedReader`, `PrintWriter`, patrón Strategy, lambdas en Java.

---

### Exercise 4 — Servidor HTTP

Se implementaron dos versiones de un servidor web sobre sockets TCP en el puerto 35000:

1. **Versión básica:** atiende una única solicitud HTTP y responde con una página HTML estática construida como cadena de texto.

2. **Versión extendida (múltiples solicitudes):** el servidor permanece en un ciclo continuo atendiendo solicitudes secuenciales (no concurrentes). Es capaz de servir archivos HTML e imágenes almacenados en el directorio `web/`, identificando el tipo de contenido con el encabezado `Content-Type` apropiado. Si el recurso no existe, responde con `404 Not Found`.

**Conceptos clave:** protocolo HTTP/1.1, encabezados HTTP, lectura y envío de archivos binarios, manejo de tipos MIME.

---

### Exercise 5 — Datagramas UDP con tolerancia a fallos

Se implementó un par cliente-servidor usando el protocolo UDP:

- El **servidor** escucha solicitudes y responde con la fecha y hora actual del sistema.
- El **cliente** consulta al servidor cada 5 segundos para obtener la hora actualizada. Si el servidor no responde dentro del tiempo de espera (timeout), el cliente conserva la última hora recibida sin interrumpir su ejecución.

Durante las pruebas se apagó el servidor mientras el cliente estaba activo: el cliente continuó funcionando mostrando la última hora conocida, y al reiniciar el servidor se actualizó de forma automática.

**Conceptos clave:** `DatagramSocket`, `DatagramPacket`, protocolo UDP, timeout de socket, resiliencia ante caídas del servidor.

---

### Exercise 6 — RMI Echo Server

Se implementó el ejemplo base de Java RMI. Un servidor publica un objeto remoto que ofrece el método `echo()`, el cual recibe un `String` y lo retorna precedido por `"desde el servidor:"`. El cliente localiza el servicio en el registro RMI y realiza la invocación de forma transparente.

El proyecto fue adaptado para Java 21+ creando el registro directamente con `LocateRegistry.createRegistry()`, evitando el uso del componente externo `rmiregistry` y del `SecurityManager` ya obsoleto.

**Conceptos clave:** `Remote`, `UnicastRemoteObject`, `Registry`, stubs y skeletons, serialización de parámetros.

---

### Exercise 7 — Chat Distribuido con RMI

Se implementó una aplicación de chat peer-to-peer usando RMI. Cada instancia actúa simultáneamente como servidor (publica un objeto remoto para recibir mensajes) y como cliente (se conecta al servicio del otro participante para enviar mensajes).

La aplicación solicita al inicio: nombre de usuario, puerto local donde publica su servicio, IP y puerto del interlocutor. Incluye un mecanismo de reintento automático: si el servicio remoto aún no está disponible, el programa espera e intenta reconectarse cada dos segundos, permitiendo iniciar las instancias en cualquier orden.

**Conceptos clave:** arquitectura peer-to-peer con RMI, publicación y descubrimiento de servicios remotos, `LocateRegistry.createRegistry`, manejo de `RemoteException`, reintentos con backoff.

---

## Tecnologías utilizadas

- Java JDK 21+
- `java.net` (URL, Socket, ServerSocket, DatagramSocket, DatagramPacket)
- Java RMI (`java.rmi`, `java.rmi.registry`, `java.rmi.server`)
- Protocolos TCP, UDP y HTTP

---

## Conclusiones

- El paquete `java.net` de Java provee abstracciones de alto nivel (URL, Socket) y bajo nivel (DatagramPacket) que simplifican la construcción de aplicaciones de red sin exponer directamente los detalles del protocolo subyacente.

- TCP y UDP responden a necesidades distintas: TCP garantiza entrega y orden, siendo adecuado para aplicaciones donde la integridad de los datos es crítica (servidores echo, HTTP); UDP sacrifica garantías a favor de velocidad y simplicidad, siendo útil cuando perder algún mensaje es aceptable (actualizaciones de hora).

- Los sockets son la abstracción fundamental del modelo cliente-servidor. Implementar un servidor HTTP desde cero permitió entender en detalle la estructura del protocolo, los encabezados y los tipos de contenido, algo que queda oculto cuando se usan frameworks de alto nivel.

- El patrón **Strategy** demostró ser una solución natural para implementar comportamiento intercambiable en tiempo de ejecución (cambio de función trigonométrica). Las lambdas de Java simplifican su implementación cuando las estrategias son operaciones simples.

- Java RMI abstrae completamente la comunicación de red, permitiendo invocar métodos en objetos remotos como si fueran locales. Esto facilita el diseño de sistemas distribuidos orientados a objetos, aunque introduce complejidad en la configuración del registro y el manejo de `RemoteException`.

- El ejercicio de chat RMI evidenció que en una arquitectura peer-to-peer cada nodo debe comportarse tanto como cliente como servidor, lo que requiere un diseño cuidadoso del ciclo de vida de la aplicación y de la gestión de conexiones.
