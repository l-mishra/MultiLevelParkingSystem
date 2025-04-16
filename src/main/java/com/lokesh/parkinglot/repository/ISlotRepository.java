package com.lokesh.parkinglot.repository;

import java.util.List;

public interface ISlotRepository<Slot> {

  List<Slot> getAllSlots();
  void init(List<Slot> slots);
}
