package com.onegateafrica.Controllers;

import com.onegateafrica.Controllers.utils.IntervalWeekUtils;
import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.Reclamation;
import com.onegateafrica.Entities.Remorqueur;
import com.onegateafrica.Payloads.request.ReclamationClientDto;
import com.onegateafrica.Payloads.request.ReclamationDto;
import com.onegateafrica.Service.BannissementService;
import com.onegateafrica.Service.ConsommateurService;
import com.onegateafrica.Service.ReclamationService;
import com.onegateafrica.Service.RemorqueurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.Instant;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/reclamation")
public class ReclamationController {

    private final ReclamationService reclamationService ;
    private final RemorqueurService remorqueurService ;
    private final BannissementService bannissementService ;
    private final ConsommateurService consommateurService ;

    @Autowired
    public ReclamationController(ReclamationService reclamationService, RemorqueurService remorqueurService, BannissementService bannissementService, ConsommateurService consommateurService) {
        this.reclamationService = reclamationService;
        this.remorqueurService = remorqueurService;
        this.bannissementService = bannissementService;
        this.consommateurService = consommateurService;
    }

    @PostMapping("/ajouterReclamation")
    public ResponseEntity<Object> addReclamation(@RequestBody ReclamationDto reclamationDto){
        if(reclamationDto !=null && reclamationDto.getIdRemorqeur() !=null && reclamationDto.getDescription() !=null){
            Remorqueur remorqueur ;

            try {
                //1)-------- ajouter et affecter la reclamation au remorqeur en question
                remorqueur = remorqueurService.getRemorqueur(reclamationDto.getIdRemorqeur()).get();
                Reclamation reclamation = new Reclamation() ;
                if(!reclamationDto.getDescription().isEmpty()) reclamation.setDescription(reclamationDto.getDescription());
                reclamation.setRemorqueur(remorqueur);

                Instant today = Instant.now();

                reclamation.setDateAjout( Timestamp.from(today));

                remorqueur.getListeReclamations().add(reclamation);

                remorqueurService.saveOrUpdateRemorqueur(remorqueur);

                //2) ------calculate the week of today's date
                IntervalWeekUtils currentIntervalWeek = reclamationService.calculateWeekFromToday(today);
                //IntervalWeekUtils intervalWeekUtils  = reclamationService.calculateWeekFromToday(today.plus(Duration.ofDays(6)));
                System.out.println("this is from controller "+currentIntervalWeek.getLeftDateIntervall()+" " +currentIntervalWeek.getRightDateIntervall());

                //3)---------- get the list of reclamations of a given remorqeur in this week
                List<Reclamation> allReclamationsList = reclamationService.getReclamationsOfRemorqeur(reclamationDto.getIdRemorqeur()).get();

               List<Reclamation> searchedListOfReclmations=  reclamationService.getReclamationsOfWeek(allReclamationsList,currentIntervalWeek.getLeftDateIntervall(),currentIntervalWeek.getRightDateIntervall());
                System.out.println("this is the liste of reclamations in this week "+searchedListOfReclmations.size());
               //4)----traiter la possibilité d'un bann
                // ---------- check if the number of recla in the week >= 5 && le premier bann then bann = 3 jours
                ///-----------check if the number of recla in the week >=10 && le second bann then bann =  10 jours
                ///-----------check if the number of recla in the week >=10 && le troisiéme bann then bann =  10 jours

               String message = reclamationService.traiterBann(reclamationDto.getIdRemorqeur(),searchedListOfReclmations);

                return ResponseEntity.status(HttpStatus.OK).body(message);

            }
            catch (Exception e){
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("erreur ");
            }


        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("vérifiez les données passés");
    }


    @PostMapping("/ajouterReclamationAuClient")
    public ResponseEntity<Object> ajouterReclamationAuClient (@RequestBody ReclamationClientDto reclamationClientDto) throws ParseException {
        if(reclamationClientDto !=null && reclamationClientDto.getIdConsommateur() !=null && reclamationClientDto.getDescription() !=null){
            Consommateur consommateur ;

            //try {
                //1)-------- ajouter et affecter la reclamation au consommateur en question
                consommateur = consommateurService.getConsommateur(reclamationClientDto.getIdConsommateur()).get();
                Reclamation reclamation = new Reclamation() ;
                if(!reclamationClientDto.getDescription().isEmpty()) reclamation.setDescription(reclamationClientDto.getDescription());
                reclamation.setConsommateur(consommateur);

                Instant today = Instant.now();

                reclamation.setDateAjout( Timestamp.from(today));

                consommateur.getListeReclamations().add(reclamation);

                consommateurService.saveOrUpdateConsommateur(consommateur);

                //2) ------calculate the week of today's date
                IntervalWeekUtils currentIntervalWeek = reclamationService.calculateWeekFromToday(today);
                //IntervalWeekUtils intervalWeekUtils  = reclamationService.calculateWeekFromToday(today.plus(Duration.ofDays(6)));
                System.out.println("this is from controller "+currentIntervalWeek.getLeftDateIntervall()+" " +currentIntervalWeek.getRightDateIntervall());

                //3)---------- get the list of reclamations of a given consommateur in this week
                List<Reclamation> allReclamationsList = reclamationService.getReclamationsOfClient(reclamationClientDto.getIdConsommateur()).get();
                List<Reclamation> searchedListOfReclmations=  reclamationService.getReclamationsClientOfWeek(allReclamationsList,currentIntervalWeek.getLeftDateIntervall(),currentIntervalWeek.getRightDateIntervall());
                System.out.println("this is the liste of reclamations in this week "+searchedListOfReclmations.size());

                //4)----traiter la possibilité d'un bann
                // ---------- check if the number of recla in the week >= 5 && le premier bann then bann = 3 jours
                ///-----------check if the number of recla in the week >=10 && le second bann then bann =  10 jours
                ///-----------check if the number of recla in the week >=10 && le troisiéme bann then bann =  10 jours

                String message = reclamationService.traiterBannOfClient(reclamationClientDto.getIdConsommateur(),searchedListOfReclmations);


                return ResponseEntity.status(HttpStatus.OK).body(message);


//            }
//            catch (Exception e){
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getStackTrace());
//            }


        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("vérifiez les données passés");
    }


}
