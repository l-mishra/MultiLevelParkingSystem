package com.lokesh.parkinglot.repository;

import static java.util.stream.Collectors.toList;

import com.lokesh.parkinglot.bo.Ticket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class TicketRepository implements ITicketRepository {

  private Map<String, Ticket> tickets = new HashMap<>();

  @Override
  public void saveTicket(Ticket ticket) {
    tickets.put(ticket.getId(), ticket);
  }

  @Override
  public Ticket getTicketById(String ticketId) {
    return tickets.get(ticketId);
  }

  @Override
  public List<Ticket> getTicketsByStatus(String ticketStatus) {
    return tickets.values().stream()
        .filter(ticket -> ticket.getBookingStatus().equals(ticketStatus)).collect(toList());
  }

  @Override
  public void reset(){
    tickets = new HashMap<>();
  }


}
