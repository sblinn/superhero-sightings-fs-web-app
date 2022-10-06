package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.Application;
import com.sblinn.superherosightings.dto.Superhero;
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
@SpringBootTest(classes = Application.class)
@Transactional
public class SuperheroDaoDBTest {
    
    @Autowired
    private SuperheroDao testSuperheroDao;
    
    public SuperheroDaoDBTest() {
    }
    
    @BeforeEach
    public void setUp() {
        List<Superhero> superheros = 
                testSuperheroDao.getAllSuperheros();
        for (Superhero hero : superheros) {
            testSuperheroDao.deleteSuperheroById(hero.getId());
        }
    }
    
    @AfterEach
    public void tearDown() {
        List<Superhero> superheros = testSuperheroDao.getAllSuperheros();
        for (Superhero hero : superheros) {
            testSuperheroDao.deleteSuperheroById(hero.getId());
        }
    }
    
    
    
    /*
     * createSuperhero:
     * - test createSuperhero with id and test getSuperheroById.
     * - test createSuperhero with without provided id (db generates id).
     * - test createSuperhero with duplicate id.
     * - test createSuperhero with missing fields:
     *   - test with null fields.
     *     - test with null name field (required).
     *   - test with empty required/unrequired fields.
     *     - test with empty/unset name field.
     */
    @Test
    public void testCreateAndGetSuperhero() {
        Superhero testHero = new Superhero();
        testHero.setId(1);
        testHero.setName("Superman");
        testHero.setDescription("Guy with superpowers.");
        
        Superhero createdHero
                = testSuperheroDao.createSuperhero(testHero);
        
        assertNotNull(createdHero, "Method should not return null, "
                + "Superhero should have been created and returned.");
        
        Superhero retrievedHero
                = testSuperheroDao.getSuperheroById(1);
        
        assertEquals(testHero, retrievedHero, "Retrieved Superhero "
                + "should be equal to the test Superhero.");
        assertEquals(testHero.getId(), retrievedHero.getId(),
                "Id should be equal.");
        assertEquals(testHero.getName(), retrievedHero.getName(),
                "Name should be equal.");
        assertEquals(testHero.getDescription(), 
                retrievedHero.getDescription(),
                "Description should be equal.");
    }
    
    @Test
    @Transactional
    public void testCreateSuperheroGenerateId() {
        Superhero testHero = new Superhero();
        //testHero.setId(1);
        testHero.setName("Superman");
        testHero.setDescription("Guy with superpowers.");
        
        Superhero createdHero
                = testSuperheroDao.createSuperhero(testHero);
        
        assertNotNull(createdHero, "Method should not return null, "
                + "Superhero should have been created and returned.");
        
        int generatedId = createdHero.getId();
        
        assertTrue(generatedId != 0);
        
        Superhero retrievedHero
                = testSuperheroDao.getSuperheroById(generatedId);
        
        assertEquals(generatedId, retrievedHero.getId(),
                "Id should be equal.");
        assertEquals(testHero.getName(), retrievedHero.getName(),
                "Name should be equal.");
        assertEquals(testHero.getDescription(), 
                retrievedHero.getDescription(),
                "Description should be equal.");
    }
    
    @Test
    public void testCreateSuperheroDuplicateId() {
        Superhero testHero = new Superhero();
        testHero.setId(1);
        testHero.setName("Superman");
        testHero.setDescription("Guy with superpowers.");
        
        Superhero duplicateIdHero = new Superhero();
        duplicateIdHero.setId(1);
        duplicateIdHero.setName("Superwoman");
        duplicateIdHero.setDescription("Lady with superpowers.");
        
        Superhero createdHero
                = testSuperheroDao.createSuperhero(testHero);
        
        assertNotNull(createdHero, "Method should not return null, "
                + "Superhero should have been created and returned.");
        
        try {
            testSuperheroDao.createSuperhero(duplicateIdHero);
            fail("DuplicateKeyException should be thrown when "
                    + "attempting to create Superhero with duplicate "
                    + "primary key (id).");
        } catch (DuplicateKeyException e) {
            // passed
            Superhero retrievedHero
                    = testSuperheroDao.getSuperheroById(1);
            assertEquals(testHero, retrievedHero,
                    "Retrieved Superhero should be equal to the test "
                    + "Superhero.");
        }
    }
    
    @Test
    public void testCreateSuperheroNullRequiredField() {
        Superhero testHero = new Superhero();
        testHero.setId(1);
        testHero.setName(null);
        testHero.setDescription("Guy with superpowers.");
        
        try {
            testSuperheroDao.createSuperhero(testHero);
            fail("Attempt to create Superhero with null required "
                    + "name field should throw Exception.");
        } catch (DataIntegrityViolationException e) {
            // passed
        }
    }
    
    @Test
    public void testCreateSuperheroEmptyRequiredField() {
        Superhero testHero = new Superhero();
        testHero.setId(1);
        //testHero.setName("");
        testHero.setDescription("Guy with superpowers.");
        
        try {
            testSuperheroDao.createSuperhero(testHero);
            fail("Attempt to create Superhero without setting "
                    + "required name field should throw Exception.");
        } catch (DataIntegrityViolationException e) {
            // passed
            testHero.setName("");
            Superhero createdHero 
                    = testSuperheroDao.createSuperhero(testHero);
            assertNotNull(createdHero, "Method should not return null, "
                + "Superhero should still be have been created and "
                + "returned even with empty superpowers field.");
        }
    }
    

    /*
     * getSuperheroById:
     * - test getSuperheroById with valid id (see testCreateAndGetSuperhero).
     * - test getSuperheroById with invalid id (id that does not exist).
     */
    @Test
    public void testGetSuperheroByInvalidId() {
        Superhero testHero = new Superhero();
        testHero.setId(1);
        testHero.setName("Superman");
        testHero.setDescription("Guy with superpowers.");
        
        Superhero createdHero
                = testSuperheroDao.createSuperhero(testHero);
        
        assertNotNull(createdHero, "Method should not return null, "
                + "Superhero should have been created and returned.");
        
        Superhero invalidRetrievedHero 
                = testSuperheroDao.getSuperheroById(2);

        assertNull(invalidRetrievedHero, "Method should return null, "
                + "there is no Superhero with the given id.");
        
        Superhero retrievedHero 
                = testSuperheroDao.getSuperheroById(1);

        assertEquals(testHero, retrievedHero, 
                "Superhero retrieved with id 1 should be equal to "
                        + "testHero.");
    }

    
    /*
     * getAllSuperheros:
     * - test getAllSuperheros with empty Superhero db Table.
     * - test getAllSuperheros with two Superheros in db.
     */
    @Test
    public void testGetAllSuperheros() {
        Superhero testHero = new Superhero();
        testHero.setId(1);
        testHero.setName("Superman");
        testHero.setDescription("Guy with superpowers.");
        
        Superhero testHero2 = new Superhero();
        testHero2.setId(2);
        testHero2.setName("Superwoman");
        testHero2.setDescription("Lady with superpowers.");
        
        // test getAllSuperheros with empty Superhero Table
        List<Superhero> testHeros 
                = testSuperheroDao.getAllSuperheros();
        
        assertTrue(testHeros.isEmpty(), "List of Superheros should "
                + "be empty, no Superheros in database.");
        
        // create 2 Superheros and test getAllSuperheros
        Superhero createdHero1 
                = testSuperheroDao.createSuperhero(testHero);
        Superhero createdHero2
                = testSuperheroDao.createSuperhero(testHero2);
        
        assertNotNull(createdHero1, 
                "First Superhero should have been created.");
        assertNotNull(createdHero2, 
                "Second Superhero should have been created.");
        
        testHeros = testSuperheroDao.getAllSuperheros();
        
        assertFalse(testHeros.isEmpty(), "List of Superheros should "
                + "not be empty.");
        assertTrue(testHeros.contains(testHero),
                "List of Superheros should contain first testHero.");
        assertTrue(testHeros.contains(testHero2), 
                "List of Superheros should contain second testHero.");
    }


    /*
     * updateSuperhero:
     * - test updateSuperhero with valid id.
     * - test updateSuperhero with invalid id (Superhero does not exist).
     * - test updateSuperhero with null required field.
     * - test updateSuperhero with empty required field.
     */
    @Test
    public void testUpdateSuperhero() {
        Superhero testHero = new Superhero();
        testHero.setId(1);
        testHero.setName("Superman");
        testHero.setDescription("Guy with superpowers.");
        
        // create Superhero to update
        Superhero createdHero
                = testSuperheroDao.createSuperhero(testHero);
        assertNotNull(createdHero, "Method should not return null, "
                + "Superhero should have been created and returned.");
        
        Superhero updatedTestHero = new Superhero();
        updatedTestHero.setId(1);
        updatedTestHero.setName("Superwoman");
        updatedTestHero.setDescription("Lady with superpowers.");
        
        boolean isUpdated
                = testSuperheroDao.updateSuperhero(updatedTestHero);
        Superhero retrievedHero 
                = testSuperheroDao.getSuperheroById(1);
        
        assertTrue(isUpdated, "Method should return True, Superhero "
                + "should be updated.");
        
        assertEquals(updatedTestHero.getId(), retrievedHero.getId(),
                "Retrieved Hero id should be equal to updatedTestHero.");
        assertEquals(updatedTestHero.getName(), retrievedHero.getName(),
                "Retrieved Hero name should be equal to updatedTestHero.");
        assertEquals(updatedTestHero.getDescription(),
                retrievedHero.getDescription(),
                "Retrieved Hero description should be equal to "
                        + "updatedTestHero.");
    }
    
    @Test
    public void testUpdateSuperheroInvalidId() {
        Superhero testHero = new Superhero();
        testHero.setId(1);
        testHero.setName("Superman");
        testHero.setDescription("Guy with superpowers.");
        
        // create Superhero to update
        Superhero createdHero
                = testSuperheroDao.createSuperhero(testHero);
        assertNotNull(createdHero, "Method should not return null, "
                + "Superhero should have been created and returned.");
        
        Superhero updatedTestHero = new Superhero();
        updatedTestHero.setId(2);
        updatedTestHero.setName("Superwoman");
        updatedTestHero.setDescription("Lady with superpowers.");
        
        assertFalse(testSuperheroDao.updateSuperhero(updatedTestHero), 
                    "Attempt to update Superhero with ID that doesn't "
                            + "exist should return false, no update "
                            + "should occur.");

        Superhero retrievedHero
                = testSuperheroDao.getSuperheroById(1);
        assertEquals(testHero, retrievedHero,
                "Retrieved Superhero should be equal to the "
                + "original testHero.");        
    }
    
    @Test
    public void testUpdateSuperheroNullRequiredField() {
        Superhero testHero = new Superhero();
        testHero.setId(1);
        testHero.setName("Superman");
        testHero.setDescription("Guy with superpowers.");
        
        // create Superhero to update
        Superhero createdHero
                = testSuperheroDao.createSuperhero(testHero);
        assertNotNull(createdHero, "Method should not return null, "
                + "Superhero should have been created and returned.");
        
        Superhero updatedTestHero = new Superhero();
        updatedTestHero.setId(1);
        updatedTestHero.setName(null);
        updatedTestHero.setDescription("Lady with superpowers.");
        
        try {
            testSuperheroDao.updateSuperhero(updatedTestHero);
            fail("Attempt to update Superhero using null require "
                    + "field value should throw Exception.");
        } catch (DataIntegrityViolationException e) {
            // passed 
            Superhero retrievedHero
                    = testSuperheroDao.getSuperheroById(1);
            assertEquals(testHero, retrievedHero,
                    "Retrieved Superhero should be equal to the "
                    + "original testHero, no update should occur.");        
        }
    }
    
    @Test
    public void testUpdateSuperheroEmptyRequiredField() {
        Superhero testHero = new Superhero();
        testHero.setId(1);
        testHero.setName("Superman");
        testHero.setDescription("Guy with superpowers.");
        
        // create Superhero to update
        Superhero createdHero
                = testSuperheroDao.createSuperhero(testHero);
        assertNotNull(createdHero, "Method should not return null, "
                + "Superhero should have been created and returned.");
        
        Superhero updatedTestHero = new Superhero();
        updatedTestHero.setId(1);
        //updatedTestHero.setName("");
        updatedTestHero.setDescription("Lady with superpowers.");
        
        try {
            testSuperheroDao.updateSuperhero(updatedTestHero);
            fail("Attempt to update Superhero without setting "
                    + "required field value should throw Exception.");
        } catch (DataIntegrityViolationException e) {
            // passed 
            Superhero retrievedHero
                    = testSuperheroDao.getSuperheroById(1);
            assertEquals(testHero, retrievedHero,
                    "Retrieved Superhero should be equal to the "
                    + "original testHero, no update should occur.");
            
            updatedTestHero.setName("");
            boolean isUpdated 
                    = testSuperheroDao.updateSuperhero(updatedTestHero);
            retrievedHero = testSuperheroDao.getSuperheroById(1);
            
            assertTrue(isUpdated, "Method should return True, "
                    + "Superhero should still be able to update with "
                    + "an empty String field value.");
            assertEquals(updatedTestHero, retrievedHero,
                    "Retrieved Superhero should be equal to the "
                    + "updated testHero, update should occur.");
        }
    }

    
    /*
     * deleteSuperheroById:
     * - test deleteSuperheroById with valid id.
     * - test deleteSuperheroById with invalid id (id does not exist).
     */
    @Test
    public void testDeleteSuperheroById() {
        Superhero testHero = new Superhero();
        testHero.setId(1);
        testHero.setName("Superman");
        testHero.setDescription("Guy with superpowers.");
        
        // create Superhero to update
        Superhero createdHero
                = testSuperheroDao.createSuperhero(testHero);
        assertNotNull(createdHero, "Method should not return null, "
                + "Superhero should have been created and returned.");
        
        boolean isDeleted 
                = testSuperheroDao.deleteSuperheroById(1);
        Superhero retrievedHero
                = testSuperheroDao.getSuperheroById(1);
        List<Superhero> testHeros 
                = testSuperheroDao.getAllSuperheros();
        
        assertTrue(isDeleted, "Method should return True, Superhero "
                + "should be deleted.");
        assertNull(retrievedHero, "Method should return null, no"
                + " Superhero should still exist with id 1.");
        assertTrue(testHeros.isEmpty(), "Retrieved list of "
                + "Superheros should be empty, all Superheros have "
                + "been deleted from the database.");
    }
    
    @Test
    public void testDeleteSuperheroByInvalidId() {
        Superhero testHero = new Superhero();
        testHero.setId(1);
        testHero.setName("Superman");
        testHero.setDescription("Guy with superpowers.");
        
        // create Superhero to update
        Superhero createdHero
                = testSuperheroDao.createSuperhero(testHero);
        assertNotNull(createdHero, "Method should not return null, "
                + "Superhero should have been created and returned.");
        
        // attempt to delete with invalid id (no superhero exists
        // with the id)
        boolean isDeleted 
                = testSuperheroDao.deleteSuperheroById(2);
        Superhero retrievedHero
                = testSuperheroDao.getSuperheroById(1);
        List<Superhero> testHeros 
                = testSuperheroDao.getAllSuperheros();
        
        assertFalse(isDeleted, "Method should return False, no "
                + "Superhero should be deleted.");
        assertNotNull(retrievedHero, "Method should not return null, "
                + "Superhero should still exist with id 1.");
        assertFalse(testHeros.isEmpty(), "Retrieved list of "
                + "Superheros should not be empty.");
    }
    
}
