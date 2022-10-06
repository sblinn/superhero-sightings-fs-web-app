
package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dto.Location;
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
public class LocationDaoDB implements LocationDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    @Transactional
    public Location getLocationById(int id) {
        try {
            final String SELECT_LOCATION
                    = "SELECT * FROM Location "
                    + "WHERE id = ?;";
            Location retrievedLocation = jdbcTemplate.queryForObject(
                    SELECT_LOCATION, new LocationMapper(), id);
            return retrievedLocation;
        } catch (DataAccessException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public List<Location> getAllLocations() {
        final String SELECT_ALL
                = "SELECT * FROM Location "
                + "ORDER BY id;";
        List<Location> locations = jdbcTemplate.query(
                SELECT_ALL, new LocationMapper());
        
        return locations;
    }

    @Override
    @Transactional
    public Location createLocation(Location location) {
        if (location.getId() != 0) {
            final String INSERT_LOCATION
                    = "INSERT INTO Location(id, `name`, "
                    + "street_address, city, state, country, "
                    + "latitude, longitude, `description`) "
                    + "VALUES(?,?,?,?,?,?,?,?,?);";
            
            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement statement = conn.prepareStatement(
                        INSERT_LOCATION, Statement.NO_GENERATED_KEYS);
                statement.setInt(1, location.getId());
                statement.setString(2, location.getName());
                statement.setString(3, location.getStreet_address());
                statement.setString(4, location.getCity());
                statement.setString(5, location.getState());
                statement.setString(6, location.getCountry());
                statement.setBigDecimal(7, location.getLatitude());
                statement.setBigDecimal(8, location.getLongitude());
                statement.setString(9, location.getDescription());
                return statement;
            });
        } else {
            final String INSERT_LOCATION_NO_ID
                    = "INSERT INTO Location(`name`, "
                    + "street_address, city, state, country, "
                    + "latitude, longitude, `description`) "
                    + "VALUES(?,?,?,?,?,?,?,?);";
            GeneratedKeyHolder keyholder = new GeneratedKeyHolder();
            
            jdbcTemplate.update((Connection conn) -> {
                PreparedStatement statement = conn.prepareStatement(
                    INSERT_LOCATION_NO_ID, Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, location.getName());
                statement.setString(2, location.getStreet_address());
                statement.setString(3, location.getCity());
                statement.setString(4, location.getState());
                statement.setString(5, location.getCountry());
                statement.setBigDecimal(6, location.getLatitude());
                statement.setBigDecimal(7, location.getLongitude());
                statement.setString(8, location.getDescription());
                return statement;
            }, keyholder);
            
            location.setId(keyholder.getKey().intValue());
        }
        
        return location;
    }

    // Allows empty fields but not null required fields.
    @Override
    public boolean updateLocation(Location updatedLocation) {
        final String UPDATE_LOCATION
                = "UPDATE Location "
                + "SET id = ?, `name` = ?, `street_address` = ?, "
                + "city = ?, state = ?, country = ?, latitude = ?, "
                + "longitude = ?, `description` = ? "
                + "WHERE id = ?;";
        int numRowsUpdated = jdbcTemplate.update(
                UPDATE_LOCATION,
                updatedLocation.getId(),
                updatedLocation.getName(),
                updatedLocation.getStreet_address(),
                updatedLocation.getCity(),
                updatedLocation.getState(),
                updatedLocation.getCountry(),
                updatedLocation.getLatitude(),
                updatedLocation.getLongitude(),
                updatedLocation.getDescription(),
                updatedLocation.getId());
        
        return numRowsUpdated == 1;
    }

    @Override
    @Transactional
    public boolean deleteLocationById(int id) {
        // must delete reference in Sighting
        final String DELETE_SIGHTING
                = "DELETE FROM Sighting "
                + "WHERE location_id = ?;";
        jdbcTemplate.update(DELETE_SIGHTING, id);
        
        final String DELETE_LOCATION
                = "DELETE FROM Location "
                + "WHERE id = ?;";
        int numRowsDeleted = jdbcTemplate.update(DELETE_LOCATION, id);
        
        return numRowsDeleted == 1;
    }
    
    
    /**
     * Maps Location data to a Location Object.
     */
    public static final class LocationMapper implements 
            RowMapper<Location> {

        @Override
        public Location mapRow(ResultSet rs, int rowNum) 
                throws SQLException {
            
            Location location = new Location();
            location.setId(rs.getInt("id"));
            location.setName(rs.getString("name"));
            location.setStreet_address(rs.getString("street_address"));
            location.setCity(rs.getString("city"));
            location.setState(rs.getString("state"));
            location.setCountry(rs.getString("country"));
            location.setLatitude(rs.getBigDecimal("latitude"));
            location.setLongitude(rs.getBigDecimal("longitude"));
            location.setDescription(rs.getString("description"));
            
            return location;
        }
        
    }

}
