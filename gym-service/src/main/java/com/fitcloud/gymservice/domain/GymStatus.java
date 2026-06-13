package com.fitcloud.gymservice.domain;

public enum GymStatus {
    ACTIVE,         // gym is open and accepting members
    DEACTIVATED,       // gym temporarily closed
    SUSPENDED       // suspended by admin for violations
}
