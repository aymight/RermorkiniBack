package com.onegateafrica.Controllers;

import com.onegateafrica.Entities.Remorqueur;
import com.onegateafrica.Service.RemorqueurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/public")
public class PublicController {
    private final RemorqueurService remorqueurService;

    @Autowired
    public PublicController(RemorqueurService remorqueurService) {
        this.remorqueurService = remorqueurService;
    }

    @PutMapping("/preciserCompteRemorqueurUitlise/{idRemorqueur}/{compteUtilise}")
    private ResponseEntity<Object> mettreAjourCompteUtilise(@PathVariable Long idRemorqueur , @PathVariable String compteUtilise){
        if(idRemorqueur !=null && compteUtilise !=null ){
            try {
                Remorqueur remorqueur = remorqueurService.getRemorqueur(idRemorqueur).get();
                if(compteUtilise.equalsIgnoreCase("assurance")){
                    remorqueur.setCompteAssurance(true);
                    remorqueurService.saveOrUpdateRemorqueur(remorqueur);
                    return ResponseEntity.status(HttpStatus.OK).body(remorqueur);
                }

                if(compteUtilise.equalsIgnoreCase("libre")){
                    remorqueur.setCompteAssurance(false);
                    remorqueurService.saveOrUpdateRemorqueur(remorqueur);
                    return ResponseEntity.status(HttpStatus.OK).body(remorqueur);
                }
            }
            catch (Exception e){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur ");
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur ");
    }


}
