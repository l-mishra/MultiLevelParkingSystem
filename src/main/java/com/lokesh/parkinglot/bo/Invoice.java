package com.lokesh.parkinglot.bo;

import java.time.Duration;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class Invoice {

  private String id;
  private Duration parkingTime;
  private long parkedDatetime;
  private long invoiceGenerationDatetime;
  private double amount;
}
