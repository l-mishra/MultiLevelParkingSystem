package com.lokesh.parkinglot.service;

import com.lokesh.parkinglot.bo.Invoice;
import com.lokesh.parkinglot.bo.Location;
import com.lokesh.parkinglot.bo.Ticket;
import com.lokesh.parkinglot.bo.Vehicle;
import com.lokesh.parkinglot.bo.Vehicle.VehicleType;

public interface ITicketBookingService {

  Ticket bookTicket(Vehicle vehicle, Location location);
  Invoice freeUpParkingSpot(Ticket invoice);
}
