package com.lokesh.parkinglot.bo;

import lombok.Data;

@Data
public class Ticket {

  private long slotTime;
  private String id;
  private String vehicleId;
  private int slotId;
  private String bookingStatus; // PARKED, CLOSED
  private String vehicleColor;
  private String regNumber;
  private String vehicleType;
  private double amount;

}
