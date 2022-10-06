package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dto.Location;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Sara Blinn
 */
@SpringBootTest
@Transactional
public class LocationDaoDBTest {
    
    @Autowired
    private LocationDao testLocationDao;
    
    
    public LocationDaoDBTest() {
    }

    @BeforeEach
    public void setUp() {
        List<Location> locations = testLocationDao.getAllLocations();
        for (Location location : locations) {
            testLocationDao.deleteLocationById(location.getId());
        }
    }
    
    @AfterEach
    public void tearDown() {
        List<Location> locations = testLocationDao.getAllLocations();
        for (Location location : locations) {
            testLocationDao.deleteLocationById(location.getId());
        }
    }
    
    

    /*
     * createLocation:
     * - test createLocation with id provided and test getLocationById.
     * - test createLocation with duplicate id.
     * - test createLocation without id provided. (db sets id)
     * - test createLocation with missing fields (both required & 
     *   not required fields).
     */
    @Test
    public void testCreateAndGetValidLocation() {
        // create Location, providing an id
        Location testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");
        
        Location createdLocation 
                = testLocationDao.createLocation(testLocation);
        Location retrievedLocation
                = testLocationDao.getLocationById(1);
        
        // compare testLocation to createdLocation
        assertEquals(testLocation.getId(), createdLocation.getId(), 
                "createdLocation ID should equal 1.");
        assertEquals(testLocation.getName(), createdLocation.getName(),
                "createdLocation Name should be equal.");
        assertEquals(testLocation.getStreet_address(), 
                createdLocation.getStreet_address(),
                "createdLocation Street Address should be equal.");
        assertEquals(testLocation.getCity(), 
                createdLocation.getCity(),
                "createdLocation City should be equal.");
        assertEquals(testLocation.getState(), 
                createdLocation.getState(),
                "createdLocation State should be equal.");
        assertEquals(testLocation.getCountry(), 
                createdLocation.getCountry(),
                "createdLocation Country should be equal.");
        assertEquals(testLocation.getLatitude(), 
                createdLocation.getLatitude(),
                "createdLocation Latitude should be equal.");
        assertEquals(testLocation.getLongitude(), 
                createdLocation.getLongitude(),
                "createdLocation Longitude should be equal.");
        assertEquals(testLocation.getDescription(), 
                createdLocation.getDescription(),
                "createdLocation Description should be equal.");
        
        // compare testLocation to retrievedLocation
        assertEquals(testLocation.getId(), retrievedLocation.getId(), 
                "retrievedLocation ID should equal 1.");
        assertEquals(testLocation.getName(), retrievedLocation.getName(),
                "retrievedLocation name should be equal.");
        assertEquals(testLocation.getStreet_address(), 
                retrievedLocation.getStreet_address(),
                "sretrievedLocation street address should be equal.");
        assertEquals(testLocation.getCity(), 
                retrievedLocation.getCity(),
                "retrievedLocation city should be equal.");
        assertEquals(testLocation.getState(), 
                retrievedLocation.getState(),
                "retrievedLocation state should be equal.");
        assertEquals(testLocation.getCountry(), 
                retrievedLocation.getCountry(),
                "retrievedLocation country should be equal.");
        assertEquals(new BigDecimal("40.758700"), 
                retrievedLocation.getLatitude(),
                "retrievedLocation latitude should be equal.");
        assertEquals(new BigDecimal("73.978700"), 
                retrievedLocation.getLongitude(),
                "retrievedLocation longitude should be equal.");
        assertEquals(testLocation.getDescription(), 
                retrievedLocation.getDescription(),
                "retrievedLocation description should be equal.");
    }
    
    @Test
    public void testCreateLocationGenerateId() {
        Location testLocation = new Location();
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");
        
        Location createdLocation 
                = testLocationDao.createLocation(testLocation);
        
        int generatedId = createdLocation.getId();
        
        // confirm that id was generated and returned
        assertNotNull(generatedId, "ID should have been "
                + "generated and returned.");
        
        // retrieve the Location at the expected id
        Location retrievedLocation
                = testLocationDao.getLocationById(generatedId);
        
        // confirm that the retrievedLocation is equal to the createdLocation
        assertEquals(generatedId, retrievedLocation.getId(), 
                "retrievedLocation ID should equal generatedId.");
        assertEquals(createdLocation.getName(), retrievedLocation.getName(),
                "retrievedLocation name should be equal.");
        assertEquals(createdLocation.getStreet_address(), 
                retrievedLocation.getStreet_address(),
                "sretrievedLocation street address should be equal.");
        assertEquals(createdLocation.getCity(), 
                retrievedLocation.getCity(),
                "retrievedLocation city should be equal.");
        assertEquals(createdLocation.getState(), 
                retrievedLocation.getState(),
                "retrievedLocation state should be equal.");
        assertEquals(createdLocation.getCountry(), 
                retrievedLocation.getCountry(),
                "retrievedLocation country should be equal.");
        assertEquals(new BigDecimal("40.758700"), 
                retrievedLocation.getLatitude(),
                "retrievedLocation latitude should be equal.");
        assertEquals(new BigDecimal("73.978700"), 
                retrievedLocation.getLongitude(),
                "retrievedLocation longitude should be equal.");
        assertEquals(createdLocation.getDescription(), 
                retrievedLocation.getDescription(),
                "retrievedLocation description should be equal.");
    }
    
    @Test
    public void testCreateDuplicateIdLocation() {
        Location testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");
        
        Location testLocation2 = new Location();
        testLocation2.setId(1);
        testLocation2.setName("Statue of Liberty");
        testLocation2.setCity("New York");
        testLocation2.setState("NY");
        testLocation2.setCountry("US");
        testLocation2.setLatitude(new BigDecimal("40.4892"));
        testLocation2.setLongitude(new BigDecimal("74.0445"));
        testLocation2.setDescription("NY Landmark, no address");
        
        testLocationDao.createLocation(testLocation);
        
        try {
            testLocationDao.createLocation(testLocation2);
            fail("DuplicateKeyException should be thrown when "
                    + "attempting to create Location with duplicate "
                    + "primary key (id).");
        } catch (DuplicateKeyException e) {
            // passed
        }
    }
    
    @Test
    public void testCreateLocationNullFields() {
        Location testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        //testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");
        
        try {
            testLocation.setCountry(null);
            testLocationDao.createLocation(testLocation);
            fail("Creating a Location with a null required field "
                    + "should throw Exception.");
        } catch (DataIntegrityViolationException e) {
            // passed
            
            testLocation.setCountry("US");
            testLocation.setState(null);
            testLocationDao.createLocation(testLocation);
            Location retrievedLocation
                    = testLocationDao.getLocationById(1);
            
            assertNotNull(retrievedLocation, "Location should have "
                    + "successfully been created, State is not a "
                    + "required field.");
        }
    }
    
    @Test
    public void testCreateLocationEmptyFields() {
        Location testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        //testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");
        
        // create Location using empty required field
        testLocation.setCountry(""); //required
        testLocationDao.createLocation(testLocation);
        Location retrievedLocation
                = testLocationDao.getLocationById(1);
        assertNotNull(retrievedLocation, "Location should have "
                + "still been created, method does not "
                + "check for empty fields.");

        // createLocation using empty unrequired field
        testLocation.setId(2);
        testLocation.setCountry("US");
        testLocation.setState(""); //unrequired
        testLocationDao.createLocation(testLocation);
        retrievedLocation = testLocationDao.getLocationById(2);

        assertNotNull(retrievedLocation, "Location should have "
                + "successfully been created.");
    }
    
    /*
     * getLocationById:
     * - test getLocationById with valid id. (see testCreateAndGetValidLocation)
     * - test getLocationById with invalid id (id does not exist).
     */
    @Test
    public void testGetLocationByInvalidId() {
        Location testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");

        testLocationDao.createLocation(testLocation);

        Location retrievedLocation
                = testLocationDao.getLocationById(1);

        assertEquals(testLocation, retrievedLocation,
                "Method should have returned testLocation at id = 1.");

        Location invalidIdRetrieval
                = testLocationDao.getLocationById(2);

        assertNull(invalidIdRetrieval, "Method should return null "
                + "when no Location exists with the provided id.");
    }

    /*
     * getAllLocations:
     * - test getAllLocations with two Locations in database.
     * - test getAllLocations with empty database table. (no locations)
     */
    @Test
    public void testGetAllLocations() {
        Location testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");

        Location testLocation2 = new Location();
        testLocation2.setId(2);
        testLocation2.setName("Statue of Liberty");
        testLocation2.setCity("New York");
        testLocation2.setState("NY");
        testLocation2.setCountry("US");
        testLocation2.setLatitude(new BigDecimal("40.4892"));
        testLocation2.setLongitude(new BigDecimal("74.0445"));
        testLocation2.setDescription("NY Landmark, no address");
        
        // test method when database is empty
        List<Location> locations = testLocationDao.getAllLocations();
        
        assertTrue(locations.isEmpty(), 
                "Retrieved list of Locations should be empty, "
                        + "database is empty.");
        
        // add Locations and test method when database has two entries
        testLocationDao.createLocation(testLocation);
        testLocationDao.createLocation(testLocation2);
        
        locations = testLocationDao.getAllLocations();
        
        assertEquals(2, locations.size(), "Method should have returned "
                + "a list containing two locations.");
        assertTrue(locations.contains(testLocation),
                "Retrieved list should contain testLocation.");
        assertTrue(locations.contains(testLocation2),
                "Retrieved list should contain testLocation2");
    }

    /*
     * updateLocation:
     * - test updateLocation with valid id.
     * - test updateLocation with invalid id (no location exists at that id).
     * - test updateLocation with null fields.
     * - test updateLocation with empty fields.
     */
    @Test
    public void testUpdateLocation() {
        Location testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");
        
        testLocationDao.createLocation(testLocation);
        
        // update testLocation's fields and call updateLocation
        testLocation.setCountry("MX");
        testLocation.setDescription("Incorrect country has been updated.");
        
        boolean isUpdated = testLocationDao.updateLocation(testLocation);
        Location retrievedLocation
                = testLocationDao.getLocationById(1);
        
        assertTrue(isUpdated, "Method should return True, Location "
                + "should have successfully updated table row.");
        assertEquals(testLocation, retrievedLocation,
                "RetrievedLocation should be equal to the updated "
                        + "testLocation.");
        assertEquals("MX", retrievedLocation.getCountry(), 
                "Location's country should be 'MX'.");
        assertEquals("Incorrect country has been updated.",
                retrievedLocation.getDescription(),
                "Location's description should be updated.");
    }
    
    @Test
    public void testUpdateLocationInvalidId() {
        Location testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");
        
        testLocationDao.createLocation(testLocation);
        
        testLocation.setId(2);
        testLocation.setCountry("MX");
        
        boolean isUpdated = testLocationDao.updateLocation(testLocation);
        
        assertFalse(isUpdated, "Location should not updated when no "
                + "Location exists with the id.");
    }
    
    @Test
    public void testUpdateLocationNullRequiredFields() {
        Location testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");
        
        testLocationDao.createLocation(testLocation);
        
        try {
            // make required field null and attempt to update
            testLocation.setCountry(null);
            testLocationDao.updateLocation(testLocation);
            fail("Required field is null, method should throw "
                    + "DataIntegrityViolationException.");
        } catch (DataIntegrityViolationException e) {
            // passed
            // make unrequired field null and attempt to update
            testLocation.setCountry("US");
            testLocation.setStreet_address(null);
            boolean isUpdated
                    = testLocationDao.updateLocation(testLocation);
            assertTrue(isUpdated, "Field updated to null is not a "
                    + "required field, Location should have "
                    + "successfully updated.");
        }
    }
    
    @Test
    public void testUpdateLocationNullUnrequiredFields() {
        Location testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");
        
        testLocationDao.createLocation(testLocation);
 
        // make unrequired field null and attempt to update
        testLocation.setStreet_address(null);
        boolean isUpdated
                = testLocationDao.updateLocation(testLocation);
        Location retrievedLocation 
                = testLocationDao.getLocationById(1);
        
        assertTrue(isUpdated, "Field updated to null is not a "
                + "required field, Location should have "
                + "successfully updated.");
        
        assertEquals(testLocation, retrievedLocation, 
                "Retrieved Location should be the same as the "
                        + "updated test Location.");
    }
    
    // Empty fields still update
    @Test
    public void testUpdateLocationEmptyFields() {
        Location testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");

        testLocationDao.createLocation(testLocation);

        // make required field empty and attempt to update
        testLocation.setCountry("");
        boolean isUpdated
                = testLocationDao.updateLocation(testLocation);
        assertTrue(isUpdated, "Empty required field.");

        // make unrequired field empty and attempt to update
        testLocation.setCountry("US");
        testLocation.setStreet_address("");
        isUpdated = testLocationDao.updateLocation(testLocation);
        assertTrue(isUpdated, "Empty field.");
    }

    /*
     * deleteLocation:
     * - test deleteLocation with valid id (id that exists).
     * - test deleteLocation with invalid id (no Location exists
     *   with that id).
     */
    @Test
    public void testDeleteLocationById() {
        Location testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");
        
        Location createdLocation 
                = testLocationDao.createLocation(testLocation);
        Location retrievedLocation 
                = testLocationDao.getLocationById(1);
        
        // confirm that location was added to the db.
        assertEquals(createdLocation, retrievedLocation, 
                "Location should have been successfully added to the"
                        + " database.");
        // delete location 
        Boolean isDeleted = testLocationDao.deleteLocationById(1);
        // confirm that the correct Location was deleted
        assertTrue(isDeleted, "Method should return True.");
        assertNull(testLocationDao.getLocationById(1), 
                "Location at id = 1 should be deleted.");
    }

    @Test
    public void testDeleteLocationByInvalidId() {
        Location testLocation = new Location();
        testLocation.setId(1);
        testLocation.setName("Rockefeller Center");
        testLocation.setStreet_address("45 Rockefeller Plaza");
        testLocation.setCity("New York");
        testLocation.setState("NY");
        testLocation.setCountry("US");
        testLocation.setLatitude(new BigDecimal("40.7587"));
        testLocation.setLongitude(new BigDecimal("73.9787"));
        testLocation.setDescription("NY Location");
        
        Location createdLocation 
                = testLocationDao.createLocation(testLocation);
        // attempt to delete a location by id that doesn't exist
        Boolean isDeleted = testLocationDao.deleteLocationById(2);
        
        assertEquals(testLocation, createdLocation);
        assertFalse(isDeleted, 
                "Method should return False as nothing was deleted.");
    }
    
}
