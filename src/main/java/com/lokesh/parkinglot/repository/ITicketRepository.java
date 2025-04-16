package com.lokesh.parkinglot.repository;

import com.lokesh.parkinglot.bo.Ticket;
import java.util.List;

public interface ITicketRepository {

  void saveTicket(Ticket ticket);

  Ticket getTicketById(String ticketId);

  List<Ticket> getTicketsByStatus(String ticketStatus);
}
