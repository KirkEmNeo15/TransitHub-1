# Routa: Transportation Management and Route Mapping System

> **Demo data notice:** All routes, stops, fares, schedules, vehicles,
> and drivers in this project are fictional sample data for a school
> project. They are **not** official transportation information. Routa
> does **not** provide live GPS tracking, live traffic information, or
> real-time arrival estimates.

## Project Description

Routa is a full-stack web application designed to help commuters in
Batangas City explore public transportation routes, including jeepneys,
buses, and vans, through an interactive map. Users can search for direct
routes between two places and view route details such as stops,
transportation type, fares, and operating hours. Registered users can
save favorite routes and report incorrect or outdated information.
Administrators can manage route information and service alerts.

The project is developed for OOP 101 and uses Java with Spring Boot for
the backend and React for the frontend.

## Problem Being Addressed

Transportation information can be scattered, outdated, or difficult to
find, especially for students and commuters who are unfamiliar with an
area. Routa brings route details, stops, fares, and operating hours
together in one place to make this information easier to access and
understand.

## Project Objectives

### General Objective

To develop a web-based transit information system, using a Java (Spring
Boot) backend and a React frontend, that allows commuters in Batangas
City to view jeepney routes on a map, search for direct routes between
two places, and view available fare and route information.

### Specific Objectives

-   Allow users to search for and view available transportation routes.
-   Display transportation routes and stops through an interactive map.
-   Provide information about fares and operating hours.
-   Allow registered users to save favorite transportation routes.
-   Apply Java Object-Oriented Programming concepts in the system.
-   Provide administrators with tools to manage transportation
    information.

## Target Users

-   **Commuters and students:** Search and view available routes, check
    stops and fare information, save favorite routes, read service
    alerts, and report incorrect route details.
-   **Administrators:** Manage routes, stops, transportation
    information, and service alerts.

## Main Features

### Public Features

-   Home page with an introduction and system statistics.
-   Browse and filter routes by transportation type.
-   View route details, including stops, fare, schedule, and status.
-   Interactive map with route lines and stop markers.
-   Search for direct routes between an origin and a destination.
-   View service alerts.

### Registered User Features

-   Register, log in, and log out using JWT-based authentication.
-   Save and view favorite routes.
-   Report incorrect or outdated route information.
-   View and manage a profile.

### Administrator Features

-   Manage transportation routes and stops.
-   Manage transportation information and service alerts.
-   Review user reports and access administrative information.

## Technologies Used

  -----------------------------------------------------------------------
  Layer                               Technology
  ----------------------------------- -----------------------------------
  Frontend                            React, TypeScript, Vite, Tailwind
                                      CSS, React Router, Axios

  Map                                 Leaflet, React Leaflet,
                                      OpenStreetMap

  Backend                             Java 21, Spring Boot, Spring Web,
                                      Spring Data JPA, Spring Security,
                                      JWT, Maven

  Database                            PostgreSQL

  Local database environment          Docker Compose

  Testing                             JUnit 5, Spring Boot Test, Vitest,
                                      React Testing Library

  Development tools                   Git, GitHub, VS Code, Docker
                                      Compose
  -----------------------------------------------------------------------

## Project Structure

``` text
Routa/
├── frontend/          React + TypeScript application
├── backend/           Spring Boot application
├── database/          Database schema, sample data, migrations, and tools
├── docs/              API, database, OOP, frontend, and testing documentation
├── .env.example       Environment-variable template
├── docker-compose.yml Local PostgreSQL configuration
└── README.md
```

The backend follows a layered structure: **controller → service →
repository**. DTO and mapper classes help transfer data between API
endpoints and entities, while a global exception handler provides a
consistent error response format.

## Setup and Installation

### 1. Prerequisites

Install or prepare the following:

-   Git
-   Docker Desktop, or a local PostgreSQL installation
-   Java 21 or newer
-   Node.js 18 or newer
-   The Maven wrapper included in the project

### 2. Configure Environment Variables

From the project root, copy the example environment file:

``` bash
cp .env.example .env
```

On Windows Command Prompt, use:

``` bat
copy .env.example .env
```

Open `.env` and set your own database credentials, JWT secret, and any
optional demo-account credentials. **Never commit your `.env` file or
real passwords to GitHub.**

Common variables include:

  -----------------------------------------------------------------------
  Variable                            Purpose
  ----------------------------------- -----------------------------------
  `DATABASE_NAME`                     PostgreSQL database name

  `DATABASE_USERNAME`                 Database username

  `DATABASE_PASSWORD`                 Database password

  `DATABASE_URL`                      JDBC connection URL used by Spring
                                      Boot

  `JWT_SECRET`                        Random secret used to sign
                                      authentication tokens

  `JWT_EXPIRATION_MS`                 Token lifetime in milliseconds

  `CORS_ALLOWED_ORIGINS`              Frontend origins permitted to call
                                      the API

  `DEMO_ADMIN_EMAIL`,                 Optional development admin account
  `DEMO_ADMIN_PASSWORD`               

  `DEMO_USER_EMAIL`,                  Optional development user account
  `DEMO_USER_PASSWORD`                
  -----------------------------------------------------------------------

Use a strong, private JWT secret and your own local credentials.

### 3. Start the Database

From the project root, run:

``` bash
docker compose up -d
docker compose ps
```

Wait for the PostgreSQL container to become healthy. On the first start
with an empty database, the configured initialization scripts create the
tables and load the fictional sample data.

**Warning:** Resetting the database with `docker compose down -v`
deletes the database volume and its data. Only do this if you intend to
start with a fresh database.

### 4. Run the Backend

Open a terminal and run:

``` bash
cd backend
./mvnw spring-boot:run
```

On Windows Command Prompt:

``` bat
mvnw spring-boot:run
```

When the backend is running, check the health endpoint:

<http://localhost:8080/api/health>

The expected response should indicate that the database is available.
Stop the backend with `Ctrl+C`.

### 5. Run the Frontend

Open a second terminal:

``` bash
cd frontend
npm install
npm run dev
```

Open the local address shown by Vite, usually:

<http://localhost:5173>

The frontend normally connects to the backend at
`http://localhost:8080`. If needed, configure `VITE_API_BASE_URL` in the
frontend environment file according to the project's setup
documentation.

### 6. Run the Tests

Backend tests:

``` bash
cd backend
./mvnw clean test
```

Frontend tests:

``` bash
cd frontend
npm test
```

Run the tests using the setup described in `docs/TESTING.md`. The
backend tests may require the database to be running.

## Sample Accounts

Development demo accounts are created only when the corresponding email
and password variables are configured in `.env`.

  Role    Email                         Password
  ------- ----------------------------- --------------------------------
  Admin   Value of `DEMO_ADMIN_EMAIL`   Value of `DEMO_ADMIN_PASSWORD`
  User    Value of `DEMO_USER_EMAIL`    Value of `DEMO_USER_PASSWORD`

Users can also register through the application's registration page.
Admin access is restricted and should not be available through ordinary
public registration. Demo accounts are intended for local development
and school demonstrations.

## Java and OOP Concepts

Routa demonstrates the Java OOP concepts covered in OOP 101:

  -----------------------------------------------------------------------
  Concept                             Application in Routa
  ----------------------------------- -----------------------------------
  **Classes and Objects**             Classes such as `Route`, `Stop`,
                                      `Schedule`, `Fare`, `Driver`, and
                                      `Vehicle` represent system
                                      entities. Objects are created from
                                      these classes and used by the
                                      application.

  **Methods**                         Methods perform operations such as
                                      retrieving route details, accessing
                                      stops and schedules, and processing
                                      route-search results.

  **Encapsulation**                   Class data is kept private and
                                      accessed through appropriate
                                      methods such as getters and
                                      setters.

  **Inheritance**                     Transportation types can inherit
                                      shared attributes and behaviors
                                      from a general transportation
                                      class.

  **Polymorphism**                    Different transportation types can
                                      provide their own behavior while
                                      being handled through a common
                                      transportation type.

  **Abstraction**                     Common transportation concepts are
                                      represented while unnecessary
                                      implementation details are hidden.

  **Exception Handling**              Invalid requests and missing
                                      resources are handled with
                                      appropriate error responses instead
                                      of uncontrolled failures.

  **Arrays and Data Handling**        Collections of routes, stops,
                                      schedules, and other records are
                                      processed and returned by the
                                      application.
  -----------------------------------------------------------------------

For the detailed class design and examples, see
[`docs/OOP-DESIGN.md`](docs/OOP-DESIGN.md).

## API Documentation

See [`docs/API.md`](docs/API.md) for the complete list of endpoints,
request formats, responses, and error examples.

The API includes areas for:

-   Authentication and user profiles.
-   Route browsing, route details, and direct route search.
-   Stops and nearby-stop lookup.
-   Transportation information, alerts, and statistics.
-   User favorites and reports.
-   Administrator route, stop, transportation, alert, and
    user-management functions.

Access to protected endpoints depends on the user's role and
authentication status.

## Known Limitations

-   The current project scope covers **Batangas City only**.
-   Route, stop, fare, schedule, vehicle, and driver information uses
    fictional sample data and is not official transportation
    information.
-   Route lines use stored coordinates; the system does not track
    vehicles using live GPS.
-   Live traffic information and real-time arrival estimates are not
    provided.
-   Route search currently supports **direct routes only**; transfers
    between different routes are not supported.
-   Results depend on the transportation data stored in the database.
-   An internet connection is required to load OpenStreetMap tiles.
-   Some transportation details may be managed as part of a route rather
    than through separate administration screens, depending on the
    current implementation.

## Future Improvements

Possible future improvements include:

-   Expanding coverage beyond Batangas City.
-   Integrating verified transportation data from reliable sources.
-   Adding transfer-aware route search.
-   Adding live GPS tracking, traffic information, and estimated arrival
    times.
-   Improving route recommendations and map usability.
-   Expanding administrative tools for managing vehicles, drivers,
    fares, and schedules.

These are possible future enhancements and are not claims about features
currently available.

## Team Members and Responsibilities

  -----------------------------------------------------------------------
  Member                              Assigned Responsibilities
  ----------------------------------- -----------------------------------
  Rodrigo B. Alderite Jr.             Backend development: Spring Boot
                                      API, routes, fares, login, and
                                      error handling

  Red Zildjian C. Sepillo             Frontend development: React pages,
                                      route cards, and map interface

  Kirk Em Neo M. Legaspi              Frontend development and data
                                      gathering: map interface and
                                      Batangas City route data

  Karl Vincent C. Samonte             Database and data gathering:
                                      PostgreSQL tables and data-loading
                                      scripts
  -----------------------------------------------------------------------

## GitHub Repository

Repository: [Routa](https://github.com/potzkie1906/Routa)

The repository contains the project's source code and, as they are
added, documentation, UML/class diagrams, screenshots, and other
required project files.

## Screenshots

Add current screenshots of the working application to
`docs/screenshots/` and update this table when they are available.

  Screen                   Suggested File
  ------------------------ -----------------------------------------
  Home page                `docs/screenshots/home.png`
  Routes list and search   `docs/screenshots/routes.png`
  Interactive map          `docs/screenshots/map.png`
  Route details            `docs/screenshots/route-details.png`
  Admin dashboard          `docs/screenshots/admin-dashboard.png`
  Admin route form         `docs/screenshots/admin-route-form.png`

## Additional Documentation

-   [API Documentation](docs/API.md)
-   [Database Design](docs/DATABASE-DESIGN.md)
-   [OOP Design](docs/OOP-DESIGN.md)
-   [Frontend Setup](docs/FRONTEND-SETUP.md)
-   [Testing Guide](docs/TESTING.md)
