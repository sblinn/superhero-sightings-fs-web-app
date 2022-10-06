
package com.sblinn.superherosightings.controller;

import com.sblinn.superherosightings.dto.Organization;
import com.sblinn.superherosightings.dto.Superhero;
import com.sblinn.superherosightings.dto.Superpower;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Sara Blinn
 */
public class SuperheroViewModel {

    private Superhero superhero = new Superhero();
    private List<Superpower> superpowers = new ArrayList<>();
    private List<Organization> organizations = new ArrayList<>();
    
    public SuperheroViewModel() {
        
    }

    public Superhero getSuperhero() {
        return superhero;
    }

    public void setSuperhero(Superhero superhero) {
        this.superhero = superhero;
    }

    public List<Superpower> getSuperpowers() {
        return superpowers;
    }

    public void setSuperpowers(List<Superpower> superpowers) {
        this.superpowers = superpowers;
    }

    public List<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<Organization> organizations) {
        this.organizations = organizations;
    }

    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.superhero);
        hash = 59 * hash + Objects.hashCode(this.superpowers);
        hash = 59 * hash + Objects.hashCode(this.organizations);
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
        final SuperheroViewModel other = (SuperheroViewModel) obj;
        if (!Objects.equals(this.superhero, other.superhero)) {
            return false;
        }
        if (!Objects.equals(this.superpowers, other.superpowers)) {
            return false;
        }
        return Objects.equals(this.organizations, other.organizations);
    }
    
    
}
