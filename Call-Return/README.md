# Lab 01 - Introduction to Naming Schemes, Networks, Clients and Services with Java

**Course:** Software Architectures (ARSW) 2026-i  
**Escuela Colombiana de Ingeniería**  

> [Versión en español](README.es.md)

---

## Objective

Understand and implement the fundamentals of network communication in Java, progressing from URL handling and TCP sockets through UDP datagrams to Remote Method Invocation (RMI). By the end of the lab, students are able to design and implement distributed client-server applications using the abstractions provided by the `java.net` package.

---

## Lab Structure

```
Call-Return/
│
├── exercise1/          URL - Reading properties of a URL object
├── exercise2/          URL - Mini-browser that downloads pages
├── exercise3/          TCP Sockets - Square server and function server
├── exercise4/          TCP Sockets - HTTP server with multi-file support
├── exercise5/          UDP - Time client/server with fault tolerance
├── exercise6/          RMI - Distributed Echo Server
└── exercise7/          RMI - Peer-to-peer distributed chat
```

Each folder contains its own `README.md` with a detailed description of the exercise, execution screenshots, and an explanation of the implementation.

---

## Exercises

### Exercise 1 — URL Object Properties

A `URL` object was created in Java and the values returned by all eight inspection methods were printed to the console: `getProtocol`, `getAuthority`, `getHost`, `getPort`, `getPath`, `getQuery`, `getFile`, and `getRef`.

**Key concepts:** `java.net.URL` class, URL structure, `MalformedURLException` handling.

---

### Exercise 2 — Mini-browser

An application was implemented that prompts the user for a URL, downloads the HTML content from that address using `openStream()`, and saves it to a local file named `resultado.html`. The generated file can be opened directly in any browser.

**Key concepts:** `URLConnection`, `BufferedReader`, file writing with `FileWriter`.

---

### Exercise 3 — TCP Socket Servers

Two servers were implemented over TCP sockets:

1. **Square server:** receives an integer and responds with its square. If the received value is not a valid number, the server replies with an error message.

2. **Trigonometric function server:** receives numbers and applies a mathematical function (sine, cosine, or tangent). The active function can be changed at runtime by sending a message prefixed with `fun:` (e.g. `fun:sin`). It defaults to cosine on startup.

   The function-switching logic was implemented in two ways:
   - Using the **Strategy design pattern** with independent classes (`Sen`, `Cos`, `Tan`).
   - Using **lambda expressions**, which simplified the code while keeping the same behavior.

**Key concepts:** `Socket`, `ServerSocket`, `BufferedReader`, `PrintWriter`, Strategy pattern, Java lambdas.

---

### Exercise 4 — HTTP Server

Two versions of a web server were implemented over TCP sockets on port 35000:

1. **Basic version:** handles a single HTTP request and responds with a static HTML page built as a string.

2. **Extended version (multiple sequential requests):** the server runs in a continuous loop handling sequential (non-concurrent) requests. It can serve HTML files and images stored in the `web/` directory, setting the appropriate `Content-Type` header for each file type. If the requested resource does not exist, it responds with `404 Not Found`.

**Key concepts:** HTTP/1.1 protocol, HTTP headers, reading and sending binary files, MIME type handling.

---

### Exercise 5 — UDP Datagrams with Fault Tolerance

A client-server pair was implemented using the UDP protocol:

- The **server** listens for incoming requests and replies with the current system date and time.
- The **client** queries the server every 5 seconds to get an updated time. If the server does not respond within the socket timeout, the client keeps the last received time and continues running without crashing.

During testing, the server was shut down while the client was active: the client kept running and displayed the last known time, then automatically updated once the server was restarted.

**Key concepts:** `DatagramSocket`, `DatagramPacket`, UDP protocol, socket timeout, resilience to server failures.

---

### Exercise 6 — RMI Echo Server

The base Java RMI example was implemented. A server publishes a remote object that exposes the `echo()` method, which receives a `String` and returns it prefixed with `"desde el servidor:"`. The client locates the service in the RMI registry and invokes the method transparently.

The project was adapted for Java 21+ by creating the registry programmatically via `LocateRegistry.createRegistry()`, avoiding the deprecated external `rmiregistry` command and the obsolete `SecurityManager`.

**Key concepts:** `Remote`, `UnicastRemoteObject`, `Registry`, stubs and skeletons, parameter serialization.

---

### Exercise 7 — Distributed Chat with RMI

A peer-to-peer chat application was implemented using RMI. Each instance simultaneously acts as a server (publishes a remote object to receive messages) and as a client (connects to the other participant's remote service to send messages).

On startup the application prompts for: username, local port to publish the service on, and the remote IP and port of the other participant. An automatic retry mechanism was included: if the remote service is not yet available, the program waits and retries every two seconds, allowing both instances to be started in any order.

**Key concepts:** peer-to-peer architecture with RMI, service publishing and discovery, `LocateRegistry.createRegistry`, `RemoteException` handling, connection retry with backoff.

---

## Technologies Used

- Java JDK 21+
- `java.net` (URL, Socket, ServerSocket, DatagramSocket, DatagramPacket)
- Java RMI (`java.rmi`, `java.rmi.registry`, `java.rmi.server`)
- TCP, UDP, and HTTP protocols

---

## Conclusions

- Java's `java.net` package provides both high-level (URL, Socket) and low-level (DatagramPacket) abstractions that simplify network application development without directly exposing underlying protocol details.

- TCP and UDP address different needs: TCP guarantees delivery and ordering, making it suitable for applications where data integrity is critical (echo servers, HTTP); UDP trades those guarantees for speed and simplicity, making it useful when occasional message loss is acceptable (time updates).

- Sockets are the fundamental abstraction of the client-server model. Building an HTTP server from scratch provided a detailed understanding of the protocol structure, headers, and content types — details that are hidden when using high-level frameworks.

- The **Strategy pattern** proved to be a natural fit for implementing interchangeable behavior at runtime (switching trigonometric functions). Java lambdas simplify its implementation when the strategies are concise operations.

- Java RMI fully abstracts network communication, allowing methods on remote objects to be invoked as if they were local. This simplifies the design of object-oriented distributed systems, though it introduces complexity in registry configuration and `RemoteException` handling.

- The RMI chat exercise demonstrated that in a peer-to-peer architecture each node must behave as both client and server simultaneously, which requires careful design of the application lifecycle and connection management.
