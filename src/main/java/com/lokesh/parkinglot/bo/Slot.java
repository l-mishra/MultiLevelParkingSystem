package com.lokesh.parkinglot.bo;

import com.lokesh.parkinglot.bo.Vehicle.VehicleType;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class Slot {

  private int slotId;
  private int level;
  private Location<?> location;
  private List<Slot> slotList;
  // Used when vehicle
  private VehicleType allowedType;
  private AtomicInteger parkedCount;

  public enum SlotType {
    NORMAL,
    ENTRY_EXIT
  }

  public void incrementParkedCount() {
    parkedCount.incrementAndGet();
  }

  public void freeParkingSpace() {
    parkedCount.decrementAndGet();
  }

  public boolean isFree() {
    return parkedCount.get() == 0;
  }

  public boolean isFullyOccupied() {
    if(allowedType == null){
      return false;
    }
    return switch (allowedType) {
      case SMALL -> parkedCount.get() == 4;
      case MEDIUM -> parkedCount.get() == 2;
      case LARGE -> parkedCount.get() == 1;
    };
  }
}
