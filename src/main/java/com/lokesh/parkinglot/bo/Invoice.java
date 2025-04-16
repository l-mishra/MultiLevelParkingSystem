package com.lokesh.parkinglot.bo;

import java.time.Duration;
import lombok.Data;

@Data
public class Invoice {

  private String id;
  private Duration parkingTime;
  private long parkedDatetime;
  private long invoiceGenerationDatetime;
  private double amount;
}
