package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dto.Location;
import com.sblinn.superherosightings.dto.Sighting;
import com.sblinn.superherosightings.dto.Superhero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
public class SightingDaoDBTest {
    
    @Autowired
    private SightingDao testSightingDao;
    
    @Autowired
    private SuperheroDao testSuperheroDao;
    
    @Autowired
    private LocationDao testLocationDao;
    
    public SightingDaoDBTest() {
    }

    @BeforeEach
    public void setUp() {
        List<Sighting> sightings = testSightingDao.getAllSightings();
        for (Sighting sighting : sightings) {
            testSightingDao.deleteSightingById(sighting.getId());
        }
        
        List<Superhero> heros = testSuperheroDao.getAllSuperheros();
        for (Superhero hero : heros) {
            testSuperheroDao.deleteSuperheroById(hero.getId());
        }
        
        List<Location> locations = testLocationDao.getAllLocations();
        for (Location location : locations) {
            testLocationDao.deleteLocationById(location.getId());
        }
        
        // CREATE TEST SUPERHEROS
        Superhero testHero = new Superhero();
        testHero.setId(1);
        testHero.setName("Superman");
        testHero.setDescription("Guy with superpowers.");
        
        Superhero testHero2 = new Superhero();
        testHero2.setId(2);
        testHero2.setName("Superwoman");
        testHero2.setDescription("Lady with superpowers.");
        
        testSuperheroDao.createSuperhero(testHero);
        testSuperheroDao.createSuperhero(testHero2);
    
        // CREATE TEST LOCATIONS
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
        
        testLocationDao.createLocation(testLocation);
        testLocationDao.createLocation(testLocation2);
    }
    
    @AfterEach
    public void tearDown() {
        List<Sighting> sightings = testSightingDao.getAllSightings();
        for (Sighting sighting : sightings) {
            testSightingDao.deleteSightingById(sighting.getId());
        }
        
        List<Superhero> heros = testSuperheroDao.getAllSuperheros();
        for (Superhero hero : heros) {
            testSuperheroDao.deleteSuperheroById(hero.getId());
        }
        
        List<Location> locations = testLocationDao.getAllLocations();
        for (Location location : locations) {
            testLocationDao.deleteLocationById(location.getId());
        }
    }
    
    
    /*
     * createSighting:
     * - test createSighting with id provided and test getSightingById.
     * - test createSighting with duplicate id.
     * - test createSighting without id provided (db generates id).
     * - test createSighting with improper date (date formatted 
     *   different than DTO, date without time values).
     * - test createSighting with null date. (date is the only field 
     *   of type that can be declared null)
     * - test createSighting with empty fields (required & unrequired fields).
     *   - empty foreign key int value (superhero_id).
     *   - invalid foreign key int value (superhero id) -- 
     *     superhero_id value that does not exist.
     */
    @Test
    public void testCreateAndGetSighting() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1);
        testSight.setSuperhero_id(1);
        testSight.setDate(LocalDateTime.now());
        
        Sighting createdSight 
                = testSightingDao.createSighting(testSight);
        Sighting retrievedSight
                = testSightingDao.getSightingById(1);
        
        assertNotNull(createdSight, "createdSight should not be null.");
        
        assertEquals(testSight, createdSight, 
                "testSight and createdSight should be equal.");
        assertEquals(testSight, retrievedSight,
                "testSight and retrievedSight should be equal.");
    }
    
    @Test
    public void testCreateSightingGenerateId() {
        Sighting testSight = new Sighting();
        testSight.setLocation_id(1);
        testSight.setSuperhero_id(1);
        testSight.setDate(LocalDateTime.now());
        
        Sighting createdSight 
                = testSightingDao.createSighting(testSight);
        int generatedId = createdSight.getId();
        Sighting retrievedSight
                = testSightingDao.getSightingById(generatedId);
        
        assertNotNull(generatedId, "Id should have been generated.");
        assertNotNull(createdSight, "createdSight should not be null.");
        
        assertEquals(testSight, createdSight, 
                "testSight and createdSight should be equal.");
        assertEquals(testSight, retrievedSight,
                "testSight and retrievedSight should be equal.");
    }
    
    @Test
    public void testCreateSightingDuplicateId() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1);
        testSight.setSuperhero_id(1);
        testSight.setDate(LocalDateTime.now());
        
        Sighting testSight2 = new Sighting();
        testSight2.setId(1);
        testSight2.setLocation_id(2);
        testSight2.setSuperhero_id(1);
        testSight2.setDate(LocalDateTime.now());
        
        Sighting createdSight 
                = testSightingDao.createSighting(testSight);
        
        assertNotNull(createdSight, "createdSight should not be null.");
        assertEquals(testSight, createdSight, "Sighting should "
                + "have been created.");

        try {
            testSightingDao.createSighting(testSight2);
            fail("DuplicateKeyException should be thrown when "
                    + "attempting to create Sighting with duplicate "
                    + "primary key (id).");
        } catch (DuplicateKeyException e) {
            // passed
        }
    }
    
    @Test
    public void testCreateSightingImproperDate() {
        DateTimeFormatter testFormat
                = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        DateTimeFormatter testFormatNoTime
                = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        // how Sighting DTO formats datetime
        DateTimeFormatter dtoFormat
                = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // testSight date lacks seconds and is formatted different than DTO
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.parse("07-22-2022 07:22",
                testFormat));
        
        Sighting createdSight 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight, 
                "Sighting should have been created.");
        
        Sighting retrievedSight
                = testSightingDao.getSightingById(1);
        assertNotNull(retrievedSight, "Method should not return null.");
        
        LocalDateTime expectedDate = LocalDateTime.parse(
                "2022-07-22 07:22:00", dtoFormat);
        LocalDateTime actualDate = retrievedSight.getDate();
        
        assertEquals(expectedDate, actualDate, "The date without "
                + "seconds set and created with a different format "
                + "should be formatted by the DTO and missing time "
                + "values should be set to 0.");
        
        try {
            // set date without any time values
            testSight.setDate(LocalDateTime.parse(
                    "07-22-2022", testFormatNoTime));
            fail("Attempting to parse a LocalDateTime object without "
                    + "LocalTime values should cause "
                    + "DateTimeParseException.");
        } catch (DateTimeParseException e) {
            // passed
        }
    }
    
    @Test
    public void testCreateSightingNullDate() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1);
        //testSight.setDate(LocalDateTime.now());
        
        try {
            testSight.setDate(null);
            //testSightingDao.createSighting(testSight);
            fail("NullPointerException should be thrown when required "
                    + "field is set to null.");
        } catch (NullPointerException e) {
            // passed
        }
    }
    
    @Test
    public void testCreateSightingEmptyDate() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1);
        //testSight.setDate(LocalDateTime.now());
        
        try {
            testSightingDao.createSighting(testSight);
            fail("NullPointerException should be thrown when required "
                    + "date field is not set.");
        } catch (NullPointerException e) {
            // passed
        }
    }
    
    @Test
    public void testCreateSightingEmptyFK() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        //testSight.setSuperhero_id(1);
        testSight.setDate(LocalDateTime.now());
        
        // attempt to create Sighting with Superhero_id foreign key
        // not yet set to a value
        try {
            testSightingDao.createSighting(testSight);
            fail("Exception should be thrown when required "
                    + "foreign key field is not set.");
        } catch (DataIntegrityViolationException e) {
            // passed
        }
    }
    
    @Test
    public void testCreateSightingInvalidFK() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(0); // no superhero exists with id 0
        testSight.setDate(LocalDateTime.now());
        
        // attempt to create sighting using invalid superhero_id
        // (foreign key) value which does not exist in Superhero
        try {
            testSightingDao.createSighting(testSight);
            fail("Exception should be thrown when required "
                    + "foreign key field is invalid (id value "
                    + "that does not exist).");
        } catch (DataIntegrityViolationException e) {
            // passed
        }
    }
    
    
    /*
     * getSightingById:
     * - test getSightingById with valid id (see testCreateAndGetSighting).
     * - test getSightingById with invalid id (id does not exist).
     */
    @Test
    public void testGetSightingByInvalidId() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.now());
        
        Sighting createdSight 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight, 
                "Sighting should have been created.");
        
        Sighting retrievedSight
                = testSightingDao.getSightingById(2);
        
        assertNull(retrievedSight, "Method should return null when "
                + "no Sighting exists with the provided id.");
    }

    
    /*
     * getAllSightingsAtLocation:
     * - test getAllSightingsAtLocation using invalid location id 
     *   (location does not exist with the id).
     * - test getAllSightingsAtLocation with valid id, but no sightings
     *   at location.
     * - test getAllSightingsAtLocation with valid id, sightings at
     *   location.
     */
    @Test
    public void testGetAllSightingsAtLocationInvalidLocationId() {
        DateTimeFormatter format 
                = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.parse("2022-07-22 07:22:00",
                format));
        
        Sighting testSight2 = new Sighting();
        testSight2.setId(2);
        testSight2.setLocation_id(1); 
        testSight2.setSuperhero_id(1); 
        testSight2.setDate(LocalDateTime.parse("2022-07-23 07:23:00",
                format));
        
        Sighting testSight3 = new Sighting();
        testSight3.setId(3);
        testSight3.setLocation_id(2); 
        testSight3.setSuperhero_id(1); 
        testSight3.setDate(LocalDateTime.parse("2022-07-23 08:23:00",
                format));
        
        Sighting testSight4 = new Sighting();
        testSight4.setId(4);
        testSight4.setLocation_id(1); 
        testSight4.setSuperhero_id(2); 
        testSight4.setDate(LocalDateTime.parse("2022-07-23 08:23:00",
                format));
        
        Sighting createdSight1 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight1, 
                "Sighting1 should have been created.");
        
        Sighting createdSight2 
                = testSightingDao.createSighting(testSight2);
        assertNotNull(createdSight2, 
                "Sighting2 should have been created.");
        
        Sighting createdSight3 
                = testSightingDao.createSighting(testSight3);
        assertNotNull(createdSight3, 
                "Sighting3 should have been created.");
        
        Sighting createdSight4 
                = testSightingDao.createSighting(testSight4);
        assertNotNull(createdSight4, 
                "Sighting4 should have been created.");
        
        // attempt to get Sightings from Location that doesn't exist
        List<Sighting> retrievedSights 
                = testSightingDao.getAllSightingsAtLocation(3);
        
        assertTrue(retrievedSights.isEmpty(), "Retrieved Sightings "
                + "should be empty.");
    }
    
    @Test
    public void testGetAllSightingsAtLocationNoSightingsAtLocation() {
        DateTimeFormatter format 
                = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.parse("2022-07-22 07:22:00",
                format));
        
        Sighting testSight2 = new Sighting();
        testSight2.setId(2);
        testSight2.setLocation_id(1); 
        testSight2.setSuperhero_id(1); 
        testSight2.setDate(LocalDateTime.parse("2022-07-23 07:23:00",
                format));
        
//        Sighting testSight3 = new Sighting();
//        testSight3.setId(3);
//        testSight3.setLocation_id(2); 
//        testSight3.setSuperhero_id(1); 
//        testSight3.setDate(LocalDateTime.parse("2022-07-23 08:23:00",
//                format));
        
        Sighting testSight4 = new Sighting();
        testSight4.setId(4);
        testSight4.setLocation_id(1); 
        testSight4.setSuperhero_id(2); 
        testSight4.setDate(LocalDateTime.parse("2022-07-23 08:23:00",
                format));
        
        Sighting createdSight1 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight1, 
                "Sighting1 should have been created.");
        
        Sighting createdSight2 
                = testSightingDao.createSighting(testSight2);
        assertNotNull(createdSight2, 
                "Sighting2 should have been created.");
        
//        Sighting createdSight3 
//                = testSightingDao.createSighting(testSight3);
//        assertNotNull(createdSight3, 
//                "Sighting3 should have been created.");
        
        Sighting createdSight4 
                = testSightingDao.createSighting(testSight4);
        assertNotNull(createdSight4, 
                "Sighting4 should have been created.");
        
        // attempt to get Sightings from Location that has no sightings
        List<Sighting> retrievedSights 
                = testSightingDao.getAllSightingsAtLocation(2);
        
        assertTrue(retrievedSights.isEmpty(), "Retrieved Sightings "
                + "should be empty, there are no Sightings at "
                + "Location id 2.");
    }
    
    @Test
    public void testGetAllSightingsAtLocation() {
        DateTimeFormatter format 
                = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.parse("2022-07-22 07:22:00",
                format));
        
        Sighting testSight2 = new Sighting();
        testSight2.setId(2);
        testSight2.setLocation_id(1); 
        testSight2.setSuperhero_id(1); 
        testSight2.setDate(LocalDateTime.parse("2022-07-23 07:23:00",
                format));
        
        Sighting testSight3 = new Sighting();
        testSight3.setId(3);
        testSight3.setLocation_id(2); 
        testSight3.setSuperhero_id(1); 
        testSight3.setDate(LocalDateTime.parse("2022-07-23 08:23:00",
                format));
        
        Sighting testSight4 = new Sighting();
        testSight4.setId(4);
        testSight4.setLocation_id(1); 
        testSight4.setSuperhero_id(2); 
        testSight4.setDate(LocalDateTime.parse("2022-07-23 08:23:00",
                format));
        
        Sighting createdSight1 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight1, 
                "Sighting1 should have been created.");
        
        Sighting createdSight2 
                = testSightingDao.createSighting(testSight2);
        assertNotNull(createdSight2, 
                "Sighting2 should have been created.");
        
        Sighting createdSight3 
                = testSightingDao.createSighting(testSight3);
        assertNotNull(createdSight3, 
                "Sighting3 should have been created.");
        
        Sighting createdSight4 
                = testSightingDao.createSighting(testSight4);
        assertNotNull(createdSight4, 
                "Sighting4 should have been created.");
        
        // attempt to get Sightings from Location that doesn't exist
        List<Sighting> retrievedSights1 
                = testSightingDao.getAllSightingsAtLocation(1);
        List<Sighting> retrievedSights2
                = testSightingDao.getAllSightingsAtLocation(2);
        
        assertTrue(retrievedSights1.contains(testSight),
                "Sightings retrieved from Location 1 should contain "
                        + "testSight1.");
        assertTrue(retrievedSights1.contains(testSight2),
                "Sightings retrieved from Location 1 should contain "
                        + "testSight2.");        
        assertFalse(retrievedSights1.contains(testSight3), 
                "Sightings retrieved from Location 1 should not "
                        + "contain testSight3.");
        assertTrue(retrievedSights1.contains(testSight4),
                "Sightings retrieved from Location 1 should contain "
                        + "testSight4.");

        assertTrue(retrievedSights2.contains(testSight3),
                "Sightings retrieved from Location 2 should contain "
                        + "testSight3.");
    }
    
    
    /*
     * getAllSightingsOnDate:
     * - test getAllSightingsOnDate using invalid date:
     *   - date not formatted for mySQL.
     * - test getAllSightingsOnDate using date that has no sightings.
     * - test getAllSightingsOnDate using date that has sightings.
     */
    @Test
    public void testGetAllSightingsOnDateImproperDate() {
        DateTimeFormatter dtFormat
                = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.parse("2022-07-22 07:22",
                dtFormat));
        
        Sighting testSight2 = new Sighting();
        testSight2.setId(2);
        testSight2.setLocation_id(1); 
        testSight2.setSuperhero_id(1); 
        testSight2.setDate(LocalDateTime.parse("2022-07-23 07:23",
                dtFormat));
        
        Sighting testSight3 = new Sighting();
        testSight3.setId(3);
        testSight3.setLocation_id(2); 
        testSight3.setSuperhero_id(1); 
        testSight3.setDate(LocalDateTime.parse("2022-07-23 08:23",
                dtFormat));
        
        Sighting testSight4 = new Sighting();
        testSight4.setId(4);
        testSight4.setLocation_id(1); 
        testSight4.setSuperhero_id(2); 
        testSight4.setDate(LocalDateTime.parse("2022-07-23 08:23",
                dtFormat));
        
        Sighting createdSight1 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight1, 
                "Sighting1 should have been created.");
        
        Sighting createdSight2 
                = testSightingDao.createSighting(testSight2);
        assertNotNull(createdSight2, 
                "Sighting2 should have been created.");
        
        Sighting createdSight3 
                = testSightingDao.createSighting(testSight3);
        assertNotNull(createdSight3, 
                "Sighting3 should have been created.");
        
        Sighting createdSight4 
                = testSightingDao.createSighting(testSight4);
        assertNotNull(createdSight4, 
                "Sighting4 should have been created.");
        
        // attempt to get Sightings on date that lacks seconds 
        // and is formatted different than Sighting DTO (yyyy-MM-dd)
        DateTimeFormatter dFormat 
                = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDate date1 = LocalDate.parse("07-22-2022", dFormat);
        LocalDate date2 = LocalDate.parse("07-23-2022", dFormat);
        
        List<Sighting> date1Sightings 
                = testSightingDao.getAllSightingsOnDate(date1);
        List<Sighting> date2Sightings
                = testSightingDao.getAllSightingsOnDate(date2);
        
        assertFalse(date1Sightings.isEmpty(), "List of Sightings "
                + "on date1 should not be empty.");
        assertFalse(date2Sightings.isEmpty(), "List of Sightings "
                + "on date2 should not be empty.");
        assertTrue(date1Sightings.size() == 1, "List of Sightings "
                + "on date1 should contain 1 Sighting.");
        assertTrue(date2Sightings.size() == 3, "List of Sightings "
                + "on date2 should contain 3 Sightings.");
    }
    
    @Test
    public void testGetAllSightingsOnDateWithNoSightings() {
        DateTimeFormatter dtFormat
                = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.parse("2022-07-22 07:22",
                dtFormat));
        
        Sighting testSight2 = new Sighting();
        testSight2.setId(2);
        testSight2.setLocation_id(1); 
        testSight2.setSuperhero_id(1); 
        testSight2.setDate(LocalDateTime.parse("2022-07-23 07:23",
                dtFormat));
        
        Sighting testSight3 = new Sighting();
        testSight3.setId(3);
        testSight3.setLocation_id(2); 
        testSight3.setSuperhero_id(1); 
        testSight3.setDate(LocalDateTime.parse("2022-07-23 08:23",
                dtFormat));
        
        Sighting testSight4 = new Sighting();
        testSight4.setId(4);
        testSight4.setLocation_id(1); 
        testSight4.setSuperhero_id(2); 
        testSight4.setDate(LocalDateTime.parse("2022-07-23 08:23",
                dtFormat));
        
        Sighting createdSight1 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight1, 
                "Sighting1 should have been created.");
        
        Sighting createdSight2 
                = testSightingDao.createSighting(testSight2);
        assertNotNull(createdSight2, 
                "Sighting2 should have been created.");
        
        Sighting createdSight3 
                = testSightingDao.createSighting(testSight3);
        assertNotNull(createdSight3, 
                "Sighting3 should have been created.");
        
        Sighting createdSight4 
                = testSightingDao.createSighting(testSight4);
        assertNotNull(createdSight4, 
                "Sighting4 should have been created.");
        
        // attempt to get Sightings on date that lacks seconds 
        // and is formatted different than Sighting DTO (yyyy-MM-dd)
        DateTimeFormatter dFormat 
                = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDate noSightingDate = LocalDate.parse("07-25-2022", dFormat);
        
        List<Sighting> noSightings 
                = testSightingDao.getAllSightingsOnDate(noSightingDate);
        
        assertTrue(noSightings.isEmpty(), "List of Sightings "
                + "should be empty, there are no Sightings for "
                + "that date.");
    }
    
    @Test
    public void testGetAllSightingsOnDate() {
        DateTimeFormatter dtFormat
                = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.parse("2022-07-22 07:22",
                dtFormat));
        
        Sighting testSight2 = new Sighting();
        testSight2.setId(2);
        testSight2.setLocation_id(1); 
        testSight2.setSuperhero_id(1); 
        testSight2.setDate(LocalDateTime.parse("2022-07-23 07:23",
                dtFormat));
        
        Sighting testSight3 = new Sighting();
        testSight3.setId(3);
        testSight3.setLocation_id(2); 
        testSight3.setSuperhero_id(1); 
        testSight3.setDate(LocalDateTime.parse("2022-07-23 08:23",
                dtFormat));
        
        Sighting testSight4 = new Sighting();
        testSight4.setId(4);
        testSight4.setLocation_id(1); 
        testSight4.setSuperhero_id(2); 
        testSight4.setDate(LocalDateTime.parse("2022-07-23 08:23",
                dtFormat));
        
        Sighting createdSight1 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight1, 
                "Sighting1 should have been created.");
        
        Sighting createdSight2 
                = testSightingDao.createSighting(testSight2);
        assertNotNull(createdSight2, 
                "Sighting2 should have been created.");
        
        Sighting createdSight3 
                = testSightingDao.createSighting(testSight3);
        assertNotNull(createdSight3, 
                "Sighting3 should have been created.");
        
        Sighting createdSight4 
                = testSightingDao.createSighting(testSight4);
        assertNotNull(createdSight4, 
                "Sighting4 should have been created.");
        
        // GET SIGHTINGS
        DateTimeFormatter dFormat 
                = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date1 = LocalDate.parse("2022-07-22", dFormat);
        LocalDate date2 = LocalDate.parse("2022-07-23", dFormat);
        
        List<Sighting> date1Sightings 
                = testSightingDao.getAllSightingsOnDate(date1);
        List<Sighting> date2Sightings
                = testSightingDao.getAllSightingsOnDate(date2);
        
        assertFalse(date1Sightings.isEmpty(), "List of Sightings "
                + "on date1 should not be empty.");
        assertFalse(date2Sightings.isEmpty(), "List of Sightings "
                + "on date2 should not be empty.");
        assertTrue(date1Sightings.size() == 1, "List of Sightings "
                + "on date1 should contain 1 Sighting.");
        assertTrue(date2Sightings.size() == 3, "List of Sightings "
                + "on date2 should contain 3 Sightings.");
    }
    
    
    /*
     * getAllSightingLocationsForSuperhero:
     * - test method using superheroId that does not exist.
     * - test method using superhero with no sightings.
     * - test method using superhero 2 sighting locations.
     */
    @Test
    public void testGetAllSightingLocationsForSuperheroInvalidId() {
        DateTimeFormatter dtFormat
                = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.parse("2022-07-22 07:22",
                dtFormat));
        
        Sighting testSight2 = new Sighting();
        testSight2.setId(2);
        testSight2.setLocation_id(1); 
        testSight2.setSuperhero_id(1); 
        testSight2.setDate(LocalDateTime.parse("2022-07-23 07:23",
                dtFormat));
        
        Sighting testSight3 = new Sighting();
        testSight3.setId(3);
        testSight3.setLocation_id(2); 
        testSight3.setSuperhero_id(1); 
        testSight3.setDate(LocalDateTime.parse("2022-07-23 08:23",
                dtFormat));
        
        Sighting testSight4 = new Sighting();
        testSight4.setId(4);
        testSight4.setLocation_id(1); 
        testSight4.setSuperhero_id(2); 
        testSight4.setDate(LocalDateTime.parse("2022-07-23 08:23",
                dtFormat));
        
        Sighting createdSight1 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight1, 
                "Sighting1 should have been created.");
        
        Sighting createdSight2 
                = testSightingDao.createSighting(testSight2);
        assertNotNull(createdSight2, 
                "Sighting2 should have been created.");
        
        Sighting createdSight3 
                = testSightingDao.createSighting(testSight3);
        assertNotNull(createdSight3, 
                "Sighting3 should have been created.");
        
        Sighting createdSight4 
                = testSightingDao.createSighting(testSight4);
        assertNotNull(createdSight4, 
                "Sighting4 should have been created.");
        
        // GET LOCATIONS OF SIGHTINGS FOR BOTH TEST HEROS
        List<Location> locationsForTestHero1
                = testSightingDao.getAllSightingLocationsForSuperhero(3);
        
        assertTrue(locationsForTestHero1.isEmpty(), "List of "
                + "Locations for Superhero id that does not exist "
                + "should be empty.");
    }
    
    @Test
    public void testGetAllSightingLocationsForSuperheroNoSightings() {
        DateTimeFormatter dtFormat
                = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.parse("2022-07-22 07:22",
                dtFormat));
        
        Sighting testSight2 = new Sighting();
        testSight2.setId(2);
        testSight2.setLocation_id(1); 
        testSight2.setSuperhero_id(1); 
        testSight2.setDate(LocalDateTime.parse("2022-07-23 07:23",
                dtFormat));
        
        Sighting testSight3 = new Sighting();
        testSight3.setId(3);
        testSight3.setLocation_id(2); 
        testSight3.setSuperhero_id(1); 
        testSight3.setDate(LocalDateTime.parse("2022-07-23 08:23",
                dtFormat));
        
//        Sighting testSight4 = new Sighting();
//        testSight4.setId(4);
//        testSight4.setLocation_id(1); 
//        testSight4.setSuperhero_id(2); 
//        testSight4.setDate(LocalDateTime.parse("2022-07-23 08:23",
//                dtFormat));
        
        Sighting createdSight1 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight1, 
                "Sighting1 should have been created.");
        
        Sighting createdSight2 
                = testSightingDao.createSighting(testSight2);
        assertNotNull(createdSight2, 
                "Sighting2 should have been created.");
        
        Sighting createdSight3 
                = testSightingDao.createSighting(testSight3);
        assertNotNull(createdSight3, 
                "Sighting3 should have been created.");
        
//        Sighting createdSight4 
//                = testSightingDao.createSighting(testSight4);
//        assertNotNull(createdSight4, 
//                "Sighting4 should have been created.");
        
        // GET LOCATIONS OF SIGHTINGS FOR BOTH TEST HEROS
        List<Location> locationsForTestHero1
                = testSightingDao.getAllSightingLocationsForSuperhero(1);
        List<Location> locationsForTestHero2
                = testSightingDao.getAllSightingLocationsForSuperhero(2);
        
        assertTrue(locationsForTestHero1.size() == 2, "Size of list "
                + "of Locations for testHero1 Sightings should be 2.");
        assertTrue(locationsForTestHero2.isEmpty(), "List "
                + "of Locations for testHero2 Sightings should be "
                + "empty, testHero2 has no Sightings recorded.");
    }
    
    @Test
    public void testGetAllSightingLocationsForSuperhero() {
        DateTimeFormatter dtFormat
                = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.parse("2022-07-22 07:22",
                dtFormat));
        
        Sighting testSight2 = new Sighting();
        testSight2.setId(2);
        testSight2.setLocation_id(1); 
        testSight2.setSuperhero_id(1); 
        testSight2.setDate(LocalDateTime.parse("2022-07-23 07:23",
                dtFormat));
        
        Sighting testSight3 = new Sighting();
        testSight3.setId(3);
        testSight3.setLocation_id(2); 
        testSight3.setSuperhero_id(1); 
        testSight3.setDate(LocalDateTime.parse("2022-07-23 08:23",
                dtFormat));
        
        Sighting testSight4 = new Sighting();
        testSight4.setId(4);
        testSight4.setLocation_id(1); 
        testSight4.setSuperhero_id(2); 
        testSight4.setDate(LocalDateTime.parse("2022-07-23 08:23",
                dtFormat));
        
        Sighting createdSight1 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight1, 
                "Sighting1 should have been created.");
        
        Sighting createdSight2 
                = testSightingDao.createSighting(testSight2);
        assertNotNull(createdSight2, 
                "Sighting2 should have been created.");
        
        Sighting createdSight3 
                = testSightingDao.createSighting(testSight3);
        assertNotNull(createdSight3, 
                "Sighting3 should have been created.");
        
        Sighting createdSight4 
                = testSightingDao.createSighting(testSight4);
        assertNotNull(createdSight4, 
                "Sighting4 should have been created.");
        
        // GET LOCATIONS OF SIGHTINGS FOR BOTH TEST HEROS
        List<Location> locationsForTestHero1
                = testSightingDao.getAllSightingLocationsForSuperhero(1);
        List<Location> locationsForTestHero2
                = testSightingDao.getAllSightingLocationsForSuperhero(2);
        
        assertTrue(locationsForTestHero1.size() == 2, "Size of list "
                + "of Locations for testHero1 Sightings should be 2.");
        assertTrue(locationsForTestHero2.size() == 1, "Size of list "
                + "of Locations for testHero2 Sightings should be 1.");
        
        assertTrue(locationsForTestHero1.contains(
                testLocationDao.getLocationById(1)), 
                "List of Locations for testHero1 should contain "
                        + "Location with id 1.");
        assertTrue(locationsForTestHero1.contains(
                testLocationDao.getLocationById(2)), 
                "List of Locations for testHero1 should contain "
                        + "Location with id 2.");
        
        assertTrue(locationsForTestHero2.contains(
                testLocationDao.getLocationById(1)), 
                "List of Locations for testHero2 should contain "
                        + "only Location with id 1.");
    }
    
    
    /*
     * getAllSightings:
     * - test getAllSightings with 2 Sightings in the database.
     * - test getAllSightings with empty database table (no Sightings).
     */
    @Test
    public void testGetAllSightings() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.now());
        
        Sighting testSight2 = new Sighting();
        testSight2.setId(2);
        testSight2.setLocation_id(2);
        testSight2.setSuperhero_id(2);
        testSight2.setDate(LocalDateTime.now());
        
        // getAllSightings from empty db table
        List<Sighting> testSights 
                = testSightingDao.getAllSightings();
        assertTrue(testSights.isEmpty(), "List of Sightings should "
                + "be empty, no Sightings are in the database.");
        
        // create and add Sightings to the database
        Sighting createdSight 
                = testSightingDao.createSighting(testSight);
        Sighting createdSight2 
                = testSightingDao.createSighting(testSight2);
        // check that the Sightings were created
        assertNotNull(createdSight, 
                "Sighting should have been created.");
        assertNotNull(createdSight2, 
                "Second Sighting should have been created.");
        
        testSights = testSightingDao.getAllSightings();
        
        assertFalse(testSights.isEmpty());
        assertTrue(testSights.contains(testSight), 
                "List of Sightings should contain testSight.");
        assertTrue(testSights.contains(testSight2),
                "List of Sightings should contain testSight2.");
    }

    
    /*
     * updateSighting:
     * - test updateSighting with valid id.
     * - test updateSighting with invalid id (Sighting doesn't exist).
     * - test updateSighting with null date field (date field is the 
     *   only field of type that is able to be declared null).
     * - test updateSighting with empty fields.
     */
    @Test
    public void testUpdateSighting() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.now());
        
        Sighting createdSight 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight, "Sighting should have been created.");
        
        Sighting updatedTestSight = new Sighting();
        updatedTestSight.setId(1);
        updatedTestSight.setLocation_id(2);
        updatedTestSight.setSuperhero_id(1);
        updatedTestSight.setDate(LocalDateTime.now());
        
        boolean isUpdated 
                = testSightingDao.updateSighting(updatedTestSight);
        Sighting retrievedSight
                = testSightingDao.getSightingById(1);
        
        assertTrue(isUpdated, "Method should return True if "
                + "Sighting was updated.");
        assertEquals(updatedTestSight, retrievedSight, 
                "Retrieved Sighting should be equal to the updated "
                        + "test Sighting.");
        assertFalse(retrievedSight.equals(testSight), 
                "Retrieved Sighting should not be equal to the "
                        + "original test Sighting.");
    }
    
    @Test
    public void testUpdateSightingInvalidId() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.now());
        
        Sighting createdSight 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight, "Sighting should have been created.");
        
        Sighting updatedTestSight = new Sighting();
        updatedTestSight.setId(2);
        updatedTestSight.setLocation_id(2);
        updatedTestSight.setSuperhero_id(1);
        updatedTestSight.setDate(LocalDateTime.now());
        
        boolean isUpdated 
                = testSightingDao.updateSighting(updatedTestSight);
        Sighting retrievedSight
                = testSightingDao.getSightingById(1);
        
        assertFalse(isUpdated, "Method should return False, "
                + "Sighting should not have updated.");
        assertEquals(testSight, retrievedSight, 
                "Retrieved Sighting should be equal to the original "
                        + "test Sighting.");
    }
    
    @Test
    public void testUpdateSightingNullDate() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.now());
        
        Sighting createdSight 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight, "Sighting should have been created.");
        
        Sighting updatedTestSight = new Sighting();
        updatedTestSight.setId(1);
        updatedTestSight.setLocation_id(1);
        updatedTestSight.setSuperhero_id(1);
        
        try {
            updatedTestSight.setDate(null); // this causes NullPointer
            testSightingDao.updateSighting(updatedTestSight);
            fail("Attempt to set required date field to null should "
                    + "throw Exception. Attempt to update Sighting "
                    + "using null value for required date field "
                    + "should also throw Exception.");
        } catch (NullPointerException e) {
            // passed
            Sighting retrievedSight
                = testSightingDao.getSightingById(1);
            assertEquals(testSight, retrievedSight, 
                "Retrieved Sighting should be equal to the original "
                        + "test Sighting.");
        }
    }
    
    @Test
    public void testUpdateSightingEmptyFK() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.now());
        
        Sighting createdSight 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight, "Sighting should have been created.");
        
        Sighting updatedTestSight = new Sighting();
        updatedTestSight.setId(1);
        //updatedTestSight.setLocation_id(1);
        updatedTestSight.setSuperhero_id(1);
        updatedTestSight.setDate(LocalDateTime.now());
        
        try {
            testSightingDao.updateSighting(updatedTestSight);
            fail("Attempt to update Sighting with empty required "
                    + "foreign key field should throw Exception.");
        } catch (DataIntegrityViolationException e) {
            // passed
            Sighting retrievedSight
                    = testSightingDao.getSightingById(1);
            assertEquals(testSight, retrievedSight, "Retrieved Sighting "
                    + "should be equal to the original Sighting.");
        }
    }
    
    @Test
    public void testUpdateSightingEmptyDate() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.now());
        
        Sighting createdSight 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight, "Sighting should have been created.");
        
        Sighting updatedTestSight = new Sighting();
        updatedTestSight.setId(1);
        updatedTestSight.setLocation_id(1);
        updatedTestSight.setSuperhero_id(1);
        //updatedTestSight.setDate(LocalDateTime.now());
        
        try {
            testSightingDao.updateSighting(updatedTestSight);
            fail("Attempt to update Sighting with empty (unset) date "
                    + "should throw Exception.");
        } catch (NullPointerException e) {
            // passed
            Sighting retrievedSight
                    = testSightingDao.getSightingById(1);
            assertEquals(testSight, retrievedSight, "Retrieved "
                    + "Sighting should be equal to the original "
                    + "Sighting.");
        }
    }

    
    /*
     * deleteSightingById:
     * - test deleteSightingById with valid id.
     * - test deleteSightingById with invalid id (Sighting does not exist).
     */
    @Test
    public void testDeleteSightingById() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.now());
        
        Sighting createdSight 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight, "Sighting should have been created.");
        
        boolean isDeleted 
                = testSightingDao.deleteSightingById(1);
        Sighting retrievedSight 
                = testSightingDao.getSightingById(1);
        
        assertTrue(isDeleted, "Method should return True, Sighting "
                + "should be deleted.");
        assertNull(retrievedSight, "Attempt to retrieve the deleted "
                + "Sighting should return null.");
    }
    
    @Test
    public void testDeleteSightingByInvalidId() {
        Sighting testSight = new Sighting();
        testSight.setId(1);
        testSight.setLocation_id(1); 
        testSight.setSuperhero_id(1); 
        testSight.setDate(LocalDateTime.now());
        
        Sighting createdSight 
                = testSightingDao.createSighting(testSight);
        assertNotNull(createdSight, "Sighting should have been created.");
        
        // attempt to delete Sighting that doesn't exist
        boolean isDeleted 
                = testSightingDao.deleteSightingById(2);
        Sighting retrievedSight 
                = testSightingDao.getSightingById(1);
        
        assertFalse(isDeleted, "Method should return False, testSight "
                + "with id = 1 should not be deleted.");
        assertEquals(testSight, retrievedSight, 
                "Test Sighting should have been returned from "
                        + "retrieval.");
    }
    
}
