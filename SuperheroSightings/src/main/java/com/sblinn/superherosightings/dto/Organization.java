
package com.sblinn.superherosightings.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * @author Sara Blinn
 */
public class Organization {

    private int id;
    
    @NotBlank(message = "Name cannot be empty.")
    @Size(max = 50, message = "Name must be less than 50 characters.")
    private String name;
    
    @Size(min = 0, max = 100, message = "Description must be less than 100 characters.")
    private String description;
    
    @Size(min = 0, max = 50, message = "Address must be less than 50 characters.")
    private String street_address;
    
    @NotBlank(message = "City cannot be empty.")
    @Size(max = 50, message = "City must be less than 50 characters.")
    @Pattern(regexp = "^[a-zA-Z ']*$", message = "City name can only contain alphabet characters.")
    private String city;
    
    @NotBlank(message = "Country cannot be empty.")
    @Size(max = 2, message = "Country must be less than 2 characters.")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "Country name can only contain alphabet characters.")
    private String country;
    
    private List<Superhero> members = new ArrayList<>();

    
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Superhero> getMembers() {
        return members;
    }

    public void setMembers(List<Superhero> members) {
        this.members = members;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + this.id;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + Objects.hashCode(this.description);
        hash = 97 * hash + Objects.hashCode(this.street_address);
        hash = 97 * hash + Objects.hashCode(this.city);
        hash = 97 * hash + Objects.hashCode(this.country);
        hash = 97 * hash + Objects.hashCode(this.members);
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
        final Organization other = (Organization) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.street_address, other.street_address)) {
            return false;
        }
        if (!Objects.equals(this.city, other.city)) {
            return false;
        }
        if (!Objects.equals(this.country, other.country)) {
            return false;
        }
        return Objects.equals(this.members, other.members);
    }
    
}
