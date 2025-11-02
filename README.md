# Sensor network with REST and gRPC (distributed systems lab 1)

First lab assignment for **Raspodijeljeni sustavi** (Distributed Systems), graduate study, 2025/26.

A centralised distributed system for collecting environmental sensor data. A Spring Boot **REST server** keeps track of the registered sensors and their readings. Each **sensor** is a separate process: it registers with the server, finds its geographically nearest neighbour, exchanges readings with it over **gRPC**, and stores the calibrated result back on the server.

```
             REST (JSON)                      REST (JSON)
 Sensor 1 ───────────────►  Web server  ◄─────────────── Sensor 2
     │     register,          (Spring       register,        ▲
     │     nearest,           Boot + H2)    nearest,         │
     │     store reading                    store reading    │
     └──────────────────────── gRPC ─────────────────────────┘
                     reading exchange between neighbours
```

## How a sensor works

1. **Start-up and registration.** The sensor picks a random location (latitude 45.75–45.85, longitude 15.87–16.00) and a free port, starts its own gRPC server on that port, and registers with the REST server. If registration fails, the sensor shuts down.
2. **Nearest neighbour.** The sensor asks the server for its nearest registered sensor. The server works out distances with the Haversine formula.
3. **Reading.** Every 10 seconds the sensor takes a reading from `readings.csv`, using row `(seconds active % 100) + 1`. Each row has temperature, pressure, humidity, CO, NO₂ and SO₂.
4. **Exchange over gRPC.** The sensor calls `FetchLatestReading` on its neighbour's gRPC server. Since every sensor is also a gRPC server, it can answer several neighbours at once.
5. **Calibration.** Each value is averaged with the neighbour's. A missing value (or 0) counts as a sensor error, and only the other value is used.
6. **Storing.** The calibrated reading is sent to the server. If the sensor has no neighbour, or the neighbour stops responding, the sensor stores its own reading instead and keeps running.

## REST API (server, port 8090)

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/sensors/register` | Register a sensor. Returns `201 Created` with a `Location` header. |
| `GET` | `/sensors` | List all registered sensors |
| `GET` | `/sensors/nearest?sensorId={id}` | Nearest neighbour of a sensor, or `204 No Content` if there is none |
| `POST` | `/readings?port={port}` | Store a reading for the sensor on that port. Returns `201 Created`, or `204 No Content` if the sensor doesn't exist. |
| `GET` | `/readings/{sensorId}` | All readings of a sensor |

The data is kept in an in-memory H2 database. You can browse it at `http://localhost:8090/h2-console` (JDBC URL `jdbc:h2:mem:testdb`, user `sa`, no password).

## gRPC service

```proto
service SensorService {
  rpc FetchLatestReading (SensorRequest) returns (SensorReadingMessage) {}
}
```

## Project layout

```
server/          Spring Boot REST server (controllers, services, JPA entities)
sensor-client/   sensor: REST client, gRPC client and gRPC server
  src/main/proto/sensor.proto
  src/main/resources/readings.csv
```

## Running

Requires **Java 21**. Gradle is downloaded automatically by the wrapper.

Start the server first:

```bash
cd server
./gradlew bootRun          # Windows: .\gradlew.bat bootRun
```

Then start as many sensors as you like, each in its own terminal:

```bash
cd sensor-client
./gradlew bootRun          # Windows: .\gradlew.bat bootRun
```

Each sensor logs its registration, its neighbour, the readings it exchanges and the calibrated values it stores. The log messages are in Croatian.

## Tech

Java 21 · Spring Boot 3.1 · Spring Data JPA · H2 · gRPC 1.68 · Protocol Buffers · Gradle 8.7
