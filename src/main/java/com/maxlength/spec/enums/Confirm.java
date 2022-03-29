package com.maxlength.spec.enums;

public enum Confirm {
    TRANSFER(1),
    MINT(2),
    BURN(3),
    ADD(4),
    REMOVE(5),
    PAUSESTART(6),
    PAUSESSTOP(7),
    BLACKLISTSTART(8),
    BLACKLISTSTOP(9),
    LOCKUPSTART(10),
    LOCKUPSTOP(11),
    TRANSFEROWNERSHIP(12);

    private final int confirmValue;

    Confirm(int confirmValue) {
        this.confirmValue = confirmValue;
    }

    public int getConfirmValue() {
        return confirmValue;
    }
}
