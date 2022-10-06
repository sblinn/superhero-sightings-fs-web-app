
package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dao.LocationDaoDB.LocationMapper;
import com.sblinn.superherosightings.dto.Location;
import com.sblinn.superherosightings.dto.Sighting;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
public class SightingDaoDB implements SightingDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public Sighting getSightingById(int id) {
        try {
            final String GET_SIGHTING
                    = "SELECT * FROM Sighting "
                    + "WHERE id = ?;";
            Sighting retrievedSighting = jdbcTemplate.queryForObject(
                    GET_SIGHTING, new SightingMapper(), id);
            return retrievedSighting;
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public List<Sighting> getAllSightingsAtLocation(int locationId) {
        try {
            final String GET_SIGHTINGS_AT_LOCATION
                    = "SELECT * FROM Sighting "
                    + "WHERE location_id = ?;";
            List<Sighting> retrievedSightings 
                    = jdbcTemplate.query(GET_SIGHTINGS_AT_LOCATION, 
                            new SightingMapper(), locationId);
            return retrievedSightings;
        } catch (DataAccessException e) {
            return null;
        }
    }
    
    @Override
    @Transactional
    public List<Sighting> getAllSightingsOnDate(LocalDate date) {
        try {
            final String GET_SIGHTINGS_ON_DATE
                    = "SELECT * FROM Sighting "
                    + "WHERE `date` BETWEEN ? AND ?;";
            
            // NOTE: Sighting DTO sets date with no nanoseconds
            LocalTime startTime = LocalTime.of(0, 0, 0);
            LocalDateTime startDateTime 
                    = LocalDateTime.of(date, startTime);
            
            LocalTime endTime = LocalTime.of(23, 59, 59);
            LocalDateTime endDateTime 
                    = LocalDateTime.of(date, endTime);
            
            List<Sighting> retrievedSightings
                    = jdbcTemplate.query(GET_SIGHTINGS_ON_DATE, 
                            new SightingMapper(), 
                            startDateTime, endDateTime);
            return retrievedSightings;
        } catch (DataAccessException e) {
            return null;
        }
    }
    
    @Override
    @Transactional
    public List<Location> getAllSightingLocationsForSuperhero(
            int superheroId) {
        
        try {
            final String GET_SIGHTING_LOCATIONS
                    = "SELECT "
                    + "l.id, "
                    + "l.`name`, "
                    + "l.street_address, "
                    + "l.city, "
                    + "l.state, "
                    + "l.country, "
                    + "l.latitude, "
                    + "l.longitude, "
                    + "l.`description` "
                    + "FROM Location l "
                    + "LEFT OUTER JOIN Sighting s "
                    + "ON s.location_id = l.id "
                    + "WHERE s.superhero_id = ? "
                    + "GROUP BY l.id;";

            List<Location> retrievedLocations
                    = jdbcTemplate.query(GET_SIGHTING_LOCATIONS,
                            new LocationMapper(), superheroId);
            return retrievedLocations;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    @Transactional
    public List<Sighting> getAllSightings() {
        final String SELECT_ALL 
                = "SELECT * FROM Sighting "
                + "ORDER BY id;";
        List<Sighting> sightings = jdbcTemplate.query(
                SELECT_ALL, new SightingMapper());
        return sightings;
    }

    /**
     * Adds a Sighting to the database and returns the Sighting with
     * an id value (if none set prior). 
     * 
     * @param sighting
     * @return 
     */
    @Override
    @Transactional
    public Sighting createSighting(Sighting sighting) {
        if (sighting.getId() != 0) {
            final String INSERT_SIGHTING
                    = "INSERT INTO Sighting(id, location_id, "
                    + "superhero_id, `date`) "
                    + "VALUES(?,?,?,?);";
            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement statement = conn.prepareStatement(
                        INSERT_SIGHTING);
                statement.setInt(1, sighting.getId());
                statement.setInt(2, sighting.getLocation_id());
                statement.setInt(3, sighting.getSuperhero_id());
                statement.setTimestamp(4, 
                        Timestamp.valueOf(sighting.getDate()));
                return statement;
            });
        } else {
            final String INSERT_SIGHTING_NO_ID
                    = "INSERT INTO Sighting(location_id, "
                    + "superhero_id, `date`) "
                    + "VALUES(?,?,?);";
            GeneratedKeyHolder keyholder = new GeneratedKeyHolder();
            
            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement statement = conn.prepareStatement(
                        INSERT_SIGHTING_NO_ID, 
                        Statement.RETURN_GENERATED_KEYS);
                statement.setInt(1, sighting.getLocation_id());
                statement.setInt(2, sighting.getSuperhero_id());
                statement.setTimestamp(3, 
                        Timestamp.valueOf(sighting.getDate()));
                return statement;
            }, keyholder);
            
            sighting.setId(keyholder.getKey().intValue());
        }
        
        return sighting;
    }

    @Override
    public boolean updateSighting(Sighting updatedSighting) {
        final String UPDATE_SIGHTING
                = "UPDATE Sighting "
                + "SET id = ?, location_id = ?, superhero_id = ?, "
                + "`date` = ? "
                + "WHERE id = ?;";
       
        int numRowsUpdated = jdbcTemplate.update(UPDATE_SIGHTING,
                updatedSighting.getId(),
                updatedSighting.getLocation_id(),
                updatedSighting.getSuperhero_id(),
                Timestamp.valueOf(updatedSighting.getDate()),
                updatedSighting.getId());
                
        return numRowsUpdated == 1;
    }

    @Override
    public boolean deleteSightingById(int id) {
        final String DELETE_SIGHTING
                = "DELETE FROM Sighting "
                + "WHERE id = ?;";
        int numRowsDeleted = jdbcTemplate.update(DELETE_SIGHTING, id);
        
        return numRowsDeleted == 1;
    }

    
    /**
     * Maps Sighting data into a Sighting object.
     */
    public static final class SightingMapper implements 
            RowMapper<Sighting> {

        @Override
        public Sighting mapRow(ResultSet rs, int rowNum) 
                throws SQLException {
            
            Sighting sighting = new Sighting();
            sighting.setId(rs.getInt("id"));
            sighting.setLocation_id(rs.getInt("location_id"));
            sighting.setSuperhero_id(rs.getInt("superhero_id"));
            sighting.setDate(rs.getTimestamp("date").toLocalDateTime());
            
            return sighting;
        }
        
    }
    
}
