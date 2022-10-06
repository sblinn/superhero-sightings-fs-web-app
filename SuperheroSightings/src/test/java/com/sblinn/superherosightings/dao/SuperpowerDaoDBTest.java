package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dto.Superhero;
import com.sblinn.superherosightings.dto.Superpower;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
public class SuperpowerDaoDBTest {
    
    @Autowired
    private SuperpowerDao testSuperpowerDao;
    
    @Autowired
    private SuperheroDao testSuperheroDao;
    
    public SuperpowerDaoDBTest() {
    }

    @BeforeEach
    public void setUp() {
        List<Superpower> powers = testSuperpowerDao.getAllSuperpowers();
        for (Superpower power : powers) {
            testSuperpowerDao.deleteSuperpowerById(power.getId());
        }
        
        List<Superhero> heros = testSuperheroDao.getAllSuperheros();
        for (Superhero hero : heros) {
            testSuperheroDao.deleteSuperheroById(hero.getId());
        }
        
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
    }
    
    @AfterEach
    public void tearDown() {
        List<Superpower> powers = testSuperpowerDao.getAllSuperpowers();
        for (Superpower power : powers) {
            testSuperpowerDao.deleteSuperpowerById(power.getId());
        }
        
        List<Superhero> heros = testSuperheroDao.getAllSuperheros();
        for (Superhero hero : heros) {
            testSuperheroDao.deleteSuperheroById(hero.getId());
        }
    }
    
    
    
    /*
     * createSuperpower:
     * - test createSuperpower with id provided and test getSuperpowerById.
     * - test createSuperpower with duplicate id.
     * - test createSuperpower without id provided. (db sets id)
     * - test createSuperpower with null fields (required/unrequired).
     * - test createSuperpower with empty fields (required/unrequired).
     */
    @Test
    public void testCreateAndGetSuperpower() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        
        assertEquals(testPower, createdPower, 
                "Superpower returned upon creation should equal "
                        + "testPower.");
        
        Superpower retrievedPower
                = testSuperpowerDao.getSuperpowerById(1);
        
        assertEquals(testPower, retrievedPower,
                "Retrieved Superpower should be equal to testPower.");
    }
    
    @Test
    public void testCreateSuperpowerDuplicateId() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superpower testPower2 = new Superpower();
        testPower2.setId(1);
        testPower2.setName("Flight");
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        Superpower retrievedPower
                = testSuperpowerDao.getSuperpowerById(1);

        assertNotNull(createdPower, "Method should not return null.");
        assertEquals(testPower, retrievedPower,
                "testPower should have been created.");  
        
        try {
            testSuperpowerDao.createSuperpower(testPower2);
            fail("Attempt to create Superpower with duplicate "
                    + "primary key id should throw Exception.");
        } catch (DuplicateKeyException e) {
            // passed
        }
    }
    
    @Test
    public void testCreateSuperpowerGenerateId() {
        Superpower testPower = new Superpower();
        //testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        int generatedId = createdPower.getId();
        Superpower retrievedPower
                = testSuperpowerDao.getSuperpowerById(generatedId);

        assertNotNull(createdPower, "Method should not return null.");
        assertNotNull(generatedId, "Method should return with "
                + "generated id.");
        
        testPower.setId(generatedId);
        
        assertEquals(testPower, retrievedPower,
                "testPower should have been created.");  
    }
    
    @Test
    public void testCreateSuperpowerNullFields() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        //testPower.setName("Lazer Eyes");
        
        testPower.setName(null); //required field
        
        try {
            testSuperpowerDao.createSuperpower(testPower);
            fail("Attempting to create Superpower with null values "
                    + "in required fields should cause Exception to "
                    + "be thrown.");
        } catch (DataIntegrityViolationException e) {
            // passed
        }
    }
    
    @Test
    public void testCreateSuperpowerEmptyFields() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        //testPower.setName("Lazer Eyes");
        
        testPower.setName(""); //required field
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);

        assertNotNull(createdPower, "Superpower with empty required "
                + "fields should still be able to be created.");
        assertEquals(testPower, createdPower, 
                "Superpower returned upon creation should equal "
                        + "testPower.");
    }
    
    
    /*
     * getSuperpowerById:
     * - test getSuperpowerById with valid id (see testCreateAndGetSuperpower).
     * - test getSuperpowerBy Id with invalid id (id does not exist).
     */
    @Test
    public void testGetSuperpowerByInvalidId() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        
        assertNotNull(createdPower, "Superpower should have been "
                + "created.");
        
        Superpower retrievedPower 
                = testSuperpowerDao.getSuperpowerById(2);
        
        assertNull(retrievedPower, "Method should return null "
                + "when id that does not exist is used to retrieve "
                + "Superpower.");
    }

    
    /*
     * getAllSuperpowers:
     * - test getAllSuperpowers with two Superpowers in database.
     * - test getAllSuperpowers with empty database table. (no Superpowers)
     */
    @Test
    public void testGetAllSuperpowers() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superpower testPower2 = new Superpower();
        testPower2.setId(2);
        testPower2.setName("Flight");
        
        // confirm that method works properly with empty database table
        List<Superpower> testPowers 
                = testSuperpowerDao.getAllSuperpowers();
        
        assertTrue(testPowers.isEmpty(), "List of Superpowers should "
                + "be empty, database table contains no Superpowers.");
        
        // add Superpowers to the table 
        Superpower createdPower1 
                = testSuperpowerDao.createSuperpower(testPower);
        Superpower createdPower2
                = testSuperpowerDao.createSuperpower(testPower2);

        assertNotNull(createdPower1, "Method should not return null.");
        assertNotNull(createdPower2, "Method should not return null.");
        
        testPowers = testSuperpowerDao.getAllSuperpowers();
        
        assertFalse(testPowers.isEmpty(), "List of Superpowers "
                + "should not be empty.");
        assertTrue(testPowers.contains(testPower), 
                "List of Superpowers should contain testPower.");
        assertTrue(testPowers.contains(testPower2), 
                "List of Superpowers should contain testPower2");
    }


    /*
     * updateSuperpower:
     * - test updateSuperpower with valid id.
     * - test updateSuperpower with invalid id (no Superpower exists with that id).
     * - test updateSuperpower with null fields (required/unrequired).
     * - test updateSuperpower with empty fields (required/unrequired).
     */
    @Test
    public void testUpdateSuperpower() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        Superhero testHero2 = testSuperheroDao.getSuperheroById(2);
        
        List<Superhero> testSuperheros = new ArrayList<>();
        testSuperheros.add(testHero1);
        
        testPower.setSuperheros(testSuperheros);
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        assertNotNull(createdPower, "Superpower should have "
                + "been created.");
        
        // create an updated version of testPower to update the DB.
        // change name and list of Superheroes with Superpower
        Superpower updatedTestPower = new Superpower();
        updatedTestPower.setId(1);
        updatedTestPower.setName("Lazer Finger");
        
        List<Superhero> updatedTestSuperheroes = new ArrayList<>();
        updatedTestSuperheroes.add(testHero1);
        updatedTestSuperheroes.add(testHero2);
        
        updatedTestPower.setSuperheros(updatedTestSuperheroes);
        
        boolean isUpdated 
                = testSuperpowerDao.updateSuperpower(updatedTestPower);
        Superpower retrievedPower 
                = testSuperpowerDao.getSuperpowerById(1);
        
        List<Superhero> retrievedSuperheros 
                = testSuperpowerDao.getAllSuperherosWithSuperpower(
                        testPower.getId());
        
        assertTrue(isUpdated, "Method should return True.");
        assertEquals(updatedTestPower, retrievedPower, 
                "Superpower's fields should have been updated.");
        
        assertEquals(retrievedSuperheros, updatedTestPower.getSuperheros(), 
                "Retrieved list of Superheros with superpower should be "
                        + "the same as the list of superheroes in the "
                        + "test Superpower.");
        assertTrue(retrievedSuperheros.contains(testHero1), 
                "Retrieved list of Superheroes should contain testHero1.");
        assertTrue(retrievedSuperheros.contains(testHero2), 
                "Retrieved list of Superheroes should contain testHero2.");
        
    }
    
    @Test
    public void testUpdateSuperpowerInvalidId() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        assertNotNull(createdPower, "Superpower should have "
                + "been created.");
        
        testPower.setId(2);
        testPower.setName("Lazer Finger");
        
        boolean isUpdated 
                = testSuperpowerDao.updateSuperpower(testPower);
        Superpower retrievedPower 
                = testSuperpowerDao.getSuperpowerById(1);
        
        assertFalse(isUpdated, "Method should return False, no "
                + "update should occur, id does not exist.");
        assertNotEquals(testPower, retrievedPower, 
                "Superpower's fields should not have been updated.");
    }
    
    @Test
    public void testUpdateSuperpowerNullFields() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        assertNotNull(createdPower, "Superpower should have "
                + "been created.");
        
        testPower.setName(null);
        
        try {
            testSuperpowerDao.updateSuperpower(testPower);
            fail("Attempt to update Superpower's required fields "
                    + "with null values should throw Exception.");
        } catch (DataIntegrityViolationException e) {
            // passed
        }
    }
    
    @Test
    public void testUpdateSuperpowerEmptyFields() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        assertNotNull(createdPower, "Superpower should have "
                + "been created.");
        
        testPower.setName("");
        boolean isUpdated 
                = testSuperpowerDao.updateSuperpower(testPower);
        Superpower retrievedPower 
                = testSuperpowerDao.getSuperpowerById(1);
        
        assertTrue(isUpdated, "Method should return True, required "
                + "fields can be updated with empty values.");
        assertEquals(testPower, retrievedPower, 
                "Superpower's fields should have been updated.");
    }
    

    /*
     * deleteSuperpowerById:
     * - test deleteSuperpowerById with valid id.
     * - test deleteSuperpowerById with invalid id (no superpower 
     *   exists with that id).
     */
    @Test
    public void testDeleteSuperpowerById() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        Superhero testHero2 = testSuperheroDao.getSuperheroById(2);
        
        List<Superhero> testSuperheros = new ArrayList<>();
        testSuperheros.add(testHero1);
        testSuperheros.add(testHero2);
        
        testPower.setSuperheros(testSuperheros);
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        assertNotNull(createdPower, "Superpower should have "
                + "been created.");
        
        boolean isDeleted = testSuperpowerDao.deleteSuperpowerById(1);
        Superpower retrievedPower 
                = testSuperpowerDao.getSuperpowerById(1);
        List<Superhero> retrievedSuperherosWithPower 
                = testSuperpowerDao.getAllSuperherosWithSuperpower(
                        testPower.getId());
        
        assertTrue(isDeleted, "Method should return True, testPower "
                + "should be deleted.");
        
        assertNull(retrievedPower, "Superpower with id 1 should be "
                + "deleted.");
        
        assertTrue(retrievedSuperherosWithPower.isEmpty(), 
                "List of Superheros with Superpower should be empty, "
                        + "deletion should have occurred in bridge table "
                        + "as well.");
    }
    
    @Test
    public void testDeleteSuperpowerByInvalidId() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        assertNotNull(createdPower, "Superpower should have "
                + "been created.");
        
        boolean isDeleted = testSuperpowerDao.deleteSuperpowerById(2);
        Superpower retrievedPower 
                = testSuperpowerDao.getSuperpowerById(1);
        
        assertFalse(isDeleted, "Method should return False, "
                + "testPower should not be deleted.");
        
        assertNotNull(retrievedPower, "Superpower with id 1 should "
                + "not be deleted.");
    }

    
    
    /*
     * addSuperpowerForSuperhero():
     * - test with valid Superhero & Superpower
     *      - test getSuperpowersForSuperhero() and 
     *          getAllSuperherosWithSuperpower to check if successful.
     * - test with null Superpower & null Superhero.
     * - test add duplicate Superpower to Superhero.
     */
    
    @Test
    public void testAddSuperpowerAndGetSuperpowersForSuperhero() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        Superhero testHero2 = testSuperheroDao.getSuperheroById(2);
        
        // leave list of superheroes empty for now, add later
        List<Superhero> testSuperheroesWithPower = new ArrayList<>();
        testPower.setSuperheros(testSuperheroesWithPower);
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        assertNotNull(createdPower, "Superpower should have "
                + "been created.");
        
        // add superheroes
        boolean isAdded = testSuperpowerDao
                .addSuperpowerForSuperhero(testPower, testHero1);
        assertTrue(isAdded, "TestPower should have been added for "
                + "TestHero1, method should return true.");
        
        isAdded = testSuperpowerDao
                .addSuperpowerForSuperhero(testPower, testHero2);
        assertTrue(isAdded, "TestPower should have been added for "
                + "testHero2, method should return true.");
        
        List<Superhero> retrievedTestSuperheroes 
                = testSuperpowerDao.getAllSuperherosWithSuperpower(
                        testPower.getId());
        Superpower retrievedPower = testSuperpowerDao
                .getSuperpowerById(testPower.getId());
        
        assertTrue(retrievedTestSuperheroes.size() == 2, 
                "List of Superheroes with test Superpower should contain "
                        + "2 Superheroes.");
        assertTrue(retrievedTestSuperheroes.contains(testHero1),
                "List of Superheroes with test Superpower should contain "
                        + "testHero1.");
        assertTrue(retrievedTestSuperheroes.contains(testHero2),
                "List of Superheroes with test Superpower should contain "
                        + "testHero2.");
        assertEquals(retrievedTestSuperheroes, 
                retrievedPower.getSuperheros(), 
                "List of Superheroes should be the same.");
        
        List<Superpower> testHero1Superpowers 
                = testSuperpowerDao.getSuperpowersForSuperhero(
                        testHero1.getId());
        
        assertTrue(testHero1Superpowers.size() == 1, "TestHero1's "
                + "list of Superpowers should contain 1 Superpower.");
        assertTrue(testHero1Superpowers.contains(retrievedPower), 
                "TestHero1's list of Superpowers should only contain "
                        + "testPower");
    }
    
    @Test
    public void testAddSuperpowerForSuperheroNullParameter() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        
        // leave list of superheroes empty for now, add later
        List<Superhero> testSuperheroesWithPower = new ArrayList<>();
        testPower.setSuperheros(testSuperheroesWithPower);
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        assertNotNull(createdPower, "Superpower should have "
                + "been created.");
        
        // add null superpower for testHero1
        boolean isAdded = testSuperpowerDao
                .addSuperpowerForSuperhero(null, testHero1);
        assertFalse(isAdded, "TestPower should not have been added for "
                + "testHero1, method should return false.");
        
        isAdded = testSuperpowerDao
                .addSuperpowerForSuperhero(testPower, null);
        assertFalse(isAdded, "Method should return false, no Superhero"
                + "to give Superpower to.");
        
        List<Superhero> retrievedTestSuperheroes 
                = testSuperpowerDao.getAllSuperherosWithSuperpower(
                        testPower.getId());
        Superpower retrievedPower = testSuperpowerDao
                .getSuperpowerById(testPower.getId());
        
        assertTrue(retrievedTestSuperheroes.isEmpty(), 
                "List of Superheroes with test Superpower should contain "
                        + "0 Superheroes.");
        assertEquals(retrievedTestSuperheroes, 
                retrievedPower.getSuperheros(), 
                "List of Superheroes should be the same.");
        
        List<Superpower> testHero1Superpowers 
                = testSuperpowerDao.getSuperpowersForSuperhero(
                        testHero1.getId());
        
        assertTrue(testHero1Superpowers.isEmpty(), "TestHero1's "
                + "list of Superpowers should contain no Superpower.");
    }
    
    @Test
    public void testAddSuperpowerForSuperheroDuplicateMember() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        
        List<Superhero> testSuperheroesWithPower = new ArrayList<>();
        testPower.setSuperheros(testSuperheroesWithPower);
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        assertNotNull(createdPower, "Superpower should have "
                + "been created.");
        
        boolean isAdded = testSuperpowerDao
                .addSuperpowerForSuperhero(testPower, testHero1);
        assertTrue(isAdded, "Superpower should have been added for "
                + "testHero1, method should return true.");
        isAdded = testSuperpowerDao
                .addSuperpowerForSuperhero(testPower, testHero1);
        assertFalse(isAdded, "Superpower should not have been added for "
                + "testHero1, method should return false.");
        
        List<Superhero> retrievedTestSuperheroes 
                = testSuperpowerDao.getAllSuperherosWithSuperpower(
                        testPower.getId());
        Superpower retrievedPower = testSuperpowerDao
                .getSuperpowerById(testPower.getId());
        
        assertTrue(retrievedTestSuperheroes.size() == 1, 
                "List of Superheroes with test Superpower should contain "
                        + "1 Superhero.");
        
        List<Superpower> testHero1Superpowers 
                = testSuperpowerDao.getSuperpowersForSuperhero(
                        testHero1.getId());
        
        assertTrue(testHero1Superpowers.size() == 1, "TestHero1's "
                + "list of Superpowers should contain 1 Superpower.");
        assertTrue(testHero1Superpowers.contains(retrievedPower), 
                "TestHero1's list of Superpowers should only contain "
                        + "testPower");
    }
        
    
    /*
     * getAllSuperherosWithSuperpower():
     * - test with Superpower that no Superheroes have 
     *      (does not have Superheroes).
     * - test with Superpower that Superheroes do have (has Superheroes).
     *      - see testAddSuperpowerAndGetSuperpowersForSuperhero().
     */
    
    @Test
    public void testGetAllSuperherosWithSuperpowerNoSuperheros() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        assertNotNull(createdPower, "Superpower should have "
                + "been created.");
        
        List<Superhero> testSuperheros 
                = testSuperpowerDao.getAllSuperherosWithSuperpower(
                        testPower.getId());
        
        assertTrue(testSuperheros.isEmpty(), "List of Superheroes with "
                + "the Superpower should be empty.");
    }
    
    @Test
    public void testGetAllSuperherosWithSuperpowerInvalidId() {
        List<Superhero> testHeros 
                = testSuperpowerDao.getAllSuperherosWithSuperpower(1);
        
        assertTrue(testHeros.isEmpty(), "Method should return an empty "
                + "list since no superpower exists with that Id.");
    }
    
    
    /*
     * getAllSuperpowersForSuperhero():
     * - test with Superhero that has multiple superpowers.
     * - test with Superhero that has no superpowers.
     * - test with invalid superheroId.
     */
    
    @Test
    public void testGetAllSuperpowersForSuperhero() {
        Superpower testPower1 = new Superpower();
        testPower1.setId(1);
        testPower1.setName("Lazer Eyes");
        
        Superpower testPower2 = new Superpower();
        testPower2.setId(2);
        testPower2.setName("Flight");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        
        Superpower createdPower1 
                = testSuperpowerDao.createSuperpower(testPower1);
        assertNotNull(createdPower1, "Superpower1 should have "
                + "been created.");
        Superpower createdPower2 
                = testSuperpowerDao.createSuperpower(testPower2);
        assertNotNull(createdPower2, "Superpower2 should have "
                + "been created.");
        
        
        boolean isAdded = testSuperpowerDao
                .addSuperpowerForSuperhero(testPower1, testHero1);
        assertTrue(isAdded, "Superpower1 should have been added for "
                + "testHero1, method should return true.");
        isAdded = testSuperpowerDao
                .addSuperpowerForSuperhero(testPower2, testHero1);
        assertTrue(isAdded, "Superpower2 should have been added for "
                + "testHero1, method should return true.");
        
        List<Superpower> testHero1Superpowers 
                = testSuperpowerDao.getSuperpowersForSuperhero(
                        testHero1.getId());
        
        Superpower retrievedTestPower1 
                = testSuperpowerDao.getSuperpowerById(testPower1.getId());
        Superpower retrievedTestPower2 
                = testSuperpowerDao.getSuperpowerById(testPower2.getId());
        
        assertTrue(testHero1Superpowers.size() == 2, "TestHero1's "
                + "list of Superpowers should contain 2 Superpowers.");
        assertTrue(testHero1Superpowers.contains(retrievedTestPower1), 
                "TestHero1's list of Superpowers should contain "
                        + "testPower1.");
        assertTrue(testHero1Superpowers.contains(retrievedTestPower2), 
                "TestHero1's list of Superpowers should contain "
                        + "testPower2.");
    }
    
    @Test
    public void testGetAllSuperpowersForSuperheroNoSuperpowers() {
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);

        List<Superpower> testHero1Superpowers 
                = testSuperpowerDao.getSuperpowersForSuperhero(
                        testHero1.getId());
        
        assertTrue(testHero1Superpowers.isEmpty(), "TestHero1's "
                + "list of Superpowers should contain 0 Superpowers.");
    }
    
    @Test
    public void testGetAllSuperpowersForSuperheroInvalidSuperheroId() {
        // using Id value 3 -- no Superhero exists with id 3
        List<Superpower> superpowers 
                = testSuperpowerDao.getSuperpowersForSuperhero(3);
     
        assertTrue(superpowers.isEmpty(), "List of superpowers for"
                + "Superhero with ID value 3 should be empty"
                + " no Superhero exists with ID 3.");
    }
    
    
    
    /*
     * deleteSuperpowerForSuperhero():
     * - test with valid inputs.
     * - test with Superhero that does not have Superpower
     */
    
    @Test 
    public void testDeleteSuperpowerForSuperhero() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        List<Superhero> testSuperheroesWithPower = new ArrayList<>();
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        Superhero testHero2 = testSuperheroDao.getSuperheroById(2);
        testSuperheroesWithPower.add(testHero1);
        testSuperheroesWithPower.add(testHero2);
        testPower.setSuperheros(testSuperheroesWithPower);
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        assertNotNull(createdPower, "Superpower should have "
                + "been created.");
        
        boolean isDeleted = 
                testSuperpowerDao.deleteSuperpowerForSuperhero(
                        testPower, testHero1.getId());
        assertTrue(isDeleted, "Method should return true, Superpower "
                + "should have been deleted for Superhero.");
        
        List<Superpower> testHero1Superpowers 
                = testSuperpowerDao.getSuperpowersForSuperhero(
                        testHero1.getId());
        
        List<Superhero> retrievedTestSuperheroes 
                = testSuperpowerDao.getAllSuperherosWithSuperpower(
                        testPower.getId());
        
        assertTrue(testHero1Superpowers.isEmpty(), 
                "List of Superpowers for testHero1 should be empty.");
        
        assertTrue(retrievedTestSuperheroes.size() == 1, 
                "List of Superheroes should be the same.");
        assertTrue(retrievedTestSuperheroes.contains(testHero2), 
                "List of Superheroes should contain testHero2.");
        assertFalse(retrievedTestSuperheroes.contains(testHero1), 
                "List of Superheroes should not contain testHero1, "
                        + "testHero1 should have been deleted.");
    }
    
    @Test 
    public void testDeleteSuperpowerNotExistForSuperhero() {
        Superpower testPower = new Superpower();
        testPower.setId(1);
        testPower.setName("Lazer Eyes");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        
        List<Superhero> testSuperheroesWithPower = new ArrayList<>();
        testPower.setSuperheros(testSuperheroesWithPower);
        
        Superpower createdPower 
                = testSuperpowerDao.createSuperpower(testPower);
        assertNotNull(createdPower, "Superpower should have "
                + "been created.");
        
        // attempt to delete a superpower for TestHero1 (has no superpowers)
        boolean isDeleted = 
                testSuperpowerDao.deleteSuperpowerForSuperhero(
                        testPower, testHero1.getId());
        assertFalse(isDeleted, "Method should return false, Superpower "
                + "should not have been deleted for Superhero.");
        
        List<Superpower> testHero1Superpowers 
                = testSuperpowerDao.getSuperpowersForSuperhero(
                        testHero1.getId());
        
        assertTrue(testHero1Superpowers.isEmpty(), 
                "List of Superpowers for testHero1 should be empty.");

    }
    
}
