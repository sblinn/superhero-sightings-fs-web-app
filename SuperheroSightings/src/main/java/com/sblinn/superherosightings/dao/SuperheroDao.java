package com.sblinn.superherosightings.dao;

import com.sblinn.superherosightings.dto.Superhero;
import java.util.List;

/**
 *
 * @author Sara Blinn
 */
public interface SuperheroDao {
    
    Superhero getSuperheroById(int id);
    
    List<Superhero> getAllSuperheros();
    
    Superhero createSuperhero(Superhero superhero);
    
    boolean updateSuperhero(Superhero updatedHero);
    
    boolean deleteSuperheroById(int id);
    
}
