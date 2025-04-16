package com.lokesh.parkinglot.bo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Vehicle {

  private String id;
  private String regNo;
  private String color;
  private VehicleType vehicleType;

  public static enum VehicleType {
    SMALL,  // BIKE
    MEDIUM, // CAR
    LARGE;// TRUCK/BUS

    public static VehicleType getVehicleTypeFromString(String name) {
      for (VehicleType vehicleType : VehicleType.values()) {
        if (vehicleType.name().equals(name)) {
          return vehicleType;
        }
      }
      throw new IllegalArgumentException("invalid vehicleType supplied");
    }

  }
}
