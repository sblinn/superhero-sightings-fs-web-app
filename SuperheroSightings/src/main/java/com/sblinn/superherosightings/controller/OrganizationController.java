
package com.sblinn.superherosightings.controller;

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
import com.sblinn.superherosightings.dto.Organization;
import com.sblinn.superherosightings.dto.Superhero;
import java.util.ArrayList;

/**
 *
 * @author Sara Blinn
 */
@Controller
@RequestMapping(value = "/organization/")
public class OrganizationController {

    @Autowired
    OrganizationDao organizationDao;

    @Autowired
    SuperheroDao superheroDao;
    
    
    @GetMapping("organizations")
    public String displayOrganizations(Model model) {
        List<Organization> organizations 
                = organizationDao.getAllOrganizations();
        
        model.addAttribute("organizations", organizations);
        return "organization/organizations";
    }

    @GetMapping("details")
    public String viewOrganization(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        Organization organization 
                = organizationDao.getOrganizationById(id);
        
        model.addAttribute("organization", organization);
        
        return "organization/details";
    }
    
    @GetMapping("add")
    public String displayAddOrganization(Model model) {
        model.addAttribute("organization", new Organization());
        return "organization/add";
    }

    @PostMapping("addOrganization")
    public String addOrganization(@Valid Organization organization,
            BindingResult result) {
        
        if (result.hasErrors()) {
            return "organization/add";
        }
        organizationDao.createOrganization(organization);
        
        return "redirect:/organization/organizations";
    }
    
    @GetMapping("edit")
    public String displayEditOrganization(HttpServletRequest request, 
            Model model) {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Organization organization 
                = organizationDao.getOrganizationById(id);
        
        model.addAttribute("organization", organization);
        model.addAttribute("superheros", superheroDao.getAllSuperheros());
        
        return "organization/edit";
    }

    @PostMapping("editOrganization")
    public String editOrganization(HttpServletRequest request, 
            @Valid Organization organization, BindingResult result) {
                
        if (result.hasErrors()) {
            return "organization/edit";
        }
        
        String[] selectedSuperheroIds 
                = request.getParameterValues("organization-members");
        List<Superhero> selectedSuperheros = new ArrayList<>();
        
        for (String superheroId : selectedSuperheroIds) {
            selectedSuperheros.add(superheroDao.getSuperheroById(
                    Integer.parseInt(superheroId)));
        }
        
        organization.setMembers(selectedSuperheros);
        organizationDao.updateOrganization(organization);
        
        return "redirect:/organization/organizations";
    }

    @GetMapping("delete")
    public String displayDeleteOrganization(int id, Model model) {
        Organization organization 
                = organizationDao.getOrganizationById(id);
        model.addAttribute("organization", organization);

        return "organization/delete";
    }

    @GetMapping("deleteOrganization")
    public String deleteOrganization(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        organizationDao.deleteOrganizationById(id);

        return "redirect:/organization/organizations";
    }
    
}
