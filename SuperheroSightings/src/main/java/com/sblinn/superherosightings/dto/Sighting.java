
package com.sblinn.superherosightings.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Sara Blinn
 */
public class Sighting {

    private int id;
    
    @NotNull(message = "Location required.")
    private int location_id;
    
    @NotNull(message = "Superhero required.")
    private int superhero_id;
    
    private LocalDateTime date;

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public int getSuperhero_id() {
        return superhero_id;
    }

    public void setSuperhero_id(int superhero_id) {
        this.superhero_id = superhero_id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Formats and sets the Date field. 
     * NOTE parameter requirements: date parameter must have 
     * yyyy-MM-dd HH:mm (can lack seconds). 
     * 
     * @param date 
     */
    public void setDate(LocalDateTime date) {
        DateTimeFormatter formatter
                = DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd HH:mm:ss");
        String timeAsText = date.format(formatter);
        LocalDateTime formattedTime
                = LocalDateTime.parse(timeAsText, formatter);
        this.date = formattedTime;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.id;
        hash = 29 * hash + this.location_id;
        hash = 29 * hash + this.superhero_id;
        hash = 29 * hash + Objects.hashCode(this.date);
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
        final Sighting other = (Sighting) obj;
        if (this.id != other.id) {
            return false;
        }
        if (this.location_id != other.location_id) {
            return false;
        }
        if (this.superhero_id != other.superhero_id) {
            return false;
        }
        return Objects.equals(this.date, other.date);
    }

}
