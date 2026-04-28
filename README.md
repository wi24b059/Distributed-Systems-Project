# Energy Community System

## About The Project

The Energy Community System is a project where a distributed system with a REST-based API, a RabbitMQ message queue and a JavaFX UI is implemented.

Energy communities are associations of at least two participants for the joint production and utilization of energy. There are community producers and users as well as grid producers and users. The goal of this project is to build a system, consisting of multiple independent components, that shows the current energy distribution and usage.

The workflow:

* Community Energy Producer sends production data to the queue based on the current weather
* Community Energy User sends usage data to the queue based on the time of day
* The Usage Service processes the incoming messages and updates the usage data
* The Current Percentage Service calculates the percentage values for the current hour
* The REST API reads the data and returns it to the GUI
* The GUI displays the current percentage data and historical usage data

For the milestone version, the project includes a reduced implementation with a Spring Boot REST API and a JavaFX GUI. The REST API provides structured example data for testing and demonstration purposes, while the GUI interacts with the API and displays the returned data.

### The System Overview

A graph of the system:

<img width="608" height="671" alt="Diagram" src="https://github.com/user-attachments/assets/3a743a7d-5bcc-49c3-b458-5a7bf8074d5e" />


## The Components

### JavaFX GUI

The JavaFX GUI is the user interface of the system. It allows the user to refresh the current percentage data and request historical energy usage data for a selected time range. The GUI communicates with the backend through the REST API and displays the returned values in labels and tables.

<img width="753" height="769" alt="gui-screenshot" src="https://github.com/user-attachments/assets/e025df34-f6f7-4bc3-bb7c-fe3cb6ba8c3a" />

### Spring Boot REST API

The Spring Boot Application provides the REST API of the system. It contains the Energy Controller, which is responsible for the two endpoints `/energy/current` and `/energy/historical?start=...&end=...`. It also contains the Energy Service, which provides structured example data for the milestone and handles the filtering of historical data.

### Community Energy Producer

The Community Energy Producer sends the following message to the queue:

* type: `PRODUCER`
* association: `COMMUNITY`
* kwh: the kWh produced in a minute
* datetime: the datetime of the energy production

The Energy Producer sends a message every couple of seconds with a semi random but plausible amount of kWh. A Weather API is used so that more energy is produced when the sun is shining.

### Community Energy User

The Community Energy User sends the following message to the queue:

* type: `USER`
* association: `COMMUNITY`
* kwh: the kWh used in a minute
* datetime: the datetime of the energy usage

The Energy User sends a message every couple of seconds with a semi random but plausible amount of kWh. The time of day is used so that more energy is needed in peak hours in the morning and in the evening.

### Usage Service

The Usage Service receives production and usage messages from the queue and updates the usage data in the database. Data from individual minutes is accumulated into the corresponding hours. If the community production pool is not sufficient, additional energy is taken from the grid.

### Current Percentage Service

The Current Percentage Service receives update messages and calculates the percentage values of the current hour. It stores the current values for `community_depleted` and `grid_portion`.

## User Guide

This guide will help you set up and run the project using IntelliJ and Maven.

### Requirements

* IntelliJ IDEA (Community or Ultimate edition) installed on your system
* Docker installed on your system


### Installation

1. Clone or download the project from the GitHub repository.
2. Open the project in IntelliJ IDEA.
3. Make sure all Maven dependencies are loaded correctly.

### Running the REST API

1. Open the `energy-rest-api` module in IntelliJ IDEA.
2. Start the Spring Boot application.
3. The REST API will run on port `9090`.

Example endpoints:

* `http://localhost:9090/energy/current`
* `http://localhost:9090/energy/historical?start=2025-01-10T06:00:00&end=2025-01-10T14:00:00`

### Running the GUI

1. Open the `energy-javafx-gui` module in IntelliJ IDEA.
2. Start the JavaFX application.
3. Click the `Refresh` button to load the current percentage data.
4. Enter a start and end datetime in the given format.
5. Click the button for historical data to request the data from the REST API.

Datetime format:

* `yyyy-MM-ddTHH:mm:ss`
* example: `2025-01-10T06:00:00`

That’s it! You can now use the GUI to interact with the REST API and display the current and historical energy data.

Please note that this guide assumes basic familiarity with IntelliJ IDEA and Maven. If you encounter any issues or have specific questions about the project, refer to the project documentation or consult the project repository.

## Credits

### Developer

The distributed system is coded by the following developers:

* Aden Ali (wi24b069)
* Benjamin Hirsch (wi24b064)
* Somih Timory (wi24b059)

### Date of creation

The project milestone was completed in April 2026.
