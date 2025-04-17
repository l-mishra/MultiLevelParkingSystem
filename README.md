# Parking Lot System

A Spring Boot based parking lot management system that handles vehicle parking, slot allocation, and payment calculation.

## Features

- **Vehicle Management**
  - Support for different vehicle types (SMALL, MEDIUM, LARGE)
  - Vehicle registration and color tracking
  - Slot allocation based on vehicle type

- **Slot Management**
  - Dynamic slot allocation
  - Support for multiple vehicle types per slot
  - Capacity management for different vehicle types
    - SMALL: 4 vehicles per slot
    - MEDIUM: 2 vehicles per slot
    - LARGE: 1 vehicle per slot

- **Parking Operations**
  - Ticket generation for parked vehicles
  - Slot allocation based on proximity to entry point
  - Support for concurrent parking operations
  - Vehicle tracking by registration number and color

- **Payment System**
  - Base charges for first 2 hours
  - Additional charges for extra hours
  - Different rates for different vehicle types
    - SMALL: Base ₹20, Extra ₹10/hour
    - MEDIUM: Base ₹30, Extra ₹15/hour
    - LARGE: Base ₹40, Extra ₹20/hour

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/lokesh/parkinglot/
│   │       ├── bo/                 # Business Objects
│   │       ├── exception/          # Custom Exceptions
│   │       ├── manager/            # Business Logic Managers
│   │       ├── repository/         # Data Access Layer
│   │       └── service/            # Service Layer
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/lokesh/parkinglot/
            └── service/impl/       # Unit Tests
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Spring Boot 3.x

### Building the Project

```bash
mvn clean install
```

### Running the Application

```bash
mvn spring-boot:run
```

## API Usage

### Parking a Vehicle

```java
Vehicle vehicle = new Vehicle();
vehicle.setRegNo("ABC123");
vehicle.setColor("RED");
vehicle.setVehicleType(VehicleType.MEDIUM);

Location entryPoint = new SequenceBasedLocation(0);
Ticket ticket = ticketBookingService.bookTicket(vehicle, entryPoint);
```

### Freeing a Parking Spot

```java
Invoice invoice = ticketBookingService.freeUpParkingSpot(ticket);
```

### Finding Vehicles by Color

```java
List<Ticket> redVehicles = ticketBookingService.getParkedVehicleListByColor("RED");
```

### Finding Slot by Registration Number

```java
Integer slotId = ticketBookingService.getSlotByRegNumber("ABC123");
```

## Testing

The project includes comprehensive unit tests for:
- Payment calculation
- Slot allocation
- Vehicle parking and unparking
- Concurrent operations

Run tests using:

```bash
mvn test
```

## Design Patterns Used

- **Repository Pattern**: For data access
- **Service Layer Pattern**: For business logic
- **Factory Pattern**: For object creation
- **Strategy Pattern**: For payment calculation
- **Singleton Pattern**: For service instances

## Thread Safety

The system is designed to be thread-safe with:
- Concurrent collections for slot management
- Synchronized blocks for critical sections
- Atomic operations for counter updates

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 