
package com.sblinn.superherosightings.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import com.sblinn.superherosightings.dto.Location;
import com.sblinn.superherosightings.dto.Sighting;
import com.sblinn.superherosightings.dto.Superhero;

/**
 *
 * @author Sara Blinn
 */
public class SightingViewModel {
    
    private Sighting sighting;
    private String datetimeStr;
    private Superhero superhero;
    private Location location;

    
    public Sighting getSighting() {
        return sighting;
    }

    public void setSighting(Sighting sighting) {
        this.sighting = sighting;
    }

    public Superhero getSuperhero() {
        return superhero;
    }

    public void setSuperhero(Superhero superhero) {
        this.superhero = superhero;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDatetimeStr() {
        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd hh:mm a");
        LocalDateTime date = this.sighting.getDate();

        return date.format(formatter);
    }



    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.sighting);
        hash = 97 * hash + Objects.hashCode(this.datetimeStr);
        hash = 97 * hash + Objects.hashCode(this.superhero);
        hash = 97 * hash + Objects.hashCode(this.location);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SightingViewModel other = (SightingViewModel) obj;
        if (!Objects.equals(this.datetimeStr, other.datetimeStr)) {
            return false;
        }
        if (!Objects.equals(this.sighting, other.sighting)) {
            return false;
        }
        if (!Objects.equals(this.superhero, other.superhero)) {
            return false;
        }
        return Objects.equals(this.location, other.location);
    }

}