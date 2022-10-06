package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dto.Superhero;
import com.sblinn.superherosightings.dto.Superpower;
import java.util.List;

/**
 *
 * @author Sara Blinn
 */
public interface SuperpowerDao {
    
    Superpower getSuperpowerById(int id);
    
    List<Superpower> getAllSuperpowers();
    
    Superpower createSuperpower(Superpower superpower);
    
    boolean updateSuperpower(Superpower updatedSuperpower);
    
    boolean deleteSuperpowerById(int id);
    
    
    List<Superhero> getAllSuperherosWithSuperpower(int superpowerId);
    
    List<Superpower> getSuperpowersForSuperhero(int superheroId);
    
    boolean addSuperpowerForSuperhero(Superpower superpower,
            Superhero superhero);
    
    boolean deleteSuperpowerForSuperhero(Superpower superpower,
            int superheroId);
    
}
