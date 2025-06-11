package com.kacper.wedding_planner.model;

public enum GuestCategory {
    RODZINA_PANA_MLODEGO("Rodzina Pana Młodego"),
    RODZINA_PANI_MLODEJ("Rodzina Pani Młodej"),
    ZNAJOMI_PANA_MLODEGO("Znajomi Pana Młodego"),
    ZNAJOMI_PANI_MLODEJ("Rodzina Pani Młodej"),
    ZNAJOMI("Znajomi");

    private final String displayName;

    GuestCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
