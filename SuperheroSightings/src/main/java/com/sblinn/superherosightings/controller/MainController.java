
package com.sblinn.superherosightings.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.maps.model.LatLng;
import com.sblinn.superherosightings.dao.LocationDao;
import com.sblinn.superherosightings.dao.SightingDao;
import com.sblinn.superherosightings.dao.SuperheroDao;
import com.sblinn.superherosightings.dto.Location;
import com.sblinn.superherosightings.dto.Sighting;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author Sara Blinn
 */
@Controller
@ControllerAdvice
@RequestMapping(value = "/")
public class MainController {
    
    @Autowired
    SightingDao sightingDao;

    @Autowired
    SuperheroDao superheroDao;

    @Autowired
    LocationDao locationDao;
    
    @Value("${maps.api.key}")
    private String mapsApiKey;
    
    
    @ModelAttribute("recentVMSightings")
    public void getRecentSightings(Model model) {
        List<Sighting> dtoSightings 
                = sightingDao.getAllSightings();

        List<Sighting> recentSightings = new ArrayList<>();

        List<SightingViewModel> vmSightings
                = new ArrayList<>();

        // sort from most recent -> oldest
        dtoSightings.sort((s1, s2) -> 
                s1.getDate().compareTo(s2.getDate()));
        Collections.reverse(dtoSightings);
        
        for (int index = 0; index < 5; index++) {
            recentSightings.add(dtoSightings.get(index));
        }
                
        for (Sighting sighting : recentSightings) {
            SightingViewModel s = new SightingViewModel();
            s.setSighting(sighting);
            s.setSuperhero(superheroDao.getSuperheroById(
                    sighting.getSuperhero_id()));
            s.setLocation(locationDao.getLocationById(
                    sighting.getLocation_id()));
            
            vmSightings.add(s);
        }

        model.addAttribute("recentVMSightings", vmSightings);
    }


    @GetMapping
    public String loadHomepage(Model model) {
        
        List<Sighting> dtoSightings 
                = sightingDao.getAllSightings();

        List<Sighting> recentSightings = new ArrayList<>();

        List<SightingViewModel> recentVMSightings
                = new ArrayList<>();

        // sort from oldest -> most recent
        dtoSightings.sort((s1, s2) -> 
                s1.getDate().compareTo(s2.getDate()));

        int max = dtoSightings.size()-1;
        for (int index = max; index > max-5; index--) {
            recentSightings.add(dtoSightings.get(index));
        }
                
        for (Sighting sighting : recentSightings) {
            SightingViewModel s = new SightingViewModel();
            s.setSighting(sighting);
            s.setSuperhero(superheroDao.getSuperheroById(
                    sighting.getSuperhero_id()));
            s.setLocation(locationDao.getLocationById(
                    sighting.getLocation_id()));
            
            recentVMSightings.add(s);
        }

        //display the most recent sighting location on the map
        Location recentSightingLocation 
            = recentVMSightings.get(0).getLocation();
        

        double latitude = recentSightingLocation.getLatitude().doubleValue();
        double longitude = recentSightingLocation.getLongitude().doubleValue();

        String latStr = String.format("%.8f", latitude);
        String lngStr = String.format("%.8f", longitude);

        model.addAttribute("selectedLocationLat", latStr);
        model.addAttribute("selectedLocationLng", lngStr);

        String URL = "https://maps.googleapis.com/maps/api/js?key=" 
                + mapsApiKey + "&callback=" + "initMap" + "&v=weekly";
        
        model.addAttribute("googleMapSrcURL", URL);

        List<LatLng> latLngs = new ArrayList<>();
        for (SightingViewModel sightingViewModel : recentVMSightings) {
            Location location = sightingViewModel.getLocation();
            double lat = location.getLatitude().doubleValue();
            double lng = location.getLongitude().doubleValue();
            latLngs.add(new LatLng(lat, lng));
        }

        model.addAttribute("recentSightingsLatLngs", latLngs);
        
        return "home";
    }

    @GetMapping("home") 
    public String loadLocation(HttpServletRequest request, Model model) {
        
            int locationId = Integer.parseInt(request.getParameter("id"));
            Location location = locationDao.getLocationById(locationId);

            double latitude = location.getLatitude().doubleValue();
            double longitude = location.getLongitude().doubleValue();
            
            String latStr = String.format("%.8f", latitude);
            String lngStr = String.format("%.8f", longitude);

            model.addAttribute("selectedLocationLat", latStr);
            model.addAttribute("selectedLocationLng", lngStr);
            
            String URL = "https://maps.googleapis.com/maps/api/js?key=" 
                    + mapsApiKey + "&callback=" + "initMap" + "&v=weekly";
    
            model.addAttribute("googleMapSrcURL", URL);

            return "home";
    }


}
