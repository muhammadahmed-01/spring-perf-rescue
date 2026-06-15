package com.muhammadahmed.perf.dto;

public record QueryStatsResponse(long queryCount, int orderCount, String mode) {
}
