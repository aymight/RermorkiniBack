package com.onegateafrica.Controllers;

import com.onegateafrica.Entities.Remorqueur;
import com.onegateafrica.Service.RemorqueurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping ("/api/visiteur")
public class VisiteurController {
  private RemorqueurService remorqeurService ;

  @Autowired
  public VisiteurController(RemorqueurService remorqeurService) {
    this.remorqeurService = remorqeurService ;
  }

  @GetMapping("/remorqeurs")
  public List<Remorqueur> findAllRemorqeurs(){
    return remorqeurService.findAll();
  }

}