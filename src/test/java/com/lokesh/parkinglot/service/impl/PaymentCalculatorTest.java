package com.lokesh.parkinglot.service.impl;

import static com.lokesh.parkinglot.bo.Vehicle.VehicleType.LARGE;
import static com.lokesh.parkinglot.bo.Vehicle.VehicleType.MEDIUM;
import static com.lokesh.parkinglot.bo.Vehicle.VehicleType.SMALL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lokesh.parkinglot.bo.Vehicle.VehicleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PaymentCalculatorTest {

  private PaymentCalculator paymentCalculator;

  @BeforeEach
  void setUp() {
    paymentCalculator = new PaymentCalculator();
  }

  @Test
  void testBaseAmountForSmallVehicle() {
    // Test base amount for small vehicle (2 hours or less)
    double amount = paymentCalculator.calculatePayment(SMALL, 7200000); // 2 hours
    assertEquals(20.0, amount);
  }

  @Test
  void testBaseAmountForMediumVehicle() {
    // Test base amount for medium vehicle (2 hours or less)
    double amount = paymentCalculator.calculatePayment(MEDIUM, 7200000); // 2 hours
    assertEquals(30.0, amount);
  }

  @Test
  void testBaseAmountForLargeVehicle() {
    // Test base amount for large vehicle (2 hours or less)
    double amount = paymentCalculator.calculatePayment(LARGE, 7200000); // 2 hours
    assertEquals(40.0, amount);
  }

  @ParameterizedTest
  @CsvSource({
      "SMALL, 10800000, 30.0",    // 3 hours
      "SMALL, 14400000, 40.0",    // 4 hours
      "SMALL, 18000000, 50.0",    // 5 hours
      "MEDIUM, 10800000, 45.0",   // 3 hours
      "MEDIUM, 14400000, 60.0",   // 4 hours
      "MEDIUM, 18000000, 75.0",   // 5 hours
      "LARGE, 10800000, 60.0",    // 3 hours
      "LARGE, 14400000, 80.0",    // 4 hours
      "LARGE, 18000000, 100.0"    // 5 hours
  })
  void testExtraHoursCalculation(VehicleType vehicleType, long durationMs, double expectedAmount) {
    double amount = paymentCalculator.calculatePayment(vehicleType, durationMs);
    assertEquals(expectedAmount, amount);
  }

  @Test
  void testLessThanOneHour() {
    // Test for duration less than 1 hour
    double amount = paymentCalculator.calculatePayment(SMALL, 1800000); // 30 minutes
    assertEquals(20.0, amount); // Should still charge base amount
  }

  @Test
  void testJustOverTwoHours() {
    // Test for just over 2 hours
    double amount = paymentCalculator.calculatePayment(SMALL, 7200001); // 2 hours and 1 millisecond
    assertEquals(30.0, amount); // Should charge base + 1 extra hour
  }

  @Test
  void testLongDuration() {
    // Test for a very long duration (24 hours)
    double amount = paymentCalculator.calculatePayment(SMALL, 86400000); // 24 hours
    assertEquals(240.0, amount); // Base (20) + 22 extra hours * 10
  }

  @Test
  void testZeroDuration() {
    // Test for zero duration
    double amount = paymentCalculator.calculatePayment(SMALL, 0);
    assertEquals(20.0, amount); // Should still charge base amount
  }
} 