package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dto.Organization;
import com.sblinn.superherosightings.dto.Superhero;
import java.util.List;

/**
 *
 * @author Sara Blinn
 */
public interface OrganizationDao {
    
    Organization getOrganizationById(int id);
    
    List<Organization> getAllOrganizations();
    
    Organization createOrganization(Organization org);
    
    boolean updateOrganization(Organization updatedOrg);
    
    boolean deleteOrganizationById(int id);
    
    
    List<Superhero> getOrganizationMembers(int orgId);
    
    List<Organization> getOrganizationsForSuperhero(int superheroId);
    
    boolean addOrganizationMember(Organization organization,
            Superhero superhero);
    
    boolean deleteOrganizationMember(Organization organization,
            int superheroId);
    
}
