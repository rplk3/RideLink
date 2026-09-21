# RideLink

RideLink is a backend ride-sharing system developed using Spring Boot microservices and MongoDB.

## Project Services

1. Account Service
2. Driver & Vehicle Service
3. Ride Management Service
4. Fare & Payment Service

## Technology Stack

* Java and Spring Boot
* Spring Data MongoDB
* REST APIs
* Spring Security and JWT, where applicable
* Swagger/OpenAPI
* JUnit and Mockito
* Postman
* GitHub Actions

## Repository Structure

* `account-service/` — account and authentication functionality
* `driver-vehicle-service/` — driver and vehicle functionality
* `ride-management-service/` — ride lifecycle functionality
* `fare-payment-service/` — fare and payment functionality
* `docs/` — architecture diagrams and documentation
* `postman/` — API testing collections

## Development Rules

* Do not commit directly to `main`.
* Each member must work on a feature branch.
* Use pull requests for merging changes.
* Review changes before merging.
* Do not commit passwords, API keys, or database credentials.
* Each service must maintain its own database boundary.
* Document API changes before integration.

## Team Members

| Member                  | Service        | Branch                    |
| ----------------------- | -------------- | ------------------------- |
| Member 1 — Group Leader | To be assigned | `feature/leader-service`  |
| Member 2                | To be assigned | `feature/member2-service` |
| Member 3                | To be assigned | `feature/member3-service` |
| Member 4                | To be assigned | `feature/member4-service` |
