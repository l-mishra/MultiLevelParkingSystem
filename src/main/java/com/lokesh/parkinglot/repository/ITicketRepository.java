package com.lokesh.parkinglot.repository;

import com.lokesh.parkinglot.bo.Ticket;

public interface ITicketRepository {

  void saveTicket(Ticket ticket);

  Ticket getTicketById(String ticketId);
}
