package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dto.Organization;
import com.sblinn.superherosightings.dto.Superhero;
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
public class OrganizationDaoDBTest {
    
    @Autowired
    private OrganizationDao testOrgDao;
    
    @Autowired
    private SuperheroDao testSuperheroDao;
    
    
    public OrganizationDaoDBTest() {
    }

    @BeforeEach
    public void setUp() {
        List<Organization> orgs = testOrgDao.getAllOrganizations();
        for (Organization org : orgs) {
            testOrgDao.deleteOrganizationById(org.getId());
        }
        
        List<Superhero> superheros = testSuperheroDao.getAllSuperheros();
        for (Superhero hero : superheros) {
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
        List<Organization> orgs = testOrgDao.getAllOrganizations();
        for (Organization org : orgs) {
            testOrgDao.deleteOrganizationById(org.getId());
        }
        
        List<Superhero> superheros = testSuperheroDao.getAllSuperheros();
        for (Superhero hero : superheros) {
            testSuperheroDao.deleteSuperheroById(hero.getId());
        }
    }
    
    
    /*
     * createOrganization:
     * - test createOrganization with id provided and 
     *   test getOrganizationById.
     * - test createOrganization with duplicate id.
     * - test createOrganization without id provided (db generates id, 
     *   method sets and returns org with id).
     * - test createOrganization with null fields (required & unrequired fields).
     * - test createOrganization with empty fields (required & unrequired fields).
     */
    @Test
    public void testCreateAndGetOrganization() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testSuperheroDao.getSuperheroById(1));
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);
        
        Organization createdOrg = testOrgDao.createOrganization(testOrg);
        
        assertEquals(testOrg, createdOrg, 
                "Organization returned upon creation should equal "
                        + "testOrg.");

        Organization retrievedOrg = testOrgDao.getOrganizationById(1);
        
        List<Superhero> retrievedMembers 
                = retrievedOrg.getMembers();
        assertTrue(retrievedMembers.size() == 2, 
                "Members should contain 2 Superheros.");
        assertTrue(retrievedMembers.contains(
                testSuperheroDao.getSuperheroById(1)), 
                "Members should contain testHero1.");
        assertTrue(retrievedMembers.contains(
                testSuperheroDao.getSuperheroById(2)),
                "Members should contain testHero2.");
        
        assertEquals(testOrg.getId(), retrievedOrg.getId(),
                "Id should be equal.");
        assertEquals(testOrg.getName(), retrievedOrg.getName(),
                "Name should be equal.");
        assertEquals(testOrg.getDescription(), 
                retrievedOrg.getDescription(),
                "Description should be equal.");
        assertEquals(testOrg.getStreet_address(),
                retrievedOrg.getStreet_address(),
                "Address should be equal.");
        assertEquals(testOrg.getCity(), retrievedOrg.getCity(),
                "City should be equal.");
        assertEquals(testOrg.getCountry(), retrievedOrg.getCountry(),
                "Country should be equal.");
        assertEquals(testOrg.getMembers(), retrievedOrg.getMembers(),
                "Members list should be equal.");
        
        assertEquals(testOrg, retrievedOrg,
                "Organization retrieved should be equal to testOrg.");
    }
    
    @Test
    public void testCreateOrganizationGenerateId() {
        Organization testOrg = new Organization();
        //testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testSuperheroDao.getSuperheroById(1));
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);
        
        Organization createdOrg = testOrgDao.createOrganization(testOrg);
        int generatedId = createdOrg.getId();
        
        testOrg.setId(generatedId);
        
        assertEquals(testOrg, createdOrg, 
                "Organization returned upon creation should equal "
                        + "testOrg.");
        
        Organization retrievedOrg 
                = testOrgDao.getOrganizationById(generatedId);
        
        assertEquals(testOrg, retrievedOrg,
                "Organization retrieved should be equal to testOrg.");
    }
    
    @Test
    public void testCreateOrganizationDuplicateId() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testSuperheroDao.getSuperheroById(1));
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);
        
        Organization testOrg2 = new Organization();
        testOrg2.setId(1);
        testOrg2.setName("Avengerrrs");
        testOrg2.setDescription("Tigers who fight bears.");
        testOrg2.setStreet_address("100 Cave Way");
        testOrg2.setCity("Montevideo");
        testOrg2.setCountry("US");
        testOrg.setMembers(members);
        
        testOrgDao.createOrganization(testOrg);
        Organization retrievedOrg 
                = testOrgDao.getOrganizationById(1);
        
        // confirm that first Organization was added
        assertNotNull(retrievedOrg, "Organization should have been "
                + "added to the database.");
        
        try {
            testOrgDao.createOrganization(testOrg2);
            fail("Using duplicate primary key id should throw "
                    + "Exception.");
        } catch (DuplicateKeyException e) {
            // passed
        }
    }
    
    @Test
    public void testCreateOrganizationNullFields() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        //testOrg.setDescription("Mice who save people.");
        testOrg.setDescription(null);
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testSuperheroDao.getSuperheroById(1));
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);

        Organization createdOrg 
                = testOrgDao.createOrganization(testOrg);
        
        assertNotNull(createdOrg, "Organization with null unrequired "
                + "field should still be able to be created.");
        
        testOrg.setId(2);
        testOrg.setDescription("Mice who save people.");
        testOrg.setCity(null);
        
        try {
            testOrgDao.createOrganization(testOrg);
            fail("Attempting to create Organization with null "
                    + "values in required fields should cause "
                    + "Exception to be thrown.");
        } catch (DataIntegrityViolationException e) {
            // passed
        }
    }
    
    @Test
    public void testCreateOrganizationEmptyFields() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        //testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testSuperheroDao.getSuperheroById(1));
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);

        testOrg.setDescription(""); // empty unrequired field
        Organization createdOrg 
                = testOrgDao.createOrganization(testOrg);
        
        assertNotNull(createdOrg, "Organization with null unrequired "
                + "field should still be able to be created.");
        
        testOrg.setId(2);
        testOrg.setDescription("Mice who save people.");
        testOrg.setCity("");
        
        createdOrg = testOrgDao.createOrganization(testOrg);
        
        assertNotNull(createdOrg, "Organization with null required "
                + "field should still be able to be created.");
    }
    
    
    /*
     * getOrganizationById:
     * - test getOrganizationById with valid id (see testCreateAndGetOrganization)
     * - test getOrganizationById with invalid id (id does not exist).
     */
    @Test
    public void testGetOrganizationByInvalidId() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testSuperheroDao.getSuperheroById(1));
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);
        
        testOrgDao.createOrganization(testOrg);
        Organization retrievedOrg = testOrgDao.getOrganizationById(1);
        
        // confirm that testOrg was added to the database at id 1
        assertEquals(testOrg, retrievedOrg,
                "Organization retrieved should be equal to testOrg.");
        
        retrievedOrg = testOrgDao.getOrganizationById(2);
        
        assertNull(retrievedOrg, "Null should be returned, no "
                + "Organization exists with id 2.");
    }
    
    
    /*
     * getAllOrganizations:
     * - test getAllOrganizations with 2 Organizations in the database.
     * - test getAllOrganizations with empty database table. (no orgs)
     */
    @Test
    public void testGetAllOrganizations() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testSuperheroDao.getSuperheroById(1));
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);
        
        Organization testOrg2 = new Organization();
        testOrg2.setId(2);
        testOrg2.setName("Avengerrrs");
        testOrg2.setDescription("Tigers who fight bears.");
        testOrg2.setStreet_address("100 Cave Way");
        testOrg2.setCity("Montevideo");
        testOrg2.setCountry("US");
        testOrg2.setMembers(members);
        
        // retrieve list of orgs from empty database table
        List<Organization> testOrgs = testOrgDao.getAllOrganizations();
        
        assertTrue(testOrgs.isEmpty(), "List should be empty, there "
                + "are no Organizations in the database.");
        
        // add orgs to the database and retrieve a list of them
        Organization createdOrg1 
                = testOrgDao.createOrganization(testOrg);
        Organization createdOrg2 
                = testOrgDao.createOrganization(testOrg2);
        
        assertNotNull(createdOrg1, "testOrg should have been created.");
        assertNotNull(createdOrg2, "testOrg2 should have been created.");
        
        testOrgs = testOrgDao.getAllOrganizations();
        
        assertEquals(2, testOrgs.size(), "Method should return a "
                + "list of the 2 Organizations in the database.");
        
        assertTrue(testOrgs.contains(testOrg), 
                "List should contain testOrg.");
        assertTrue(testOrgs.contains(testOrg2),
                "List should contain testOrg2.");
    }


    /*
     * updateOrganization:
     * - test updateOrganization with valid id.
     * - test updateOrganization with invalid id. (org doesn't exist)
     * - test updateOrganization with null fields.
     * - test updateOrganization with empty fields.
     */
    @Test
    public void testUpdateOrganization() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testSuperheroDao.getSuperheroById(1));
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);
        
        testOrgDao.createOrganization(testOrg);
        assertNotNull(testOrgDao.getOrganizationById(1),
                "Organization should have been created.");
        
        // update testOrg
        testOrg.setDescription("Mice who eat people.");
        testOrg.setCity("Chicago");
        members.remove(testSuperheroDao.getSuperheroById(1));
        testOrg.setMembers(members);
        
        boolean isUpdated = testOrgDao.updateOrganization(testOrg);
        Organization retrievedOrg = testOrgDao.getOrganizationById(1);
        
        assertTrue(isUpdated, "Method should return True.");
        assertEquals(testOrg, retrievedOrg,
                "Organization's fields should have been updated.");
    }
    
    @Test
    public void testUpdateOrganizationInvalidId() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testSuperheroDao.getSuperheroById(1));
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);
        
        testOrgDao.createOrganization(testOrg);
        assertNotNull(testOrgDao.getOrganizationById(1),
                "Organization should have been created.");
        
        testOrg.setId(2);
        testOrg.setDescription("Mice who eat people.");
        testOrg.setCity("Chicago");
        
        try {
            testOrgDao.updateOrganization(testOrg);
            fail("Attempt to update Organization using invalid id "
                    + "should throw Exception as there is no "
                    + "Organization with id = 2 (cannot update "
                    + "foreign key child rows with values that do "
                    + "not exist in the parent table).");
        } catch (DataIntegrityViolationException e) {
            // passed 
            Organization retrievedOrg = testOrgDao.getOrganizationById(1);
        
            assertNotEquals(testOrg, retrievedOrg,
                "Organization's fields should not have been updated.");
        }
    }
    
    @Test
    public void testUpdateOrganizationNullFields() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testSuperheroDao.getSuperheroById(1));
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);
        
        // create the testOrg
        testOrgDao.createOrganization(testOrg);
        assertNotNull(testOrgDao.getOrganizationById(1),
                "Organization should have been created.");
        
        // update unrequired field to null
        testOrg.setDescription(null); // unrequired field
        
        boolean isUpdated = testOrgDao.updateOrganization(testOrg);
        Organization retrievedOrg = testOrgDao.getOrganizationById(1);
        
        assertTrue(isUpdated, "Method should return True, "
                + "unrequired fields can be updated to null.");
        assertEquals(testOrg, retrievedOrg,
                "Organization's fields should have been updated.");
        
        // update required field to null
        testOrg.setDescription("Mice who save people.");
        testOrg.setCity(null); // required field
        
        try {
            testOrgDao.updateOrganization(testOrg);
            fail("Attempting to update Organization's required "
                    + "fields with null values should throw "
                    + "Exception.");
        } catch (DataIntegrityViolationException e) {
            // passed
        }
    }
    
    @Test
    public void testUpdateOrganizationEmptyFields() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testSuperheroDao.getSuperheroById(1));
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);
        
        // create the testOrg
        testOrgDao.createOrganization(testOrg);
        assertNotNull(testOrgDao.getOrganizationById(1),
                "Organization should have been created.");
        
        // update unrequired field to null
        testOrg.setDescription(""); // unrequired field
        
        boolean isUpdated = testOrgDao.updateOrganization(testOrg);
        Organization retrievedOrg = testOrgDao.getOrganizationById(1);
        
        assertTrue(isUpdated, "Method should return True, "
                + "unrequired fields can be updated to null.");
        assertEquals(testOrg, retrievedOrg,
                "Organization's fields should have been updated.");
        
        // update required field to null
        testOrg.setDescription("Mice who save people.");
        testOrg.setCity(""); // required field

        isUpdated = testOrgDao.updateOrganization(testOrg);
        retrievedOrg = testOrgDao.getOrganizationById(1);
        
        assertTrue(isUpdated, "Method should return True, "
                + "required fields can be updated to empty values.");
        assertEquals(testOrg, retrievedOrg,
                "Organization's fields should have been updated.");       
    }
    
    /*
     * deleteOrganization:
     * - test deleteOrganization with valid id.
     * - test deleteOrganization with invalid id. (org doesn't exist)
     */
    @Test
    public void testDeleteOrganizationById() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testSuperheroDao.getSuperheroById(1));
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);
        
        // create the testOrg
        testOrgDao.createOrganization(testOrg);
        assertNotNull(testOrgDao.getOrganizationById(1),
                "Organization should have been created.");
        
        boolean isDeleted = testOrgDao.deleteOrganizationById(1);
        
        assertTrue(isDeleted, "Method should return True, testOrg "
                + "should be deleted.");
        
        assertNull(testOrgDao.getOrganizationById(1), 
                "Organization at id 1 should be deleted.");
    }
    
    @Test
    public void testDeleteOrganizationByInvalidId() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testSuperheroDao.getSuperheroById(1));
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);
        
        // create the testOrg
        testOrgDao.createOrganization(testOrg);
        assertNotNull(testOrgDao.getOrganizationById(1),
                "Organization should have been created.");    
        
        // attempt to delete using id that doesn't exist
        boolean isDeleted = testOrgDao.deleteOrganizationById(2);
        
        assertFalse(isDeleted, "Method should return False, no "
                + "Organization exists with id 2.");
        
        assertNotNull(testOrgDao.getOrganizationById(1), 
                "Organization at id 1 should be returned as it "
                        + "should not have been deleted.");
    }
        
    
    /*
     * addOrganizationMember():
     * - test with valid Organization & Superhero:
     *      - test getOrganizationsForSuperhero() 
     *          & testGetOrganizationMembers() to check if add successful.
     * - test with null Superhero & null organization. --> nullpointer
     * - test add duplicate Superhero to Organization.
     */
    
    @Test
    public void testAddOrganizationMemberAndGetOrganizationsForSuperhero() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        // to be added later
        Superhero testHero2 = testSuperheroDao.getSuperheroById(2);
        
        List<Superhero> members = new ArrayList<>();
        members.add(testHero1);
        testOrg.setMembers(members);
        
        
        // create the testOrg
        Organization createdOrg = testOrgDao.createOrganization(testOrg);
        assertNotNull(testOrgDao.getOrganizationById(1),
                "Organization should have been created.");  
        
        // add testHero2 to the test org
        boolean isAdded 
                = testOrgDao.addOrganizationMember(createdOrg, testHero2);
        
        assertTrue(isAdded, "TestHero2 should have successfully been "
                + "added to the Organization and method should "
                + "return true.");
        
        List<Organization> testHeroAffiliations 
                = testOrgDao.getOrganizationsForSuperhero(
                        testHero2.getId());
        
        List<Superhero> testOrgMembers 
                = testOrgDao.getOrganizationMembers(testOrg.getId());
        
        // Get the testOrg -- it should have an updated members list.
        Organization updatedTestOrg = testOrgDao.getOrganizationById(1);
        
        
        assertTrue(testHeroAffiliations.size() == 1, "TestHero2's "
                + "list of Organizations should only contain one.");
        assertTrue(testHeroAffiliations.contains(updatedTestOrg), 
                "TestHero2's list of Organizations should only contain "
                        + "the test organization.");
        
        assertTrue(testOrgMembers.size() == 2, "List of Organization "
                + "members should only contain 2 Superhero members.");
        assertTrue(testOrgMembers.contains(testHero1), "List of "
                + "Organization members should contain testHero1.");
        assertTrue(testOrgMembers.contains(testHero2), "List of "
                + "Organization members should contain testHero2");
        
        assertEquals(updatedTestOrg.getMembers(), testOrgMembers, 
                "Members lists should be equal.");
    }
    
    @Test
    public void testAddOrganizationMemberNullParameter() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        
        List<Superhero> members = new ArrayList<>();
        members.add(testHero1);
        testOrg.setMembers(members);
        
        // create the testOrg
        Organization createdOrg = testOrgDao.createOrganization(testOrg);
        assertNotNull(testOrgDao.getOrganizationById(1),
                "Organization should have been created.");  
        
        // add null to the test org
        boolean isAdded 
                = testOrgDao.addOrganizationMember(createdOrg, null);
        
        assertFalse(isAdded, "Nothing should have been "
                + "added to the Organization and method should "
                + "return false -- NullPointerException ignored.");
        
        List<Superhero> testOrgMembers 
                = testOrgDao.getOrganizationMembers(testOrg.getId());
        
        // Get the testOrg -- it should have an updated members list.
        Organization updatedTestOrg 
                = testOrgDao.getOrganizationById(testOrg.getId());
        
        assertTrue(testOrgMembers.size() == 1, "List of Organization "
                + "members should only contain 2 Superhero members.");
        assertTrue(testOrgMembers.contains(testHero1), "List of "
                + "Organization members should contain testHero1.");
        assertEquals(updatedTestOrg.getMembers(), testOrgMembers, 
                "Members lists should be equal.");
        
        
        // add testHero2 to null
        Superhero testHero2 = testSuperheroDao.getSuperheroById(2);
        isAdded = testOrgDao.addOrganizationMember(null, testHero2);
        
        assertFalse(isAdded, "Nothing should have changed "
                + "and method should return false -- "
                + "NullPointerException ignored.");
        
        testOrgMembers 
                = testOrgDao.getOrganizationMembers(testOrg.getId());
        
        // Get the testOrg -- it should have an updated members list.
        updatedTestOrg = testOrgDao.getOrganizationById(testOrg.getId());
        
        assertTrue(testOrgMembers.size() == 1, "List of Organization "
                + "members should only contain 2 Superhero members.");
        assertFalse(testOrgMembers.contains(testHero2), "List of "
                + "Organization members should not contain testHero2.");
        assertEquals(updatedTestOrg.getMembers(), testOrgMembers, 
                "Members lists should be equal.");
    }
    
    @Test
    public void testAddOrganizationMemberDuplicateMember() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        
        List<Superhero> members = new ArrayList<>();
        members.add(testHero1);
        testOrg.setMembers(members);
        
        
        // create the testOrg
        Organization createdOrg = testOrgDao.createOrganization(testOrg);
        assertNotNull(testOrgDao.getOrganizationById(1),
                "Organization should have been created.");  
        
        // add testHero1 to the test org, again
        boolean isAdded 
                = testOrgDao.addOrganizationMember(createdOrg, testHero1);
        
        assertFalse(isAdded, "Method should return false, Superhero "
                + "already exists in the Organization.");
        
        List<Organization> testHeroAffiliations 
                = testOrgDao.getOrganizationsForSuperhero(
                        testHero1.getId());
        
        List<Superhero> testOrgMembers 
                = testOrgDao.getOrganizationMembers(testOrg.getId());
        
        // Get the testOrg -- it should have an updated members list.
        Organization updatedTestOrg = testOrgDao.getOrganizationById(1);
        
        
        assertTrue(testHeroAffiliations.size() == 1, "TestHero1's "
                + "list of Organizations should only contain one.");
        assertTrue(testHeroAffiliations.contains(updatedTestOrg), 
                "TestHero1's list of Organizations should only contain "
                        + "the test organization.");
        
        assertTrue(testOrgMembers.size() == 1, "List of Organization "
                + "members should only contain 1 Superhero member.");
        assertTrue(testOrgMembers.contains(testHero1), "List of "
                + "Organization members should only contain testHero1.");
        
        assertEquals(updatedTestOrg.getMembers(), testOrgMembers, 
                "Members lists should be equal.");
    }
    
    
    /*
     * getOrganizationMembers():
     * - test with Organization with no members.
     * - test with Organization with members 
     *      (see testAddOrganizationMemberAndGetOrganizationsForSuperhero()).
     */
    
    @Test
    public void testGetOrganizationMembersWithoutMembers() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        testOrg.setMembers(members);
        
        // create the testOrg
        testOrgDao.createOrganization(testOrg);
        assertNotNull(testOrgDao.getOrganizationById(1),
                "Organization should have been created.");  

        // get the empty list of members.
        List<Superhero> testOrgMembers 
                = testOrgDao.getOrganizationMembers(testOrg.getId());
        
        // Get the testOrg -- it should have an updated members list.
        Organization updatedTestOrg = testOrgDao.getOrganizationById(1);
        
        assertTrue(testOrgMembers.isEmpty(), "List of Organization "
                + "members should be empty.");

        assertEquals(updatedTestOrg.getMembers(), testOrgMembers, 
                "Members lists should be equal.");
    }
    
    
    /*
     * getOrganizationsForSuperhero():
     * - test with Superhero with multiple affiliated organizations.
     * - test with Superhero with no affiliated organizations.
     * - test with invalid Superhero id (Superhero does not exist).
     */
    
    @Test
    public void testGetOrganizationsForSuperhero() {
        Superhero testHero = testSuperheroDao.getSuperheroById(1);
        
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        List<Superhero> members = new ArrayList<>();
        members.add(testHero);
        members.add(testSuperheroDao.getSuperheroById(2));
        testOrg.setMembers(members);
        
        Organization testOrg2 = new Organization();
        testOrg2.setId(2);
        testOrg2.setName("Avengerrrs");
        testOrg2.setDescription("Tigers who fight bears.");
        testOrg2.setStreet_address("100 Cave Way");
        testOrg2.setCity("Montevideo");
        testOrg2.setCountry("US");
        testOrg2.setMembers(members);
        
        // create the testOrgs
        testOrgDao.createOrganization(testOrg);
        Organization retrievedTestOrg1 = testOrgDao.getOrganizationById(1);
        assertNotNull(retrievedTestOrg1,
                "Organization should have been created.");  
        
        testOrgDao.createOrganization(testOrg2);
        Organization retrievedTestOrg2 = testOrgDao.getOrganizationById(2);
        assertNotNull(retrievedTestOrg2,
                "Organization should have been created."); 
        
        List<Organization> testHeroAffiliations 
                = testOrgDao.getOrganizationsForSuperhero(
                        testHero.getId());
        
        // Get the testOrg -- it should have an updated members list.
        Organization updatedTestOrg1 
                = testOrgDao.getOrganizationById(testOrg.getId());
        Organization updatedTestOrg2 
                = testOrgDao.getOrganizationById(testOrg2.getId());
        
        assertTrue(testHeroAffiliations.size() == 2, "TestHero's "
                + "list of Organizations should contain 2.");
        assertTrue(testHeroAffiliations.contains(updatedTestOrg1), 
                "TestHero's list of Organizations should contain "
                        + "testOrg1.");
        assertTrue(testHeroAffiliations.contains(updatedTestOrg2),
                "TestHero's list of Organizations should "
                        + "contain testOrg2.");
    }
    
    @Test
    public void testGetOrganizationsForSuperheroZeroOrgs() {
        Superhero testHero = testSuperheroDao.getSuperheroById(1);
        
        List<Organization> testHeroAffiliations 
                = testOrgDao.getOrganizationsForSuperhero(
                        testHero.getId());
        
        assertTrue(testHeroAffiliations.isEmpty(), "TestHero's "
                + "list of Organizations should be empty.");
    }
    
    @Test
    public void testGetOrganizationsForSuperheroNullSuperheroId() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        
        List<Superhero> members = new ArrayList<>();
        members.add(testHero1);
        testOrg.setMembers(members);
        
        // create the testOrg
        testOrgDao.createOrganization(testOrg);
        assertNotNull(testOrgDao.getOrganizationById(1),
                "Organization should have been created.");  
        
        // No Superhero should exist at id 3
        Superhero testHeroNotExist = testSuperheroDao.getSuperheroById(3);
        assertNull(testHeroNotExist, "No Superhero exists with id 3.");
        
        try {
            // attempt to get Organizations for null superheroId
            testOrgDao.getOrganizationsForSuperhero(
                            testHeroNotExist.getId());
            fail("The test hero used was null, therefore it had null id "
                    + "and should have thrown NullPointerException.");
        } catch (NullPointerException e) {
            // passed
        }
    }
    
    @Test
    public void testGetOrganizationsForSuperheroInvalidSuperheroId() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        
        List<Superhero> members = new ArrayList<>();
        members.add(testHero1);
        testOrg.setMembers(members);
        
        // create the testOrg
        testOrgDao.createOrganization(testOrg);
        assertNotNull(testOrgDao.getOrganizationById(1),
                "Organization should have been created.");  
        
        // No Superhero should exist at id 3
        Superhero testHeroNotExist = testSuperheroDao.getSuperheroById(3);
        assertNull(testHeroNotExist, "No Superhero exists with id 3.");
        
        // attempt to get Organizations for superhero that does not exist
        List<Organization> orgsForTestHeroNotExist 
                = testOrgDao.getOrganizationsForSuperhero(3);
        
        assertTrue(orgsForTestHeroNotExist.isEmpty(), "List should be "
                + "empty, no Superhero exists with that Id.");
    }

    
    /*
     * deleteOrganizationMember():
     * - test with valid inputs.
     * - test with null parameters (null organization).
     * - test with superheroId that is not in Organization.
     */
    
    @Test
    public void testDeleteOrganizationMember() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        
        List<Superhero> members = new ArrayList<>();
        members.add(testHero1);
        testOrg.setMembers(members);
        
        // create the testOrg
        testOrgDao.createOrganization(testOrg);
        Organization retrievedTestOrg = testOrgDao.getOrganizationById(1);

        assertNotNull(retrievedTestOrg,
                "Organization should have been created.");  
        
        assertFalse(retrievedTestOrg.getMembers().isEmpty(), 
                "List of members should not be empty.");
        
        boolean isDeleted 
                = testOrgDao.deleteOrganizationMember(
                        testOrg, testHero1.getId());
        List<Organization> testHeroAffiliations
                = testOrgDao.getOrganizationsForSuperhero(
                        testHero1.getId());
        List<Superhero> testOrgMembers 
                = testOrgDao.getOrganizationMembers(testOrg.getId());
        
        assertTrue(isDeleted, "Method should return true if member was "
                + "deleted without exception.");
        assertTrue(testHeroAffiliations.isEmpty(), "TestHero should "
                + "have no remaining affiliations.");
        assertTrue(testOrgMembers.isEmpty(), "TestOrg should have no "
                + "remaining members, TestHero was deleted.");
    }
    
        
    @Test
    public void testDeleteOrganizationMemberNullOrg() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        
        List<Superhero> members = new ArrayList<>();
        members.add(testHero1);
        testOrg.setMembers(members);
        
        // create the testOrg
        testOrgDao.createOrganization(testOrg);
        Organization retrievedTestOrg = testOrgDao.getOrganizationById(1);

        assertNotNull(retrievedTestOrg,
                "Organization should have been created.");  
        
        assertFalse(retrievedTestOrg.getMembers().isEmpty(), 
                "List of members should not be empty.");
        
        boolean isDeleted 
                = testOrgDao.deleteOrganizationMember(
                        null, testHero1.getId());
        
        assertFalse(isDeleted, "Method should return false, null "
                + "Organization parameter.");

    }
    
        
    @Test
    public void testDeleteOrganizationMemberNonMember() {
        Organization testOrg = new Organization();
        testOrg.setId(1);
        testOrg.setName("ResQRs");
        testOrg.setDescription("Mice who save people.");
        testOrg.setStreet_address("504 Orchard Circle");
        testOrg.setCity("Montevideo");
        testOrg.setCountry("US");
        
        Superhero testHero1 = testSuperheroDao.getSuperheroById(1);
        
        List<Superhero> members = new ArrayList<>();
        members.add(testHero1);
        testOrg.setMembers(members);
        
        // create the testOrg
        testOrgDao.createOrganization(testOrg);
        Organization retrievedTestOrg = testOrgDao.getOrganizationById(1);

        assertNotNull(retrievedTestOrg,
                "Organization should have been created.");  
        
        assertFalse(retrievedTestOrg.getMembers().isEmpty(), 
                "List of members should not be empty.");
        
        // testHero2 was never added as a member.
        // attempt to delete testHero2 from the Organization.
        Superhero testHero2 = testSuperheroDao.getSuperheroById(2);
        
        boolean isDeleted 
                = testOrgDao.deleteOrganizationMember(
                        testOrg, testHero2.getId());

        List<Superhero> testOrgMembers 
                = testOrgDao.getOrganizationMembers(testOrg.getId());
        
        assertFalse(isDeleted, "Method should return false, no member "
                + "to delete from Organization.");
        assertTrue(testOrgMembers.size() == 1, "TestOrg should have one "
                + "remaining member.");
        assertEquals(testOrgMembers, testOrg.getMembers(), "List of "
                + "members should remain the same.");
    }
    
}
