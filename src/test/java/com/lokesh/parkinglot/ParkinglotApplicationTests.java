package com.lokesh.parkinglot;

import static com.lokesh.parkinglot.bo.Vehicle.VehicleType.LARGE;
import static com.lokesh.parkinglot.bo.Vehicle.VehicleType.MEDIUM;
import static com.lokesh.parkinglot.bo.Vehicle.VehicleType.SMALL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lokesh.parkinglot.bo.Location;
import com.lokesh.parkinglot.bo.SequenceBasedLocation;
import com.lokesh.parkinglot.bo.Slot;
import com.lokesh.parkinglot.bo.Ticket;
import com.lokesh.parkinglot.bo.Vehicle;
import com.lokesh.parkinglot.bo.Vehicle.VehicleType;
import com.lokesh.parkinglot.exception.SlotUnAvailableException;
import com.lokesh.parkinglot.manager.SlotManager;
import com.lokesh.parkinglot.repository.SlotRepository;
import com.lokesh.parkinglot.repository.TicketRepository;
import com.lokesh.parkinglot.service.ITicketBookingService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ParkinglotApplicationTests {

  @Autowired
  private SlotManager slotManager;

  @Autowired
  private ITicketBookingService ticketBookingService;

  @Autowired
  private SlotRepository slotRepository;

  @Autowired
  private TicketRepository ticketRepository;

  public void init(int capacity) {
    List<Slot> testSlots = new ArrayList<>();
    // Create 10 test slots
    for (int i = 1; i <= capacity; i++) {
      Location location = new SequenceBasedLocation(i);
      Slot slot = Slot.builder()
          .slotId(i)
          .level(1)
          .location(location)
          .parkedCount(new java.util.concurrent.atomic.AtomicInteger(0))
          .build();
      testSlots.add(slot);
    }
    slotRepository.init(testSlots);
    slotManager.init();
  }

  @AfterEach
  void tearDown() {
    // Clear all slots after each test
    slotManager.reset();
    slotRepository.getAllSlots().clear();
    ticketRepository.reset();
  }

  @Test
  void test_ParkingLot_Initialisation() {
    assertNotNull(slotRepository);
    assertNotNull(slotManager);
    assertNotNull(ticketBookingService);
  }

  @Test
  void test_ParkingAnd_Unparking() {
    init(10);
    Vehicle car = new Vehicle();
    car.setRegNo("ABC123");
    car.setColor("Red");
    car.setVehicleType(MEDIUM);
    Location entryPoint = new SequenceBasedLocation(0);
    Ticket ticket = ticketBookingService.bookTicket(car, entryPoint);
    assertNotNull(ticket);
    assertEquals(car.getColor(), ticket.getVehicleColor());
    assertEquals(MEDIUM.name(), ticket.getVehicleType());
  }

  @Test
  void test_Vehicle_Capacilty_Per_Slot() {
    init(1);
    Location entryPoint = new SequenceBasedLocation(0);
    for (int i = 0; i <= 1; i++) {
      Vehicle car = new Vehicle();
      String regNo = "ABC123" + i;
      car.setRegNo(regNo);
      car.setColor("Red");
      car.setVehicleType(MEDIUM);
      Ticket ticket = ticketBookingService.bookTicket(car, entryPoint);
      assertNotNull(ticket);
      assertEquals(car.getColor(), ticket.getVehicleColor());
      assertEquals(MEDIUM.name(), ticket.getVehicleType());
    }
    Vehicle car = new Vehicle();
    String regNo = "ABC123" + 3;
    car.setRegNo(regNo);
    car.setColor("Red");
    car.setVehicleType(MEDIUM);
    assertThrows(SlotUnAvailableException.class,
        () -> ticketBookingService.bookTicket(car, entryPoint));

  }

  @Test
  void testConcurrentParking() throws InterruptedException {
    init(10);
    int noOfThread = 10;
    String regNo = "ABC";
    String[] color = {"RED", "BLUE", "GREEN"};
    VehicleType[] vehicleTypes = {SMALL, MEDIUM, LARGE};
    Random random = new Random();
    ExecutorService executorService = Executors.newFixedThreadPool(noOfThread);
    CountDownLatch latch = new CountDownLatch(noOfThread);
    for (int i = 0; i < noOfThread; i++) {
      final int index = i;
      executorService.submit(() -> {
        try {
          Vehicle car = new Vehicle();
          car.setColor(color[random.nextInt(3)]);
          car.setVehicleType(vehicleTypes[random.nextInt(3)]);
          car.setRegNo(regNo + index);
          Location entryPoint = new SequenceBasedLocation(0);
          Ticket ticket = ticketBookingService.bookTicket(car, entryPoint);
          assertNotNull(ticket);
        } finally {
          latch.countDown();
        }
      });
    }
    assertTrue(latch.await(5, TimeUnit.SECONDS));
    executorService.shutdown();
    System.out.println(slotRepository.getAllSlots());
  }

  @Test
  void testListParkedVehiclesByColor() throws InterruptedException {
    init(10);
    int noOfThread = 10;
    String regNo = "ABC";
    String[] color = {"RED", "BLUE", "GREEN"};
    VehicleType[] vehicleTypes = {SMALL, MEDIUM, LARGE};
    Random random = new Random();
    ExecutorService executorService = Executors.newFixedThreadPool(noOfThread);
    CountDownLatch latch = new CountDownLatch(noOfThread);
    List<Vehicle> vehicleList = Collections.synchronizedList(new ArrayList<>());
    for (int i = 0; i < noOfThread; i++) {
      final int index = i;
      executorService.submit(() -> {
        try {
          Vehicle vehicle = new Vehicle();
          String vehicleColor = color[random.nextInt(3)];
          vehicle.setColor(vehicleColor);
          vehicle.setVehicleType(vehicleTypes[random.nextInt(3)]);
          vehicle.setRegNo(regNo + index);
          synchronized (vehicleList) {
            vehicleList.add(vehicle);
          }
          Location entryPoint = new SequenceBasedLocation(0);
          Ticket ticket = ticketBookingService.bookTicket(vehicle, entryPoint);
          assertNotNull(ticket);
        } finally {
          latch.countDown();
        }
      });
    }
    assertTrue(latch.await(5, TimeUnit.SECONDS));
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.SECONDS); // Wait for all tasks to complete
    long expectedRedCount = vehicleList.stream()
        .filter(vehicle -> "RED".equals(vehicle.getColor()))
        .count();
    long actualRedCount = ticketBookingService.getParkedVehicleListByColor("RED").stream().count();
    assertEquals(expectedRedCount, actualRedCount, 
        "Expected " + expectedRedCount + " RED vehicles but found " + actualRedCount);
  }

  @Test
  void testFreeSlot(){
    init(1);
    Vehicle car = new Vehicle();
    String regNo = "ABC123";
    car.setRegNo(regNo);
    car.setVehicleType(MEDIUM);
    car.setColor("RED");
    Ticket ticket = ticketBookingService.bookTicket(car, new SequenceBasedLocation(0));
    assertNotNull(ticket);
    ticketBookingService.freeUpParkingSpot(ticket);
    assertTrue(slotRepository.getAllSlots().getFirst().isFree());
  }

  @Test
  void testSlotByVehicleRegNumber(){
    init(1);
    Vehicle car = new Vehicle();
    String regNo = "ABC123";
    car.setRegNo(regNo);
    car.setVehicleType(MEDIUM);
    car.setColor("RED");
    Ticket ticket = ticketBookingService.bookTicket(car, new SequenceBasedLocation(0));
    assertNotNull(ticket);
    assertEquals(ticket.getSlotId(), ticketBookingService.getSlotByRegNumber(car.getRegNo()));
  }

  @Test
  void testSlotsOfParkedVehicleColor() throws InterruptedException {
    init(10);
    int noOfThread = 10;
    String regNo = "ABC";
    String[] color = {"RED", "BLUE", "GREEN"};
    VehicleType[] vehicleTypes = {SMALL, MEDIUM, LARGE};
    Random random = new Random();
    ExecutorService executorService = Executors.newFixedThreadPool(noOfThread);
    CountDownLatch latch = new CountDownLatch(noOfThread);
    List<Vehicle> vehicleList = Collections.synchronizedList(new ArrayList<>());
    List<Integer> bookedSlotIds = Collections.synchronizedList(new ArrayList<>());
    List<String> vehicleColors = Collections.synchronizedList(new ArrayList<>());
    for (int i = 0; i < noOfThread; i++) {
      final int index = i;
      executorService.submit(() -> {
        try {
          Vehicle vehicle = new Vehicle();
          String vehicleColor = color[random.nextInt(3)];
          vehicle.setColor(vehicleColor);
          vehicle.setVehicleType(vehicleTypes[random.nextInt(3)]);
          vehicle.setRegNo(regNo + index);
          synchronized (vehicleList) {
            vehicleList.add(vehicle);
          }
          Location entryPoint = new SequenceBasedLocation(0);
          Ticket ticket = ticketBookingService.bookTicket(vehicle, entryPoint);
          assertNotNull(ticket);
          synchronized (bookedSlotIds) {
            bookedSlotIds.add(ticket.getSlotId());
            vehicleColors.add(vehicleColor);
          }
        } finally {
          latch.countDown();
        }
      });
    }
    assertTrue(latch.await(5, TimeUnit.SECONDS));
    executorService.shutdown();
    executorService.awaitTermination(1, TimeUnit.SECONDS); // Wait for all tasks to complete
    
    // Filter bookedSlotIds to only include RED vehicles
    List<Integer> expectedRedSlotIds = new ArrayList<>();
    for (int i = 0; i < bookedSlotIds.size(); i++) {
      if ("RED".equals(vehicleColors.get(i))) {
        expectedRedSlotIds.add(bookedSlotIds.get(i));
      }
    }
    
    List<Integer> actualRedSlotIds = ticketBookingService.getSlotsByParkedVehicleColor("RED");
    assertEquals(expectedRedSlotIds.size(), actualRedSlotIds.size(),
        "Expected " + expectedRedSlotIds.size() + " RED vehicles but found " + actualRedSlotIds.size());
    assertTrue(actualRedSlotIds.containsAll(expectedRedSlotIds),
        "Expected slots " + expectedRedSlotIds + " for RED vehicles but found " + actualRedSlotIds);
  }
}
