package com.lokesh.parkinglot.bo;

public record SequenceBasedLocation(int sequenceNo) implements Location<SequenceBasedLocation> {

  @Override
  public int compareTo(SequenceBasedLocation o) {
    return this.sequenceNo - o.sequenceNo;
  }

  @Override
  public int getDistance() {
    return 0;
  }
}
