package com.lokesh.parkinglot.service;

import com.lokesh.parkinglot.bo.Vehicle.VehicleType;
import java.time.Duration;

public interface IPaymentCalculator {
  double calculatePayment(VehicleType vehicleType, long durationInMs);
}
