# Energy Community System

## About the Project

The Energy Community System is a distributed systems project based on RabbitMQ, PostgreSQL, Spring Boot, and JavaFX.

An energy community is an association of at least two participants for the joint production and utilization of energy. In this project, the system simulates community energy production and community energy usage. Several independent applications communicate through a message queue, process incoming data, store calculated results in a database, and provide the data to a graphical user interface.

The goal of the project is to build a system consisting of multiple independent components that shows the current energy distribution and historical energy usage.

---

## Project Idea

At the center of the system is a message queue that receives energy production and usage messages.

Based on these updates:

* the current community and grid usage is calculated
* hourly usage data is stored in the database
* the percentage values for the current hour are calculated
* the data is displayed in a JavaFX GUI via a REST API

If a community user wants energy, the community energy pool is used first. If the available energy in the community pool is not sufficient, the remaining part is taken from the grid.

---

## System Workflow

The system works as follows:

1. The **Community Energy Producer** sends production data to RabbitMQ based on the current weather.
2. The **Community Energy User** sends usage data to RabbitMQ based on the time of day.
3. The **Usage Service** receives the messages, aggregates the data into hourly values, and stores it in the database.
4. The **Current Percentage Service** receives update messages and calculates the percentage values for the current hour.
5. The **REST API** reads the stored values from the database.
6. The **JavaFX GUI** displays the current percentage data and historical usage data.

---

## System Overview

A graph of the system:

<img width="608" height="671" alt="Diagram" src="https://github.com/user-attachments/assets/3a743a7d-5bcc-49c3-b458-5a7bf8074d5e" />

---

## Components

### 1. Community Energy Producer

The Community Energy Producer sends the following message to RabbitMQ:

* `type`: `PRODUCER`
* `association`: `COMMUNITY`
* `kwh`: the kWh produced in a minute
* `datetime`: the datetime of the energy production

The producer sends a message every couple of seconds with a semi-random but plausible amount of kWh.

To make the production more realistic, the producer uses the **Open-Meteo API**. Better weather and daylight conditions lead to higher production values.


### 2. Community Energy User

The Community Energy User sends the following message to RabbitMQ:

* `type`: `USER`
* `association`: `COMMUNITY`
* `kwh`: the kWh used in a minute
* `datetime`: the datetime of the energy usage

The user sends a message every couple of seconds with a semi-random but plausible amount of kWh.

The time of day is used to simulate higher energy demand during peak hours in the morning and in the evening.


### 3. Usage Service

The Usage Service receives production and usage messages from RabbitMQ and updates the usage data in the database.

Its tasks are:

* reading producer and user messages
* aggregating minute-based values into the corresponding hour
* storing the results in the `usage_data` table
* deciding whether the requested energy is taken from the community pool or the grid
* sending an update message after new usage data has been saved

The stored hourly values are:

* `community_produced`
* `community_used`
* `grid_used`

The logic follows the project specification:

* `community_used` can never be greater than `community_produced`
* if the requested usage is larger than the currently available community energy, the remaining part is added to `grid_used`

In the current implementation, the **Usage Service** also creates both required database tables through SQL migration.


### 4. Current Percentage Service

The Current Percentage Service receives update messages from RabbitMQ after the usage data has changed.

Its tasks are:

* reading the updated hourly usage values
* calculating `community_depleted`
* calculating `grid_portion`
* storing the result in the `current_percentage` table

The table `current_percentage` stores the percentage values of the current hour.

In the current implementation, the **Current Percentage Service** validates the existing database structure with JPA.


### 5. Spring Boot REST API

The REST API provides the interface between the backend and the GUI.

It contains the two required endpoints from the specification:

* `GET /energy/current`
* `GET /energy/historical?start=...&end=...`

The REST API is connected to the database and only reads data from the tables.

It returns:

* the current percentage data of the current hour
* the historical usage data for a selected time period


### 6. JavaFX GUI

The JavaFX GUI is the frontend of the system.

It allows the user to:

* refresh the current percentage data
* request historical energy data using a time filter
* display current values in labels
* display historical values in a table

The GUI is not directly connected to the database. It uses the REST API to fetch the required data.

<img width="748" height="761" alt="GUI" src="https://github.com/user-attachments/assets/aaf51ce8-d1e4-4153-b533-b13310d67d88" />

---

## Message Structure

### Producer and User Message

```json
{
  "type": "PRODUCER or USER",
  "association": "COMMUNITY",
  "kwh": 0.00123,
  "datetime": "2026-06-08T21:21:52"
}
```

### Usage Update Message

```json
{
  "usageHour": "2026-06-08T21:00:00",
  "communityProduced": 12.45,
  "communityUsed": 10.87,
  "gridUsed": 1.58
}
```

---

## Database Structure

The following database schema shows the two tables of the system and the stored values.

<img width="695" height="256" alt="datenbanks" src="https://github.com/user-attachments/assets/c4d8b31e-7b5a-4d43-93aa-d94a23533ec9" />

The table `usage_data` contains the hourly aggregated usage data. It stores, for each hour, how much energy was produced inside the community, how much of that energy was used by community users, and how much additional energy had to be taken from the grid.

The table `current_percentage` contains the calculated percentage values for the current hour. It stores how much of the community pool has already been depleted (`community_depleted`) and how large the grid share of the total energy consumption is (`grid_portion`).

Both tables use `usage_hour` as the hourly time reference. In the current implementation, the **Usage Service** creates both tables, while the **Current Percentage Service** validates the existing table structure with JPA.

---

## Design Ideas and Architecture

The system was designed as a distributed solution with clearly separated responsibilities. Each application has one focused task and can be started independently.

### Main Design Decisions

#### Separation into Independent Components

Instead of building one large application, the project was split into six components:

* Community Energy Producer
* Community Energy User
* Usage Service
* Current Percentage Service
* REST API
* JavaFX GUI

This makes the architecture easier to understand, easier to test, and closer to a realistic distributed system.

#### RabbitMQ as Communication Layer

RabbitMQ is used for asynchronous communication between the components.

Advantages of this decision:

* loose coupling between services
* clear separation of responsibilities
* event-based communication
* easier extensibility

#### PostgreSQL as Persistence Layer

PostgreSQL stores the hourly usage data and current percentage data.

Advantages:

* persistent storage
* support for historical queries
* stable data source for the REST API

#### REST API Between Backend and GUI

The GUI does not communicate directly with the database. Instead, the REST API acts as the access layer between frontend and backend.

This matches the project specification and keeps the frontend separated from backend logic.

#### Realistic Data Generation

To make the simulation more realistic:

* the producer uses weather-based generation
* the user uses time-based consumption patterns

This makes the generated values more plausible than fully random numbers.

---

## Setup and Execution

### Requirements

* Java 21
* Maven
* Docker
* IntelliJ IDEA
* Internet connection for the weather API

### Infrastructure

The infrastructure is started with Docker Compose.

Included services:

* PostgreSQL
* RabbitMQ
* RabbitMQ Management UI

Used ports:

* PostgreSQL: `5432`
* RabbitMQ: `5672`
* RabbitMQ Management UI: `15672`
* REST API: `9090`

### Installation

1. Clone or download the project from the [GitHub repository](https://github.com/wi24b059/Distributed-Systems-Project.git).
3. Open the project in IntelliJ IDEA.
4. Reload all Maven dependencies.
5. Start the Docker containers for PostgreSQL and RabbitMQ.

### Recommended Startup Order

1. Start Docker containers
2. Start `usage-service`
3. Start `percentage-service`
4. Start `producer-service`
5. Start `consumer-service`
6. Start `energy-rest-api`
7. Start `energy-javafx-gui`

---

## REST API Examples

### Current Percentage Data

```text
http://localhost:9090/energy/current
```

### Historical Usage Data

```text
http://localhost:9090/energy/historical?start=2025-01-10T06:00:00&end=2025-01-10T14:00:00
```

### Datetime Format

```text
yyyy-MM-ddTHH:mm:ss
```

Example:

```text
2025-01-10T06:00:00
```

---

## GUI Usage

The JavaFX GUI allows the user to interact with the system in a simple way.

Main functions:

* refresh current percentage values
* request historical data for a given time range
* display returned values in a structured layout

The historical values in the table are formatted with two decimal places.

The GUI also informs the user if:

* current data is not available
* historical data could not be loaded
* no historical data exists for the selected time range
* the entered datetime format is invalid

---

## Lessons Learned

During the implementation of the project, several important lessons were learned:

* Distributed systems require strict consistency in JSON structure and datetime formatting.
* Separating the logic into multiple services makes the project easier to structure and explain.
* Correct timestamp handling is important when minute-based values are aggregated into hours.
* Clear user feedback in the GUI improves usability.

---

## Time Spent

Approximate total time spent on the project: **72 hours**

---

## Developers

* **Aden Ali** (wi24b069)
* **Benjamin Hirsch** (wi24b064)
* **Somih Timory** (wi24b059)

---

## Date

Completed in **June 2026**.
