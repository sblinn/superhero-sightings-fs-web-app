
package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dto.Superhero;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DataAccessException;
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
public class SuperheroDaoDB implements SuperheroDao {

    @Autowired 
    private JdbcTemplate jdbcTemplate;
    
    
    @Override
    @Transactional
    public Superhero getSuperheroById(int id) {
        try {
            final String GET_SUPERHERO
                    = "SELECT * FROM Superhero "
                    + "WHERE id = ?;";
            Superhero retrievedHero = jdbcTemplate.queryForObject(
                    GET_SUPERHERO, new SuperheroMapper(), id);
            
            return retrievedHero;
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public List<Superhero> getAllSuperheros() {
        final String SELECT_ALL
                = "SELECT * FROM Superhero "
                + "ORDER BY id;";
        List<Superhero> superheros = jdbcTemplate.query(
                SELECT_ALL, new SuperheroMapper());
        
        return superheros;
    }

    /**
     * Adds Superhero to the database, return the Superhero with id 
     * if id not previously set.
     * 
     * @param superhero
     * @return Superhero
     */
    @Override
    @Transactional
    public Superhero createSuperhero(Superhero superhero) {

        if (superhero.getId() != 0) {
            final String INSERT_HERO
                    = "INSERT INTO Superhero(id, `name`, "
                    + "`description`) "
                    + "VALUES(?,?,?);";
            
            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement statement = conn.prepareStatement(
                        INSERT_HERO);
                statement.setInt(1, superhero.getId());
                statement.setString(2, superhero.getName());
                statement.setString(3, superhero.getDescription());
                return statement;
            });
        } else {
            final String INSERT_HERO_NO_ID 
                    = "INSERT INTO Superhero(`name`, `description`) "
                    + "VALUES(?,?);";
            GeneratedKeyHolder keyholder = new GeneratedKeyHolder();
            
            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement statement = conn.prepareStatement(
                        INSERT_HERO_NO_ID, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, superhero.getName());
                statement.setString(2, superhero.getDescription());
                return statement;
            }, keyholder);
            
            superhero.setId(keyholder.getKey().intValue());
        }
        
        return superhero;
    }

    @Override
    @Transactional
    public boolean updateSuperhero(Superhero updatedHero) {
        final String UPDATE_HERO
                = "UPDATE Superhero "
                + "SET id = ?, `name` = ?, `description` = ? "
                + "WHERE id = ?;";
        int numRowsUpdated = jdbcTemplate.update(
                UPDATE_HERO,
                updatedHero.getId(),
                updatedHero.getName(),
                updatedHero.getDescription(),
                updatedHero.getId());

        return numRowsUpdated == 1;
    }

    /**
     * Deletes Superhero and all references to Superhero by id if it 
     * exists, and returns true if deletion occurred.
     * 
     * @param id
     * @return boolean
     */
    @Override
    @Transactional
    public boolean deleteSuperheroById(int id) {
        // must delete references to superhero in:
        // Sighting, Superhero_Superpower, Organization_Superhero
        final String DELETE_SUPERHERO_SIGHTING
                = "DELETE FROM Sighting "
                + "WHERE superhero_id = ?;";
        jdbcTemplate.update(DELETE_SUPERHERO_SIGHTING, id);
        
        deleteSuperpowersForHero(id);
        deleteOrganizationsForHero(id);
        
        final String DELETE_SUPERHERO
                = "DELETE FROM Superhero "
                + "WHERE id = ?;";
        int numRowsDeleted = jdbcTemplate.update(DELETE_SUPERHERO, id);
        
        return numRowsDeleted == 1;
    }

    
    
    // PRIVATE METHODS FOR HANDLING SUPERHERO'S SUPERPOWERS

    /**
     * Deletes references to Superhero in Superhero_Superpower
     * bridge table. 
     * 
     * @param superheroId 
     */
    private void deleteSuperpowersForHero(int superheroId) {
        
        final String DELETE_SUPERHERO_SUPERPOWER
                = "DELETE FROM Superhero_Superpower "
                + "WHERE superhero_id = ?;";
        jdbcTemplate.update(DELETE_SUPERHERO_SUPERPOWER, 
                    superheroId);
    }
    
    
    // PRIVATE METHOD FOR HANDLING SUPERHERO'S ORGANIZATIONS

    /**
     * Deletes references to Superhero in Organization_Superhero 
     * bridge table. 
     * 
     * @param superheroId 
     */
    private void deleteOrganizationsForHero(int superheroId) {
        
        final String DELETE_ORGANIZATION_SUPERHERO
                = "DELETE FROM Organization_Superhero "
                + "WHERE superhero_id = ?;";
        jdbcTemplate.update(DELETE_ORGANIZATION_SUPERHERO,
                superheroId);
    }
    

    /**
     * Maps Superhero data to a Superhero object.
     */
    public static final class SuperheroMapper implements 
            RowMapper<Superhero> {
        
        @Override 
        public Superhero mapRow(ResultSet rs, int rowNum) 
                throws SQLException {
            
            Superhero superhero = new Superhero();
            superhero.setId(rs.getInt("id"));
            superhero.setName(rs.getString("name"));
            superhero.setDescription(rs.getString("description"));
            
            return superhero;
        }
        
    }
    
}
