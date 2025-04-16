package com.lokesh.parkinglot.manager;

import static com.lokesh.parkinglot.bo.Vehicle.VehicleType.LARGE;
import static com.lokesh.parkinglot.bo.Vehicle.VehicleType.MEDIUM;
import static com.lokesh.parkinglot.bo.Vehicle.VehicleType.SMALL;
import static java.util.Objects.requireNonNull;

import com.lokesh.parkinglot.bo.Location;
import com.lokesh.parkinglot.bo.Slot;
import com.lokesh.parkinglot.bo.Vehicle.VehicleType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;
import org.springframework.stereotype.Service;

@Service
public class SlotManager {

  private ConcurrentSkipListMap<Location, Slot> smallAvailableSlots = new ConcurrentSkipListMap<>();
  private ConcurrentSkipListMap<Location, Slot> mediumAvailableSlots = new ConcurrentSkipListMap<>();
  private ConcurrentSkipListMap<Location, Slot> largeAvailableSlots = new ConcurrentSkipListMap<>();
  private ConcurrentSkipListMap<Integer, Slot> bookedSlots = new ConcurrentSkipListMap<>();

  public Slot allocateSlot(VehicleType vehicleType, Location location) {
    Slot pickedSlot = null;
    while (true) {
      Entry<Location, Slot> closestSlotEntry = getClosestSlotEntry(vehicleType, location);
      if (Objects.isNull(closestSlotEntry)) {
        return null;
      }
      pickedSlot = closestSlotEntry.getValue();
      synchronized (pickedSlot) {
        if (pickedSlot.isFullyOccupied()) {
          continue;
        }
        pickedSlot.incrementParkedCount();
        if (pickedSlot.getAllowedType() == null) {
          largeAvailableSlots.remove(closestSlotEntry.getKey());
          if (vehicleType == SMALL) {
            pickedSlot.setAllowedType(SMALL);
            smallAvailableSlots.put(closestSlotEntry.getKey(), closestSlotEntry.getValue());
          } else if (vehicleType == MEDIUM) {
            pickedSlot.setAllowedType(MEDIUM);
            mediumAvailableSlots.put(closestSlotEntry.getKey(), closestSlotEntry.getValue());
          } else {
            pickedSlot.setAllowedType(LARGE);
          }
        }
      }
      if (pickedSlot.isFullyOccupied()) {
        if (pickedSlot.getAllowedType() == SMALL) {
          smallAvailableSlots.remove(closestSlotEntry.getKey());
        } else if (pickedSlot.getAllowedType() == MEDIUM) {
          mediumAvailableSlots.remove(closestSlotEntry.getKey());
        } else {
          largeAvailableSlots.remove(closestSlotEntry.getKey());
        }
        bookedSlots.put(pickedSlot.getSlotId(), pickedSlot);
      }
    }
  }

  private Entry<Location, Slot> getClosestSlotEntry(VehicleType vehicleType, Location location) {

    List<Entry<Location, Slot>> slotEntries = new ArrayList<>();

    if (vehicleType == VehicleType.SMALL) {
      addIntoList(smallAvailableSlots.ceilingEntry(location), slotEntries);
      addIntoList(smallAvailableSlots.floorEntry(location), slotEntries);
    } else if (vehicleType == VehicleType.MEDIUM) {
      addIntoList(mediumAvailableSlots.ceilingEntry(location), slotEntries);
      addIntoList(mediumAvailableSlots.floorEntry(location), slotEntries);
    }
    addIntoList(largeAvailableSlots.ceilingEntry(location), slotEntries);
    addIntoList(smallAvailableSlots.floorEntry(location), slotEntries);

    Entry<Location, Slot> closestEntry = getClosestEntryByLocations(slotEntries, location);
    return closestEntry;
  }

  private void addIntoList(Entry<Location, Slot> slotEntry,
      List<Entry<Location, Slot>> slotEntries) {
    if (Objects.nonNull(slotEntry)) {
      slotEntries.add(slotEntry);
    }
  }

  private Entry<Location, Slot> getClosestEntryByLocations(List<Entry<Location, Slot>> slotsEntries,
      Location location) {
    Entry<Location, Slot> selectedEntry = null;
    int minDistance = Integer.MAX_VALUE;
    for (Entry<Location, Slot> currentEntry : slotsEntries) {
      int currDistance = getDistance(location, currentEntry.getKey());
      if (currDistance > minDistance) {
        minDistance = currDistance;
        selectedEntry = currentEntry;
      }
    }
    return selectedEntry;
  }

  private int getDistance(Location source, Location dest) {
    return Math.abs(source.getDistance() - dest.getDistance());
  }

  public void freeSlot(int slotId) {
    if (!bookedSlots.containsKey(slotId)) {
      System.out.println("slot is not booked");
      return;
    }
    Slot slot = bookedSlots.get(slotId);
    synchronized (slot) {
      slot.freeParkingSpace();
    }
    requireNonNull(slot);
    if (slot.isFree()) {
      slot.setAllowedType(null);
      largeAvailableSlots.put(slot.getLocation(), slot);
      bookedSlots.remove(slotId);
      return;
    }
    if (slot.getAllowedType() == SMALL) {
      smallAvailableSlots.put(slot.getLocation(), slot);
    } else if (slot.getAllowedType() == MEDIUM) {
      mediumAvailableSlots.put(slot.getLocation(), slot);
    }
  }
}
