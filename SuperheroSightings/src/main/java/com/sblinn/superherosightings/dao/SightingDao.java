package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dto.Location;
import com.sblinn.superherosightings.dto.Sighting;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Sara Blinn
 */
public interface SightingDao {
 
    Sighting getSightingById(int id);
    
    List<Sighting> getAllSightingsAtLocation(int locationId);
    
    List<Sighting> getAllSightingsOnDate(LocalDate date);
    
    List<Location> getAllSightingLocationsForSuperhero(int superheroId);
    
    List<Sighting> getAllSightings();
    
    Sighting createSighting(Sighting sighting);
    
    boolean updateSighting(Sighting updatedSighting);
    
    boolean deleteSightingById(int id);
    
}
