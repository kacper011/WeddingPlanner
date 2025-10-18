package com.kacper.wedding_planner.model;

public enum GuestCategory {
    GROOM_FAMILY("Rodzina Pana Młodego"),
    BRIDE_FAMILY("Rodzina Pani Młodej"),
    GROOM_FRIENDS("Znajomi Pana Młodego"),
    BRIDE_FRIENDS("Znajomi Pani Młodej"),
    FRIENDS("Znajomi");
    private final String displayName;

    GuestCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
