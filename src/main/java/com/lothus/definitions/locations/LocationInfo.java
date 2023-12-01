package com.lothus.definitions.locations;

import com.lothus.definitions.locations.type.LocationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter @Setter
@AllArgsConstructor
public class LocationInfo {

    private Location location;
    private LocationType locationType;

}
