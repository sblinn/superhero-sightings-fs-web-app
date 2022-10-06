
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

import com.sblinn.superherosightings.dao.SuperheroDao;
import com.sblinn.superherosightings.dao.SuperpowerDao;
import com.sblinn.superherosightings.dto.Superhero;
import com.sblinn.superherosightings.dto.Superpower;
import java.util.ArrayList;

/**
 *
 * @author Sara Blinn
 */
@Controller
@RequestMapping(value = "/superpower/")
public class SuperpowerController {

    @Autowired
    SuperpowerDao superpowerDao;

    @Autowired
    SuperheroDao superheroDao;
    
    
    @GetMapping("superpowers")
    public String displaySuperpowers(Model model) {
        List<Superpower> superpowers 
                = superpowerDao.getAllSuperpowers();
        
        model.addAttribute("superpowers", superpowers);
        return "superpower/superpowers";
    }

    @GetMapping("details")
    public String viewSuperpower(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        Superpower superpower = superpowerDao.getSuperpowerById(id);

        model.addAttribute("superpower", superpower);
        return "superpower/details";
    }
    
    @GetMapping("add")
    public String displayAddSuperpower(Model model) {
        model.addAttribute("superpower", new Superpower());
        return "superpower/add";
    }

    @PostMapping("addSuperpower")
    public String addSuperpower(@Valid Superpower superpower, BindingResult result) {
        if (result.hasErrors()) {
            return "superpower/add";
        }
        superpowerDao.createSuperpower(superpower);
        
        return "redirect:/superpower/superpowers";
    }
    
    @GetMapping("edit")
    public String displayEditSuperpower(HttpServletRequest request, Model model) {
        int id = Integer.parseInt(request.getParameter("id"));
        Superpower superpower = superpowerDao.getSuperpowerById(id);

        model.addAttribute("superpower", superpower);
        model.addAttribute("selectedSuperheros", superpower.getSuperheros());
        model.addAttribute("allSuperheros", 
                superheroDao.getAllSuperheros());

        return "superpower/edit";
    }

    @PostMapping("edit")
    public String editSuperpower(HttpServletRequest request, 
        @Valid Superpower superpower, 
        BindingResult result, Model model) {

        String[] selectedSuperheroIds 
                = request.getParameterValues("superpower-superheros");
        List<Superhero> selectedSuperheros = new ArrayList<>();
        
        for (String superheroId : selectedSuperheroIds) {
            selectedSuperheros.add(
                    superheroDao.getSuperheroById(
                            Integer.parseInt(superheroId)));
        }

        if (result.hasErrors()) {
            model.addAttribute("superpower", superpower);
            model.addAttribute("selectedSuperheros",
                selectedSuperheros);
            model.addAttribute("allSuperheros", 
                superheroDao.getAllSuperheros());
            return "superpower/edit";
        }
        
        superpower.setSuperheros(selectedSuperheros);
        superpowerDao.updateSuperpower(superpower);
        
        return "redirect:/superpower/superpowers";
    }

    @GetMapping("delete")
    public String displayDeleteSuperpower(int id, Model model) {
        Superpower superpower = superpowerDao.getSuperpowerById(id);
        model.addAttribute("superpower", superpower);

        return "superpower/delete";
    }

    @GetMapping("deleteSuperpower")
    public String deleteSuperpower(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        superpowerDao.deleteSuperpowerById(id);

        return "redirect:/superpower/superpowers";
    }
    
}
