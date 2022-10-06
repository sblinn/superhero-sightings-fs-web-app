
package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dao.SuperheroDaoDB.SuperheroMapper;
import com.sblinn.superherosightings.dto.Organization;
import com.sblinn.superherosightings.dto.Superhero;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 * @author Sara Blinn
 */
@Repository
@Profile("database")
public class OrganizationDaoDB implements OrganizationDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    
    @Override
    @Transactional
    public Organization getOrganizationById(int id) {
        try {
            final String GET_ORG
                    = "SELECT * FROM `Organization` "
                    + "WHERE id = ?;";
            Organization retrievedOrg = jdbcTemplate.queryForObject(
                    GET_ORG, new OrganizationMapper(), id);
            
            retrievedOrg.setMembers(getOrganizationMembers(id));
            
            return retrievedOrg;
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public List<Organization> getAllOrganizations() {
        final String SELECT_ALL
                = "SELECT * FROM `Organization` "
                + "ORDER BY id;";
        List<Organization> orgs = jdbcTemplate.query(
                SELECT_ALL, new OrganizationMapper());
        
        getMembersForOrganizations(orgs);
        
        return orgs;
    }

    @Override
    @Transactional
    public Organization createOrganization(Organization org) {
        
        if (org.getId() != 0) {
            final String INSERT_ORG
                    = "INSERT INTO `Organization` "
                    + "(id, `name`, `description`, street_address, "
                    + "city, country) "
                    + "VALUES(?,?,?,?,?,?);";
            
            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement statement = conn.prepareStatement(
                        INSERT_ORG, Statement.NO_GENERATED_KEYS);
                statement.setInt(1, org.getId());
                statement.setString(2, org.getName());
                statement.setString(3, org.getDescription());
                statement.setString(4, org.getStreet_address());
                statement.setString(5, org.getCity());
                statement.setString(6, org.getCountry());
                return statement;
            });
        } else {
            final String INSERT_ORG_NO_ID
                    = "INSERT INTO `Organization` "
                    + "(`name`, `description`, street_address, "
                    + "city, country) "
                    + "VALUES(?,?,?,?,?);";
            GeneratedKeyHolder keyholder = new GeneratedKeyHolder();
            
            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement statement = conn.prepareStatement(
                    INSERT_ORG_NO_ID, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, org.getName());
                statement.setString(2, org.getDescription());
                statement.setString(3, org.getStreet_address());
                statement.setString(4, org.getCity());
                statement.setString(5, org.getCountry());
                return statement;
            }, keyholder);
            
            org.setId(keyholder.getKey().intValue());
        }
        
        if (!org.getMembers().isEmpty()) {
            addMembersForOrganization(org.getId(),
                    org.getMembers());
        }
        
        return org;
    }

    @Override
    @Transactional
    public boolean updateOrganization(Organization updatedOrg) {
        final String UPDATE_ORG
                = "UPDATE `Organization` "
                + "SET id = ?, `name` = ?, `description` = ?, "
                + "street_address = ?, city = ?, country = ? "
                + "WHERE id = ?;";
        int numRowsUpdated = jdbcTemplate.update(
                UPDATE_ORG,
                updatedOrg.getId(),
                updatedOrg.getName(),
                updatedOrg.getDescription(),
                updatedOrg.getStreet_address(),
                updatedOrg.getCity(),
                updatedOrg.getCountry(),
                updatedOrg.getId());
        
        // delete and update rows of bridge table with new values
        deleteMembersForOrganization(updatedOrg.getId());
        
        if (!updatedOrg.getMembers().isEmpty()) {
            addMembersForOrganization(updatedOrg.getId(), 
                    updatedOrg.getMembers());
        }
        
        return numRowsUpdated == 1;
    }

    @Override
    @Transactional
    public boolean deleteOrganizationById(int id) {
        // must delete reference in Organization_Superhero
        final String DELETE_ORG_SUPERHERO
                = "DELETE FROM Organization_Superhero "
                + "WHERE org_id = ?;";
        jdbcTemplate.update(DELETE_ORG_SUPERHERO, id);
        
        final String DELETE_ORG
                = "DELETE FROM `Organization` "
                + "WHERE id = ?;";
        int numRowsDeleted = jdbcTemplate.update(DELETE_ORG, id);
        
        return numRowsDeleted == 1;
    }
    
    
    // METHODS FOR HANDLING ORGANIZATION MEMBERS/SUPERHEROS
    
    @Override
    public List<Organization> getOrganizationsForSuperhero(
            int superheroId) {

        try {
            final String GET_ORGANIZATIONS
                    = "SELECT org.id, " 
                    + "org.`name`, "
                    + "org.`description`, "
                    + "org.street_address, " 
                    + "org.city, "
                    + "org.country "
                    + "FROM `Organization` org "
                    + "LEFT OUTER JOIN Organization_Superhero members "
                    + "ON org.id = members.org_id "
                    + "LEFT OUTER JOIN Superhero "
                    + "ON Superhero.id = members.superhero_id "
                    + "WHERE Superhero.id = ?;";
            
            List<Organization> orgs = jdbcTemplate.query(
                    GET_ORGANIZATIONS, new OrganizationMapper(), 
                    superheroId);
            
            for (Organization org : orgs) {
                org.setMembers(getOrganizationMembers(org.getId()));
            }
            
            return orgs;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Returns a list of Superheros affiliated with a given 
     * Organization in the bridge table.
     * @param orgId
     * @return members ArrayList
     */
    @Override
    public List<Superhero> getOrganizationMembers(int orgId) {
        try {
            final String GET_MEMBERS
                    = "SELECT "
                    + "Superhero.id, "
                    + "Superhero.`name`, "
                    + "Superhero.`description` "
                    + "FROM Superhero "
                    + "LEFT OUTER JOIN Organization_Superhero members "
                    + "ON Superhero.id = members.superhero_id "
                    + "LEFT OUTER JOIN `Organization` "
                    + "ON `Organization`.id = members.org_id "
                    + "WHERE members.org_id = ?;";
            List<Superhero> members = jdbcTemplate.query(GET_MEMBERS,
                    new SuperheroMapper(), orgId);

            return members;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }
        
    /**
     * Sets the members field (list of Superheros) for a list of 
     * Organizations.
     * 
     * @param orgs 
     */
    private void getMembersForOrganizations(List<Organization> orgs) {
        for (Organization org : orgs) {
            org.setMembers(getOrganizationMembers(org.getId()));
        }
    }
    
    /**
     * Adds an Organization member (Superhero) to the 
     * Organization_Superhero bridge table and returns true if 
     * successful.
     * 
     * @param organization
     * @param superhero
     * @return boolean 
     */
    @Override
    public boolean addOrganizationMember(Organization organization, 
            Superhero superhero) {
        
        try {
            final String INSERT_ORGANIZATION_MEMBER
                    = "INSERT INTO Organization_Superhero "
                    + "(superhero_id, org_id) "
                    + "VALUES(?,?);";

            int numRowsAdded = jdbcTemplate.update(
                    INSERT_ORGANIZATION_MEMBER,
                    superhero.getId(),
                    organization.getId());
            
            return numRowsAdded == 1;
        } catch (NullPointerException | DuplicateKeyException e) {
            // ignore
        }
        return false;
    }
    
    /**
     * Deletes relationship between Organization and Superhero in 
     * Organization_Superhero bridge table, returns true if 
     * deletion occurred.
     * 
     * @param organization
     * @param superheroId
     * @return boolean
     */
    @Override
    public boolean deleteOrganizationMember(
            Organization organization, int superheroId) {
        
        try {
            final String DELETE_ORGANIZATION_MEMBER 
                = "DELETE FROM Organization_Superhero "
                + "WHERE org_id = ? AND superhero_id = ?;";
            
            int numRowsDeleted = jdbcTemplate.update(
                    DELETE_ORGANIZATION_MEMBER, 
                    organization.getId(), 
                    superheroId);
            
            return numRowsDeleted == 1;
        } catch (NullPointerException e) {
            //ignore
        }
        return false;
    }
    
    /**
     * Inserts Organization members into the bridge table.
     * @param orgId
     * @param members 
     */
    private void addMembersForOrganization(int orgId, 
            List<Superhero> members) {
        
        try {
            if (!members.isEmpty()) {
                final String INSERT_ORGANIZATION_MEMBER
                        = "INSERT INTO Organization_Superhero "
                        + "(superhero_id, org_id) "
                        + "VALUES(?,?);";
                for (Superhero member : members) {
                    jdbcTemplate.update(INSERT_ORGANIZATION_MEMBER, 
                            member.getId(),
                            orgId);
                }
            }
        } catch (NullPointerException e) {
            // ignore
        }
    }
    
    
    /**
     * Deletes rows in bridge table which are linked to a given 
     * Organization.
     * @param orgId 
     */
    private void deleteMembersForOrganization(int orgId) {
        
        final String DELETE_ORGANIZATION_MEMBERS 
                = "DELETE FROM Organization_Superhero "
                + "WHERE org_id = ?;";
        jdbcTemplate.update(DELETE_ORGANIZATION_MEMBERS, orgId);
    }

    
    /**
     * Maps Organization data into an Organization object.
     */
    public static final class OrganizationMapper implements 
            RowMapper<Organization> {

        @Override
        public Organization mapRow(ResultSet rs, int rowNum) 
                throws SQLException {

            Organization org = new Organization();
            org.setId(rs.getInt("id"));
            org.setName(rs.getString("name"));
            org.setDescription(rs.getString("description"));
            org.setStreet_address(rs.getString("street_address"));
            org.setCity(rs.getString("city"));
            org.setCountry(rs.getString("country"));
            
            return org;
        }
        
    }
    
}
