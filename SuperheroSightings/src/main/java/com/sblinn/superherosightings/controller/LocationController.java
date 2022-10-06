
package com.sblinn.superherosightings.controller;

import com.sblinn.superherosightings.dao.LocationDao;
import com.sblinn.superherosightings.dto.Location;
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

/**
 *
 * @author Sara Blinn
 */
@Controller
@RequestMapping(value = "/location/")
public class LocationController {

    @Autowired
    LocationDao locationDao;
    
    
    @GetMapping("locations")
    public String displayLocations(Model model) {
        List<Location> locations 
                = locationDao.getAllLocations();
        
        model.addAttribute("locations", locations);
        return "location/locations";
    }

    @GetMapping("details")
    public String viewLocation(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        Location location = locationDao.getLocationById(id);

        model.addAttribute("location", location);
        return "location/details";
    }
    
    @GetMapping("add")
    public String displayAddLocation(Model model) {
        model.addAttribute("location", new Location());
        return "location/add";
    }

    @PostMapping("addLocation")
    public String addLocation(@Valid Location location,
            BindingResult result) {
        
        if (result.hasErrors()) {
            return "location/add";
        }
        locationDao.createLocation(location);
        
        return "redirect:/location/locations";
    }
    
    @GetMapping("edit")
    public String displayEditLocation(HttpServletRequest request, 
            Model model) {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Location location 
                = locationDao.getLocationById(id);

        model.addAttribute("location", location);
        
        return "location/edit";
    }

    @PostMapping("editLocation")
    public String editLocation(HttpServletRequest request, 
            @Valid Location location, BindingResult result) {
        
        if (result.hasErrors()) {
            return "location/edit";
        }
        locationDao.updateLocation(location);
        
        return "redirect:/location/locations";
    }

    @GetMapping("delete")
    public String displayDeleteLocation(int id, Model model) {
        Location location = locationDao.getLocationById(id);
        model.addAttribute("location", location);

        return "location/delete";
    }

    @GetMapping("deleteLocation")
    public String deleteLocation(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        locationDao.deleteLocationById(id);

        return "redirect:/location/locations";
    }
    
}
