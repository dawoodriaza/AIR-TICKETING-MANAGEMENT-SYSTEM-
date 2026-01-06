
---
## API Documentation (DRAFT)

### Flight

  | Method | Endpoint                                             | Functionality                      | Acceess |
  | ------ | ---------------------------------------------------- | ---------------------------------- | ------- |
  | GET    | `/api/flights`                                       | Retrieve list of flights           | PUBLIC  |
  | GET    | `/api/airports/{airportId}/flights/{airportId}`      | Retrieve flight                    | PUBLIC  |
  | GET    | `/api/airports/{airportId}/flights`                  | Retrieve List of flights           | PUBLIC  |
  | POST   | `/api/flights`                                       | Create flight                      | PRIVATE |
  | POST   | `/api/airports/{originAirportId}/flights`            | Create flight                      | PRIVATE |
  | PUT    | `/api/flights/{flightId}`                            | Update flight                      | PRIVATE |
  | PUT    | `/api/airports/{originAirportId}/flights/{flightId}` | Update flight                      | PRIVATE |
  | DELETE | `/api/flights/{flightId}`                             | Delete flight                      | PRIVATE |
  | GET    | `/api/airports/{airportId}/departures`               | Retrieve List of departing flights | PUBLIC  |
  | GET    | `/api/airports/{airportId}/arrivals`                 | Retrieve List of arriving flights  | PUBLIC  |
---