
package com.sblinn.superherosightings.dto;

import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * @author Sara Blinn
 */
public class Location {

    private int id;
    
    @NotBlank(message = "Name cannot be empty.")
    @Size(max = 50, message = "Name must be less than 50 characters.")
    private String name;
    
    @Size(min = 0, max = 50, message = "Address must be less than 50 characters.")
    private String street_address;
    
    @NotBlank(message = "City cannot be empty.")
    @Size(max = 50, message = "City must be less than 50 characters.")
    @Pattern(regexp = "^[a-zA-Z ']*$")
    private String city;
    
    @Size(min = 0, max = 2, message = "State must be 2 characters.")
    @Pattern(regexp = "^[a-zA-Z]*$")
    private String state;
    
    @NotBlank(message = "Country cannot be empty.")
    @Size(max = 2, message = "Country must be 2 characters.")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Country name can only contain alphabet characters.")
    private String country; 
    
    @NotNull(message = "Longitude cannot be empty.")
    @DecimalMin(value = "-180.000000", inclusive = true)
    @DecimalMax(value = "180.000000", inclusive = true)
    private BigDecimal longitude;
    
    @NotNull(message = "Latitude cannot be empty.")
    @DecimalMin(value = "-90.000000", inclusive = true)
    @DecimalMax(value = "90.000000", inclusive = true)
    private BigDecimal latitude;
    
    @NotBlank(message = "Description cannot be empty.")
    @Size(max = 100, message = "Description must be less than 100 characters.")
    private String description;

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet_address() {
        return street_address;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        String lngStr = longitude.toString();
        if (!lngStr.contains(".")) {
            lngStr.concat(".000000");
        }

        longitude = new BigDecimal(lngStr);
        this.longitude = longitude;
    }
    
    public void setLongitude(String longitude) {
        if (!longitude.contains(".")) {
            longitude.concat(".000000");
        }
        this.longitude = new BigDecimal(longitude);
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        String latStr = latitude.toString();
        if (!latStr.contains(".")) {
            latStr.concat(".000000");
        }
        
        latitude = new BigDecimal(latStr);
        this.latitude = latitude;
    }

    public void setLatitude(String latitude) {
        if (!latitude.contains(".")) {
            latitude.concat(".000000");
        }
        this.longitude = new BigDecimal(latitude);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + this.id;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.street_address);
        hash = 29 * hash + Objects.hashCode(this.city);
        hash = 29 * hash + Objects.hashCode(this.state);
        hash = 29 * hash + Objects.hashCode(this.country);
        hash = 29 * hash + Objects.hashCode(this.longitude);
        hash = 29 * hash + Objects.hashCode(this.latitude);
        hash = 29 * hash + Objects.hashCode(this.description);
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
        final Location other = (Location) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.street_address, other.street_address)) {
            return false;
        }
        if (!Objects.equals(this.city, other.city)) {
            return false;
        }
        if (!Objects.equals(this.state, other.state)) {
            return false;
        }
        if (!Objects.equals(this.country, other.country)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (this.longitude.compareTo(other.longitude) != 0) {
            return false;
        }
        return (this.latitude.compareTo(other.latitude) == 0);
    }
    
}
