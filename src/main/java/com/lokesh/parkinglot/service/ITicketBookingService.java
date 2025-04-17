package com.lokesh.parkinglot.service;

import com.lokesh.parkinglot.bo.Invoice;
import com.lokesh.parkinglot.bo.Location;
import com.lokesh.parkinglot.bo.Ticket;
import com.lokesh.parkinglot.bo.Vehicle;
import com.lokesh.parkinglot.bo.Vehicle.VehicleType;
import java.util.List;

public interface ITicketBookingService {

  Ticket bookTicket(Vehicle vehicle, Location location);
  Invoice freeUpParkingSpot(Ticket invoice);
  List<Ticket> getParkedVehicleListByColor(String color);
  Integer getSlotByRegNumber(String regNo);
  List<Integer> getSlotsByParkedVehicleColor(String color);
}
