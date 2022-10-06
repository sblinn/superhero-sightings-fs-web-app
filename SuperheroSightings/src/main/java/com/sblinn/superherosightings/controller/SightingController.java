
package com.sblinn.superherosightings.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sblinn.superherosightings.dao.LocationDao;
import com.sblinn.superherosightings.dao.SightingDao;
import com.sblinn.superherosightings.dao.SuperheroDao;
import com.sblinn.superherosightings.dto.Location;
import com.sblinn.superherosightings.dto.Sighting;
import com.sblinn.superherosightings.dto.Superhero;

/**
 *
 * @author Sara Blinn
 */
@Controller
@RequestMapping(value = "/sighting/")
public class SightingController {

    @Autowired
    SightingDao sightingDao;

    @Autowired
    SuperheroDao superheroDao;

    @Autowired
    LocationDao locationDao;
    

    @GetMapping("sightings")
    public String displaySightings(Model model) {
        List<Sighting> dtoSightings 
                = sightingDao.getAllSightings();
        
        List<SightingViewModel> vmSightings
                = new ArrayList<>();

        for (Sighting sighting : dtoSightings) {
            SightingViewModel s = new SightingViewModel();
            s.setSighting(sighting);
            s.setSuperhero(superheroDao.getSuperheroById(
                    sighting.getSuperhero_id()));
            s.setLocation(locationDao.getLocationById(
                    sighting.getLocation_id()));
            
            vmSightings.add(s);
        }

        model.addAttribute("vmSightings", vmSightings);
        model.addAttribute("order");

        return "sighting/sightings";
    }
    
    @GetMapping("sightings/sort")
    public String displaySightingsInOrder(HttpServletRequest request, 
            Model model) {
        
        List<Sighting> dtoSightings 
                = sightingDao.getAllSightings();

        List<SightingViewModel> vmSightings
                = new ArrayList<>();
        for (Sighting sighting : dtoSightings) {
            SightingViewModel s = new SightingViewModel();
            s.setSighting(sighting);
            s.setSuperhero(superheroDao.getSuperheroById(
                    sighting.getSuperhero_id()));
            s.setLocation(locationDao.getLocationById(
                    sighting.getLocation_id()));
            
            vmSightings.add(s);
        }
        
        String orderRequest = request.getParameter("order");

        // date-asc = most recent -> oldest
        // date-desc = oldest -> most recent
        if (orderRequest.equals("date-asc") || 
                orderRequest.equals("date-desc") || 
                orderRequest.equals(null)) {

            vmSightings.sort((s1, s2) -> 
                    s1.getSighting().getDate().compareTo(
                            s2.getSighting().getDate()));
            
            if (orderRequest.equals("date-asc") || 
                    orderRequest.equals(null)) {
                Collections.reverse(vmSightings);
            }
        };

        // superhero-desc = Z-A
        if (orderRequest.equals("superhero") ||
            orderRequest.equals("superhero-desc")) {

            vmSightings.sort((s1, s2) -> 
                    (s1.getSuperhero().getName())
                            .compareToIgnoreCase(
                                    s2.getSuperhero().getName()));

            if (orderRequest.equals("superhero-desc")) {
                Collections.reverse(vmSightings);
            }
        };

        if (orderRequest.equals("location") ||
            orderRequest.equals("location-desc")) {

            vmSightings.sort((s1, s2) -> 
                    (s1.getLocation().getName())
                            .compareToIgnoreCase(
                                s2.getLocation().getName()));
                        
            if (orderRequest.equals("location-desc")) {
                Collections.reverse(vmSightings);
            }
        };

        model.addAttribute("vmSightings", vmSightings);
        model.addAttribute("order");

        return "sighting/sightings";
    }

    @GetMapping("details")
    public String viewSightingDetails(HttpServletRequest request, Model model) {
        SightingViewModel sightingViewModel = new SightingViewModel();

        int id = Integer.parseInt(request.getParameter("id"));
        Sighting sighting = sightingDao.getSightingById(id);
        
        Superhero superhero = superheroDao.getSuperheroById(sighting.getSuperhero_id());
        Location location = locationDao.getLocationById(sighting.getLocation_id());

        sightingViewModel.setSighting(sighting);
        sightingViewModel.setSuperhero(superhero);
        sightingViewModel.setLocation(location);

        model.addAttribute("sightingViewModel", sightingViewModel);

        return "sighting/details";
    }
    
    @GetMapping("add")
    public String displayAddSighting(Model model) {
        model.addAttribute("sighting", new Sighting());
        model.addAttribute("superheros", superheroDao.getAllSuperheros());
        model.addAttribute("locations", locationDao.getAllLocations());

        return "sighting/add";
    }

    @PostMapping("reportSighting")
    public String addSighting(HttpServletRequest request, 
            @Valid Sighting sighting,      
            BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "sighting/add";
        }

        LocalDateTime dateInput = LocalDateTime.parse(request.getParameter("sighting-date")); 

        sighting.setDate(dateInput);
        sighting.setSuperhero_id(Integer.parseInt(request.getParameter("sighting-superhero")));
        sighting.setLocation_id(Integer.parseInt(request.getParameter("sighting-location")));

        sightingDao.createSighting(sighting);
        
        return "redirect:/sighting/sightings";
    }
    
    @GetMapping("edit")
    public String displayEditSighting(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        Sighting sighting = sightingDao.getSightingById(id);

        model.addAttribute("sighting", sighting);
        model.addAttribute("superheros", superheroDao.getAllSuperheros());
        model.addAttribute("locations", locationDao.getAllLocations());
        
        return "sighting/edit";
    }

    @PostMapping("editSighting")
    public String editSighting(HttpServletRequest request, @Valid Sighting sighting,     
            BindingResult result) {

        if (result.hasErrors()) {
            return "sighting/edit";
        }

        sighting.setSuperhero_id(Integer.parseInt(request.getParameter("sighting-superhero")));
        sighting.setLocation_id(Integer.parseInt(request.getParameter("sighting-location")));
        sighting.setDate(LocalDateTime.parse(request.getParameter("sighting-date")));

        sightingDao.updateSighting(sighting);
        
        return "redirect:/sighting/sightings";
    }

    @GetMapping("delete")
    public String displayDeleteSighting(int id, Model model) {
        Sighting sighting = sightingDao.getSightingById(id);
        Superhero superhero = superheroDao.getSuperheroById(sighting.getSuperhero_id());
        Location location = locationDao.getLocationById(sighting.getLocation_id());

        SightingViewModel sightingViewModel = new SightingViewModel();
        sightingViewModel.setSighting(sighting);
        sightingViewModel.setSuperhero(superhero);
        sightingViewModel.setLocation(location);

        model.addAttribute("sightingViewModel", sightingViewModel);

        return "sighting/delete";
    }

    @GetMapping("deleteSighting")
    public String deleteSighting(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        sightingDao.deleteSightingById(id);

        return "redirect:/sighting/sightings";
    }

}
