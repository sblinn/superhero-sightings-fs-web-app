package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dto.Location;
import java.util.List;

/**
 *
 * @author Sara Blinn
 */
public interface LocationDao {
    
    Location getLocationById(int id);
    
    List<Location> getAllLocations();
    
    Location createLocation(Location location);
    
    boolean updateLocation(Location updatedLocation);
    
    boolean deleteLocationById(int id);
    
}
