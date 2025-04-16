package com.lokesh.parkinglot.service.impl;

import com.lokesh.parkinglot.bo.Invoice;
import com.lokesh.parkinglot.bo.Location;
import com.lokesh.parkinglot.bo.Slot;
import com.lokesh.parkinglot.bo.Ticket;
import com.lokesh.parkinglot.bo.Vehicle;
import com.lokesh.parkinglot.bo.Vehicle.VehicleType;
import com.lokesh.parkinglot.manager.SlotManager;
import com.lokesh.parkinglot.repository.IInvoiceRepository;
import com.lokesh.parkinglot.repository.ITicketRepository;
import com.lokesh.parkinglot.service.IPaymentCalculator;
import com.lokesh.parkinglot.service.ITicketBookingService;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketBookingService implements ITicketBookingService {

  private final SlotManager slotManager;
  private final IPaymentCalculator paymentCalculator;
  private final ITicketRepository ticketRepository;
  private final IInvoiceRepository invoiceRepository;

  @Autowired
  public TicketBookingService(SlotManager slotManager, IPaymentCalculator paymentCalculator,
      ITicketRepository ticketRepository, IInvoiceRepository invoiceRepository) {
    this.slotManager = slotManager;
    this.paymentCalculator = paymentCalculator;
    this.ticketRepository = ticketRepository;
    this.invoiceRepository = invoiceRepository;
  }

  @Override
  public Ticket bookTicket(Vehicle vehicle, Location location) {
    Slot slot = slotManager.allocateSlot(vehicle.getVehicleType(), location);
    Ticket ticket = new Ticket();
    ticket.setSlotTime(System.currentTimeMillis());
    ticket.setId(UUID.randomUUID().toString());
    ticket.setSlotId(slot.getSlotId());
    ticket.setVehicleColor(vehicle.getColor());
    ticket.setRegNumber(vehicle.getRegNo());
    ticket.setBookingStatus("INPROG");
    ticket.setVehicleType(vehicle.getVehicleType().name());
    ticketRepository.saveTicket(ticket);
    return ticket;

  }

  @Override
  public Invoice freeUpParkingSpot(Ticket ticket) {
    slotManager.freeSlot(ticket.getSlotId());
    double amount = paymentCalculator.calculatePayment(
        VehicleType.getVehicleTypeFromString(ticket.getVehicleType()),
        System.currentTimeMillis() - ticket.getSlotTime());
    Invoice invoice = new Invoice();
    invoice.setId(UUID.randomUUID().toString());
    invoice.setParkedDatetime(ticket.getSlotTime());
    invoice.setInvoiceGenerationDatetime(System.currentTimeMillis());
    invoice.setAmount(amount);
    invoiceRepository.saveInvoice(invoice);
    return invoice;
  }
}
