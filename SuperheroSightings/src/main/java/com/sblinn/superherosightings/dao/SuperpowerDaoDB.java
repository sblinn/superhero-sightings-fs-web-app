
package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dao.SuperheroDaoDB.SuperheroMapper;
import com.sblinn.superherosightings.dto.Superhero;
import com.sblinn.superherosightings.dto.Superpower;
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
public class SuperpowerDaoDB implements SuperpowerDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    
    @Override
    @Transactional
    public Superpower getSuperpowerById(int id) {
        try {
            final String GET_SUPERPOWER 
                    = "SELECT * FROM Superpower "
                    + "WHERE id = ?;";
            Superpower retrievedPower = jdbcTemplate.queryForObject(
                    GET_SUPERPOWER, new SuperpowerMapper(), id);
            
            retrievedPower.setSuperheros(
                    getAllSuperherosWithSuperpower(id));
            
            return retrievedPower;
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public List<Superpower> getAllSuperpowers() {
        final String SELECT_ALL
                = "SELECT * FROM Superpower "
                + "ORDER BY id;";
        List<Superpower> superpowers = jdbcTemplate.query(
                SELECT_ALL, new SuperpowerMapper());
        
        for (Superpower superpower : superpowers) {
            superpower.setSuperheros(
                    getAllSuperherosWithSuperpower(superpower.getId()));
        }
        
        return superpowers;
    }

    /**
     * Adds a given Superpower to the database and returns the 
     * Superpower with an id if one was not specified in the parameter.
     * 
     * @param superpower
     * @return Superpower
     */
    @Override
    @Transactional
    public Superpower createSuperpower(Superpower superpower) {
        if (superpower.getId() != 0) {
            final String INSERT_POWER
                    = "INSERT INTO Superpower(id, `name`) "
                    + "VALUES(?,?);";
            
            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement statement = conn.prepareStatement(
                        INSERT_POWER);
                statement.setInt(1, superpower.getId());
                statement.setString(2, superpower.getName());
                return statement;
            });
        } else {
            final String INSERT_POWER_NO_ID 
                    = "INSERT INTO Superpower(`name`) "
                    + "VALUES(?);";
            GeneratedKeyHolder keyholder = new GeneratedKeyHolder();
            
            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement statement = conn.prepareStatement(
                    INSERT_POWER_NO_ID, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, superpower.getName());
                return statement;
            }, keyholder);
            
            superpower.setId(keyholder.getKey().intValue());
        }
        
        if (!superpower.getSuperheros().isEmpty()) {
            addSuperpowerForSuperheros(superpower, 
                    superpower.getSuperheros());
        }
        
        return superpower;
    }

    /**
     * Returns true if update  to Superpower occurred.
     * @param updatedSuperpower
     * @return boolean
     */
    @Override
    @Transactional
    public boolean updateSuperpower(Superpower updatedSuperpower) {
        // get the current list of Superheros with Superpower 
        List<Superhero> currentSuperherosWithSuperpower
                = getAllSuperherosWithSuperpower(
                        updatedSuperpower.getId());
        int numRowsUpdated = 0;
        
        final String UPDATE_POWER
                = "UPDATE Superpower "
                + "SET id = ?, `name` = ? "
                + "WHERE id = ?;";
        numRowsUpdated = jdbcTemplate.update(
                UPDATE_POWER,
                updatedSuperpower.getId(),
                updatedSuperpower.getName(),
                updatedSuperpower.getId());
        
        if (!currentSuperherosWithSuperpower.isEmpty()) {
            deleteSuperpowerForSuperheros(updatedSuperpower, 
                    currentSuperherosWithSuperpower);
        }
        
        if (!updatedSuperpower.getSuperheros().isEmpty()) {
            addSuperpowerForSuperheros(updatedSuperpower, 
                    updatedSuperpower.getSuperheros());
        }
        
        return numRowsUpdated == 1;
    }

    /**
     * Deletes Superpower and all references in the database, by id, 
     * and returns true if the deletion occurred.
     * 
     * @param id
     * @return boolean
     */
    @Override
    @Transactional
    public boolean deleteSuperpowerById(int id) {
        // must delete reference in Superhero_Superpower
        final String DELETE_SUPERHERO_SUPERPOWER
                = "DELETE FROM Superhero_Superpower "
                + "WHERE superpower_id = ?;";
        jdbcTemplate.update(DELETE_SUPERHERO_SUPERPOWER, id);
        
        final String DELETE_SUPERPOWER
                = "DELETE FROM Superpower "
                + "WHERE id = ?;";
        int numRowsDeleted = jdbcTemplate.update(DELETE_SUPERPOWER, id);
        
        return numRowsDeleted == 1;
    }

    
    /**
     * Returns a List of all Superheros with a given Superpower.
     * 
     * @param superpowerId
     * @return List<Superhero>
     */
    @Override
    public List<Superhero> getAllSuperherosWithSuperpower(int superpowerId) {
        
        try {
            final String GET_SUPERHEROS
                    = "SELECT "
                    + "Superhero.id, "
                    + "Superhero.`name`, "
                    + "Superhero.`description` "
                    + "FROM Superhero "
                    + "LEFT OUTER JOIN Superhero_Superpower "
                    + "ON Superhero.id = Superhero_Superpower.superhero_id "
                    + "LEFT OUTER JOIN Superpower "
                    + "ON Superpower.id = Superhero_Superpower.superpower_id "
                    + "WHERE Superhero_Superpower.superpower_id = ?;";

            List<Superhero> superheros = jdbcTemplate.query(
                    GET_SUPERHEROS,
                    new SuperheroMapper(), 
                    superpowerId);

            return superheros;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Queries the database to return a list of superpowers a given
     * superhero has. 
     * 
     * @param superheroId
     * @return List<Superpower>
     */
    @Override
    @Transactional
    public List<Superpower> getSuperpowersForSuperhero(int superheroId) {
        try {
            final String GET_SUPERPOWERS
                    = "SELECT "
                    + "Superpower.id,"
                    + "Superpower.`name` "
                    + "FROM Superpower "
                    + "LEFT OUTER JOIN Superhero_Superpower "
                    + "ON Superpower.id = Superhero_Superpower.superpower_id "
                    + "WHERE Superhero_Superpower.superhero_id = ?;";
            
            List<Superpower> powers = jdbcTemplate.query(
                    GET_SUPERPOWERS, 
                    new SuperpowerMapper(), 
                    superheroId);
            
            if (!powers.isEmpty()) {
                for(Superpower power : powers) {
                    power.setSuperheros(getAllSuperherosWithSuperpower(
                        power.getId()));
                }
            }
            
            return powers;
        } catch (NullPointerException e) {
            return new ArrayList<>();
        }
    }
    
    /**
     * Adds a relationship between a Superpower and a Superhero,
     * returns true if successful.
     * 
     * @param superpower
     * @param superhero
     * @return boolean
     */
    @Override
    public boolean addSuperpowerForSuperhero(Superpower superpower,
            Superhero superhero) {
        
        try {
            if (superpower != null) {
                final String INSERT_SUPERHERO_SUPERPOWER
                        = "INSERT INTO Superhero_Superpower "
                        + "(superhero_id, superpower_id) "
                        + "VALUES(?,?);";
                
                int numRowsAdded = jdbcTemplate.update(
                        INSERT_SUPERHERO_SUPERPOWER,
                        superhero.getId(),
                        superpower.getId());
                return numRowsAdded == 1;
            }
        } catch (NullPointerException | DuplicateKeyException e) {
            // ignore and return false
        }
        return false;
    }
    
    /**
     * Deletes relationship between a Superpower and a Superhero, 
     * returns true if deletion occurred.
     * 
     * @param superpower
     * @param superheroId
     * @return boolean
     */
    @Override
    public boolean deleteSuperpowerForSuperhero(Superpower superpower,
            int superheroId) {
        
        try {
            final String DELETE_SUPERHERO_SUPERPOWER 
                    = "DELETE FROM Superhero_Superpower "
                    + "WHERE superpower_id = ? AND superhero_id = ?;";
            
            int numRowsDeleted = jdbcTemplate.update(
                    DELETE_SUPERHERO_SUPERPOWER,
                    superpower.getId(),
                    superheroId);
            
            return numRowsDeleted == 1; 
        } catch (NullPointerException e) {
            //ignore and return false
        }
        return false;
    }
    
    private void addSuperpowerForSuperheros(Superpower superpower, 
            List<Superhero> superheros) {
        
        for (Superhero superhero : superheros) {
            addSuperpowerForSuperhero(superpower, superhero);
        }
    }
    
    private void deleteSuperpowerForSuperheros(Superpower superpower,
            List<Superhero> superheros) {
        
        for (Superhero superhero : superheros) {
            deleteSuperpowerForSuperhero(superpower, 
                    superhero.getId());
        }
    }
    
    
    /**
     * Maps Superpower data to a Superpower object.
     */
    public static final class SuperpowerMapper implements 
            RowMapper<Superpower> {

        @Override
        public Superpower mapRow(ResultSet rs, int rowNum) 
                throws SQLException {

            Superpower power = new Superpower();
            power.setId(rs.getInt("id"));
            power.setName(rs.getString("name"));
            
            return power;
        }
        
    }
}
