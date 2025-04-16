package com.lokesh.parkinglot.bo;

public interface Location<T extends Location> extends Comparable<T> {
  int getDistance();
}
