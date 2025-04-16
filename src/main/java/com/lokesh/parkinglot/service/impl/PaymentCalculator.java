package com.lokesh.parkinglot.service.impl;

import static com.lokesh.parkinglot.bo.Vehicle.VehicleType.LARGE;
import static com.lokesh.parkinglot.bo.Vehicle.VehicleType.MEDIUM;
import static com.lokesh.parkinglot.bo.Vehicle.VehicleType.SMALL;

import com.lokesh.parkinglot.bo.Vehicle.VehicleType;
import com.lokesh.parkinglot.service.IPaymentCalculator;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PaymentCalculator implements IPaymentCalculator {

  private static final double BASE_SMALL_AMOUNT = 20;
  private static final double DELTA_SMALL_AMOUNT = 10;

  private static final double BASE_MEDIUM_AMOUNT = 30;
  private static final double DELTA_MEDIUM_AMOUNT = 15;

  private static final double BASE_LARGE_AMOUNT = 40;
  private static final double DELTA_LARGE_AMOUNT = 20;

  private static final int BASE_HOURS = 2;

  private static final long MILLISECONDS_IN_HOURS = 3600000;

  private static final Map<VehicleType, Double> baseAmount = Map.of(SMALL, BASE_SMALL_AMOUNT,
      MEDIUM, BASE_MEDIUM_AMOUNT, LARGE, BASE_LARGE_AMOUNT);

  private static final Map<VehicleType, Double> extraPerHourAmounts = Map.of(SMALL,
      DELTA_SMALL_AMOUNT,
      MEDIUM, DELTA_MEDIUM_AMOUNT, LARGE, DELTA_LARGE_AMOUNT);


  @Override
  public double calculatePayment(VehicleType vehicleType, long durationInMs) {
    double amount = baseAmount.get(vehicleType);
    int hours = calculateDeltaHours(durationInMs);
    if (hours > BASE_HOURS) {
      amount += (hours - BASE_HOURS) * extraPerHourAmounts.get(vehicleType);
    }
    return amount;
  }

  public int calculateDeltaHours(long durationInMs) {
    return (int) (durationInMs / MILLISECONDS_IN_HOURS) + (int) (durationInMs
        % MILLISECONDS_IN_HOURS);
  }

}
