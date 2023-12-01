package com.lothus.definitions.locations.type;

public enum LocationType {

    SPAWN,

    NPC_SKYWARS,
    NPC_BEDWARS,
    NPC_TRAINING,

    NPC_SOLO,
    NPC_TEAM,
    NPC_TRIO,
    NPC_QUARTETO,

    NPC_STATS,

    HOLOGRAM_TOP_KILLS,
    HOLOGRAM_TOP_WINS,
    HOLOGRAM_TOP_LEVEL,
    HOLOGRAM_TOP_RANK;


    public static LocationType getByName(String name) {
        for (LocationType locationType : values()) {
            if (locationType.name().equalsIgnoreCase(name)) {
                return locationType;
            }
        }
        return null;
    }
}
