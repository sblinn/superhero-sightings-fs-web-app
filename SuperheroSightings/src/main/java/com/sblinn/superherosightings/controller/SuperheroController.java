
package com.sblinn.superherosightings.controller;

import java.util.ArrayList;
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

import com.sblinn.superherosightings.dao.OrganizationDao;
import com.sblinn.superherosightings.dao.SuperheroDao;
import com.sblinn.superherosightings.dao.SuperpowerDao;
import com.sblinn.superherosightings.dto.Organization;
import com.sblinn.superherosightings.dto.Superhero;
import com.sblinn.superherosightings.dto.Superpower;

/**
 *
 * @author Sara Blinn
 */
@Controller
@RequestMapping(value = "/superhero/")
public class SuperheroController {

    @Autowired
    SuperheroDao superheroDao;

    @Autowired
    SuperpowerDao superpowerDao;

    @Autowired
    OrganizationDao organizationDao;
    
    
    @GetMapping("superheros")
    public String displaySuperheros(Model model) {
        List<Superhero> superheros 
                = superheroDao.getAllSuperheros();
        
        model.addAttribute("superheros", superheros);
        
        return "superhero/superheros";
    }

    @GetMapping("details")
    public String viewSuperhero(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        Superhero superhero = superheroDao.getSuperheroById(id);

        model.addAttribute("superhero", superhero);
        model.addAttribute("superpowers", 
                superpowerDao.getSuperpowersForSuperhero(id));
        model.addAttribute("organizations", 
                organizationDao.getOrganizationsForSuperhero(id));
        
        return "superhero/details";
    }
    
    @GetMapping("add")
    public String displayAddSuperhero(
                SuperheroViewModel superheroViewModel, 
                Model model) {
        
        model.addAttribute("superheroViewModel", 
                superheroViewModel);
        model.addAttribute("superhero", 
                superheroViewModel.getSuperhero());
        model.addAttribute("allSuperpowers", 
                superpowerDao.getAllSuperpowers());
        model.addAttribute("allOrganizations", 
                organizationDao.getAllOrganizations());

        return "superhero/add";
    }

    @PostMapping("add")
    public String addSuperhero(HttpServletRequest request, 
                @Valid Superhero superhero,      
                BindingResult result, Model model) {

        String[] superpowerIds 
                = request.getParameterValues("superhero-superpowers");
        ArrayList<Superpower> superpowers = new ArrayList<>();
        if (superpowerIds != null) {
                for (String superpowerId : superpowerIds) {
                superpowers.add(superpowerDao.getSuperpowerById(
                        Integer.parseInt(superpowerId)));
                }
        }

        String[] organizationIds 
                = request.getParameterValues("superhero-organizations");
        ArrayList<Organization> organizations = new ArrayList<>();
        if (organizationIds != null) {
                for (String organizationId : organizationIds) {
                organizations.add(organizationDao.getOrganizationById(
                        Integer.parseInt(organizationId)));
                }
        }

        SuperheroViewModel superheroViewModel 
                = new SuperheroViewModel();
        superheroViewModel.setSuperhero(superhero);
        superheroViewModel.setSuperpowers(superpowers);
        superheroViewModel.setOrganizations(organizations);

        if (result.hasErrors()) {
                model.addAttribute("superheroViewModel", 
                        superheroViewModel);    
                model.addAttribute("superhero", 
                        superheroViewModel.getSuperhero());
                model.addAttribute("allSuperpowers", 
                        superpowerDao.getAllSuperpowers());
                model.addAttribute("allOrganizations", 
                        organizationDao.getAllOrganizations());
                return "superhero/add";
        }
        
        superheroDao.createSuperhero(superhero);
        
        // add selected superpowers to bridge table
        for (Superpower superpower : superpowers) {
            superpowerDao.addSuperpowerForSuperhero(superpower, 
                    superhero);
        }

        // add new superhero's affiliations to bridge table
        for (Organization org : organizations) {
            organizationDao.addOrganizationMember(org, superhero);
        }

        return "redirect:/superhero/superheros";
    }
    
    @GetMapping("edit")
    public String displayEditSuperhero(HttpServletRequest request, 
            Model model) {
        
        int superheroId 
                = Integer.parseInt(request.getParameter("id"));
        Superhero superhero 
                = superheroDao.getSuperheroById(superheroId);
        
        SuperheroViewModel superheroViewModel = new SuperheroViewModel();
        superheroViewModel.setSuperhero(superhero);
        superheroViewModel.setSuperpowers(
                superpowerDao.getSuperpowersForSuperhero(superheroId));
        superheroViewModel.setOrganizations(
                organizationDao.getOrganizationsForSuperhero(superheroId));

        model.addAttribute("superheroViewModel", superheroViewModel);
        model.addAttribute("superhero", superhero);
        model.addAttribute("allSuperpowers", 
                superpowerDao.getAllSuperpowers());
        model.addAttribute("allOrganizations", 
                organizationDao.getAllOrganizations());
        
        return "superhero/edit";
    }

    @PostMapping("edit")
    public String editSuperhero(HttpServletRequest request, 
            @Valid Superhero superhero,     
            BindingResult result, Model model) {

        String[] superpowerIds 
                = request.getParameterValues("superhero-superpowers");
        ArrayList<Superpower> selectedSuperpowers = new ArrayList<>();
        
        String[] organizationIds 
                = request.getParameterValues("superhero-organizations");
        ArrayList<Organization> selectedOrgs = new ArrayList<>();

        try {
            for (String superpowerId : superpowerIds) {
                selectedSuperpowers.add(superpowerDao.getSuperpowerById(
                        Integer.parseInt(superpowerId)));
            }

            for (String organizationId : organizationIds) {
                selectedOrgs.add(organizationDao.getOrganizationById(
                        Integer.parseInt(organizationId)));
            }
        } catch (NullPointerException e) {
            
        }

        SuperheroViewModel superheroViewModel 
                = new SuperheroViewModel();
        superheroViewModel.setSuperhero(superhero);
        superheroViewModel.setSuperpowers(selectedSuperpowers);
        superheroViewModel.setOrganizations(selectedOrgs);

        if (result.hasErrors()) {
                model.addAttribute("superheroViewModel", 
                        superheroViewModel);
                model.addAttribute("superhero", superhero);
                model.addAttribute("allSuperpowers", 
                        superpowerDao.getAllSuperpowers());
                model.addAttribute("allOrganizations", 
                        organizationDao.getAllOrganizations());
                

                return "superhero/edit";
        }

        superheroDao.updateSuperhero(superhero);
        
        // UPDATE SUPERPOWERS
        List<Superpower> formerSuperpowers 
                = superpowerDao.getSuperpowersForSuperhero(
                        superhero.getId());
        
        //delete any former superpowers that were deselected
        for (Superpower formerSuperpower : formerSuperpowers) {
            if (!selectedSuperpowers.contains(formerSuperpower)) {
                superpowerDao.deleteSuperpowerForSuperhero(
                        formerSuperpower, superhero.getId());
            }
        }
        // add selected superpowers
        for (Superpower selectedSuperpower : selectedSuperpowers) {
            if (!formerSuperpowers.contains(selectedSuperpower)) {
                superpowerDao.addSuperpowerForSuperhero(
                        selectedSuperpower, superhero);
            }
        }
        
        // UPDATE ORGANIZATIONS
        List<Organization> formerOrganizations 
                = organizationDao.getOrganizationsForSuperhero(
                        superhero.getId());
        
        // delete any former affiliations that were deselected
        for (Organization formerOrg : formerOrganizations) {
            if (!selectedOrgs.contains(formerOrg)) {
                organizationDao.deleteOrganizationMember(formerOrg, 
                        superhero.getId());
            }
        }
        // add new affiliations
        for (Organization selectedOrg : selectedOrgs) {
            if (!formerOrganizations.contains(selectedOrg)) {
            organizationDao.addOrganizationMember(selectedOrg, 
                    superhero);
            }
        }
        
        return "redirect:/superhero/superheros";
    }

    @GetMapping("delete")
    public String displayDeleteSuperhero(int id, Model model) {
        Superhero superhero = superheroDao.getSuperheroById(id);
        model.addAttribute("superhero", superhero);

        return "superhero/delete";
    }

    @GetMapping("deleteSuperhero")
    public String deleteSuperhero(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        superheroDao.deleteSuperheroById(id);

        return "redirect:/superhero/superheros";
    }

}
