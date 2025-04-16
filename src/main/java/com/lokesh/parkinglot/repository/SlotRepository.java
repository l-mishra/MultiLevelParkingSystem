package com.lokesh.parkinglot.repository;

import com.lokesh.parkinglot.bo.Location;
import com.lokesh.parkinglot.bo.Slot;
import com.lokesh.parkinglot.bo.Vehicle.VehicleType;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import org.antlr.v4.runtime.tree.Tree;
import org.springframework.stereotype.Repository;

@Repository
public class SlotRepository implements ISlotRepository<Slot> {

  // This class is an implementation of repository class.
  private List<Slot> slots;

  public List<Slot> getAllSlots() {
    return slots;
  }

  @Override
  public void init(List<Slot> slots) {
    this.slots = slots;
  }

}
