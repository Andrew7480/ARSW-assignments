# Java RMI - Echo Server

## Descripción

Este proyecto implementa un ejemplo básico de comunicación distribuida utilizando Java RMI (Remote Method Invocation). El sistema está compuesto por un servidor que ofrece un servicio remoto y un cliente que consume dicho servicio a través de una invocación remota.

El servicio implementado corresponde a un servidor *Echo*, el cual recibe una cadena de texto y retorna la misma cadena precedida por el mensaje:

```
desde el servidor:
```

---

## Conceptos de RMI

Java RMI (Remote Method Invocation) permite que un programa Java invoque métodos de objetos que se encuentran ejecutándose en otra Máquina Virtual de Java (JVM), incluso en equipos diferentes conectados por red.

El modelo está compuesto por:

* **Objeto remoto:** objeto que ofrece servicios a otros procesos.
* **Interfaz remota:** define los métodos que pueden ser invocados remotamente.
* **Registro RMI (Registry):** servicio encargado de asociar nombres con objetos remotos.
* **Cliente:** obtiene una referencia al objeto remoto y realiza invocaciones sobre él.

En este ejemplo el cliente obtiene una referencia al servicio denominado `echoServer` y ejecuta el método remoto `echo()`.

---

## Estructura del proyecto

```
rmiexample/
│
├── src/
│   ├── EchoServer.java
│   ├── EchoServerImpl.java
│   └── EchoClient.java
│
└── out/
```

### Archivos principales

* **EchoServer.java**: interfaz remota que define el servicio.
* **EchoServerImpl.java**: implementación del servidor y publicación del objeto remoto.
* **EchoClient.java**: cliente que localiza el servicio y realiza la invocación remota.

---

## Requisitos

* Java JDK 21 o superior.
* Consola de comandos (PowerShell, CMD o terminal).

---

## Consideraciones para Java 21+

El ejemplo original del taller utiliza:

* `rmiregistry`
* `SecurityManager`
* archivos de políticas (`policy`)

Debido a cambios introducidos en versiones recientes de Java, el proyecto fue adaptado para crear el registro RMI directamente desde el servidor mediante:

```java
LocateRegistry.createRegistry(23000);
```

Esta modificación mantiene el mismo comportamiento de RMI sin depender de componentes obsoletos.

---

## Compilación

Desde la carpeta raíz del proyecto ejecutar:

```bash
javac -d out src\*.java
```

---

## Ejecución

### Iniciar el servidor

```bash
java -cp out EchoServerImpl
```

Salida esperada:

```text
Registry iniciado
Echo server ready...
```

### Ejecutar el cliente

Abrir una segunda terminal y ejecutar:

```bash
java -cp out EchoClient
```

---

## Resultado esperado

El cliente invocará el método remoto `echo()` y recibirá la respuesta enviada por el servidor:

```text
desde el servidor: Hola como estas?
```

Lo anterior demuestra que la comunicación remota entre cliente y servidor fue realizada correctamente mediante Java RMI.
