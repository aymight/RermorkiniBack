package com.onegateafrica.Controllers;

import com.onegateafrica.Entities.*;
import com.onegateafrica.Payloads.request.DemandeRemorquageAccepteDto;
import com.onegateafrica.Payloads.request.DemandeRemorquageDto;
import com.onegateafrica.Payloads.response.VerificationChangementRemorqeurResponse;
import com.onegateafrica.Repositories.DemandeRemorquageRepository;
import com.onegateafrica.Service.ConsommateurService;
import com.onegateafrica.Service.DemandeRemorquageService;
import com.onegateafrica.Service.RemorqueurService;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.spec.ECField;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/demandeRemorquage")

public class DemandeRemorquageController {

  private final RemorqueurService remorqueurService ;
  private final DemandeRemorquageRepository demandeRemorquageRepository ;
  private final DemandeRemorquageService demandeRemorquageService ;
  private final ConsommateurService consommateurService ;

  @Autowired
  public DemandeRemorquageController(RemorqueurService remorqueurService, DemandeRemorquageRepository demandeRemorquageRepository, DemandeRemorquageService demandeRemorquageService, ConsommateurService consommateurService) {
    this.remorqueurService = remorqueurService;
    this.demandeRemorquageRepository = demandeRemorquageRepository;
    this.demandeRemorquageService = demandeRemorquageService;
    this.consommateurService = consommateurService;
  }

  @GetMapping("/getLastCommandeAssurance/{idRemorqueur}")
  public ResponseEntity<Object> getCommandeRemorquageAssurance(@PathVariable Long idRemorqueur){
      if(idRemorqueur !=null) {
          try {
              List<DemandeRemorquage> listDemande = demandeRemorquageRepository.findAll();
              DemandeRemorquage commandeAenvoyer = new DemandeRemorquage();
              for(DemandeRemorquage d : listDemande){
                  if(d.getRemorqueur() != null) {
                      if( d.getRemorqueur().getId() == idRemorqueur && (d.getIsFinished() ==null || !d.getIsFinished()) ) {
                          commandeAenvoyer = d ;
                          break;
                      }
                  }

              }
              return ResponseEntity.status(HttpStatus.OK).body(commandeAenvoyer);
          }
          catch (Exception e) {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur");
          }
      }

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur");
  }
  @PostMapping("/addDemande")
  //@PreAuthorize("hasRole('CONSOMMATEUR')")
  public ResponseEntity< Object > addDemandeRemorquage(@RequestBody DemandeRemorquageDto demandeRemorquageDto){

   if(demandeRemorquageDto != null) {
     try {
         Optional<Consommateur> consommateur = consommateurService.getConsommateur(demandeRemorquageDto.getIdConsommateur());

         DemandeRemorquage demandeRemorquage = new DemandeRemorquage();
         Consommateur entity = consommateur.get();

         demandeRemorquage.setConsommateur(entity);
         //demandeRemorquage.setDescription(demandeRemorquageDto.getDescription());
         if(demandeRemorquageDto.getMarqueVoiture() !=null) demandeRemorquage.setMarqueVoiture(demandeRemorquageDto.getMarqueVoiture());
         if(demandeRemorquageDto.getNbrePersonnes()!=null) demandeRemorquage.setNbrePersonnes(demandeRemorquageDto.getNbrePersonnes());
         if(demandeRemorquageDto.getTypePanne()!=null) demandeRemorquage.setTypePanne(demandeRemorquageDto.getTypePanne());


         //------------------Affecter a un remorqueur assurance si la demande est assurance--------------
         if(demandeRemorquageDto.getTypeRemorquage() !=null && demandeRemorquageDto.getTypeRemorquage().equals("assurance")) {
             demandeRemorquage.setTypeRemorquage(demandeRemorquageDto.getTypeRemorquage());
             List<Remorqueur> remoqueurListe = remorqueurService.getRemorqueurs() ;
             List<Remorqueur> remorqueurAssuranceListe =new ArrayList<>();
             List<Remorqueur> listeRemorqueursAssur = new ArrayList<>();

             //1) trouver la liste des remorqueurs d'assurance
             for(Remorqueur r: remoqueurListe) {
                 Set<Role> roles = r.getConsommateur().getRoles();
                     for(Role ro : roles) {
                         if(ro.getRoleName().equals(ERole.ROLE_R_ASSUR)){
                             listeRemorqueursAssur.add(r) ;
                             break ;
                         }
                     }
             }

             //2) affecter à un remorqueur d'assurance disponible
             for(Remorqueur ra : listeRemorqueursAssur){
                 if(ra.isDisponible() && !ra.isCommandeAssuranceAffected() && ra.isCompteAssurance()){
                     Instant now = Instant.now();
                     Timestamp dateAcceptation = Timestamp.from(now);

                     demandeRemorquage.setRemorqueur(ra);
                     //informer le remorqueur en question
                     ra.setCommandeAssuranceAffected(true);
                     remorqueurService.saveOrUpdateRemorqueur(ra);

                     demandeRemorquage.setIsDeclined(false);

                     //statique à changer avec une methode  de mise à jour de distance et duree pour remorqueur d'assurance
                     demandeRemorquage.setDurreeInMinutes(3);

                     demandeRemorquage.setDateAcceptation(dateAcceptation);
                     demandeRemorquage.setIsdemandeChangedByClient(false);
                     demandeRemorquage.setIsClientPickedUp(false);
                     demandeRemorquage.setIsCanceledByRemorqueur(false);
                     demandeRemorquage.setIsAdresseDepartReachedByRemorqueur(false);
                     break ;
                 }
             }


         }

         //------------------Fin Affecter a un remorqueur assurance si la demande est assurance--------------


         if(demandeRemorquageDto.getTypeRemorquage() !=null && demandeRemorquageDto.getTypeRemorquage().equals("libre")) {
             demandeRemorquage.setTypeRemorquage(demandeRemorquageDto.getTypeRemorquage());
         }


         Instant now = Instant.now();
         demandeRemorquage.setDateCreation(Timestamp.from(now));
         demandeRemorquage.setIsCanceledByClient(false);

         Location depart = new Location(demandeRemorquageDto.getDepartLattitude(), demandeRemorquageDto.getDepartLongitude());
         // depart.setDemandeRemorquageDepart(demandeRemorquage);
         Location destination = new Location(demandeRemorquageDto.getDestinationLattitude(), demandeRemorquageDto.getDestinationLongitude());
//       destination.setDemandeRemorquageDestination(demandeRemorquage);

         demandeRemorquage.setDepartRemorquage(depart);
         demandeRemorquage.setDestinationRemorquage(destination);

         List<DemandeRemorquage> listeDemandeRemorquage = new ArrayList<>();
         listeDemandeRemorquage.add(demandeRemorquage);

         entity.setListeDemandesRemorquage(listeDemandeRemorquage);

         demandeRemorquageRepository.save(demandeRemorquage);
         //consommateurService.saveOrUpdateConsommateur(entity);
         return ResponseEntity.status(HttpStatus.OK).body(demandeRemorquage);
     }
     catch (Exception e) {
       return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur");
     }
   }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur ");

  }

  @GetMapping("/verifierPossibiliteChangementRemorqueur/{idDemande}")
  public ResponseEntity<Object> VerifierPermissionChangementRemorqueur(@PathVariable  Long idDemande) {
    if(idDemande != null) {
      try {
        VerificationChangementRemorqeurResponse resVerif = demandeRemorquageService.permettreChangementRemorqueur(idDemande);

        return ResponseEntity.status(HttpStatus.OK).body(resVerif);
      }
      catch (Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur");
      }

    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("l'id demande ne peut pas étre null");
  }

  @PutMapping("/annulerDemandeParClient/{idDemande}")
  public ResponseEntity<Object> annulerDemandeParClient (@PathVariable Long idDemande) {
    if(idDemande != null) {
//      try {
        DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(idDemande).get();
        demandeRemorquage.setIsCanceledByClient(true);
        demandeRemorquage.setIsFinished(true);




        if(demandeRemorquage.getIsCanceledByRemorqueur() == null || !demandeRemorquage.getIsCanceledByRemorqueur()){
            demandeRemorquage.getRemorqueur().setCommandeAssuranceAffected(false);
            remorqueurService.saveOrUpdateRemorqueur(demandeRemorquage.getRemorqueur());
        }


        demandeRemorquageRepository.save(demandeRemorquage);
        return ResponseEntity.status(HttpStatus.OK).body("modifié avec succés") ;
      }
//      catch (Exception e) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur lors de l'opération de mise à jour") ;
//      }
    //}
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur : idDemande ne peut pas étre null") ;
  }

  @GetMapping("/getDemande/{idDemande}")
  public ResponseEntity<Object> getDemande(@PathVariable Long idDemande) {
    if(idDemande != null ) {
      try{
        DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(idDemande).get();
        return ResponseEntity.status(HttpStatus.OK).body(demandeRemorquage);
      }
      catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }

    }
     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
  }
  @GetMapping("/getRemorqeurFromDemande/{idDemande}")
  public ResponseEntity<Object>  getRemorqeurFromDemande(@PathVariable Long idDemande) {
    if(idDemande != null) {
      try{
        DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(idDemande).get();
        Remorqueur remorqueur = demandeRemorquage.getRemorqueur();
        return ResponseEntity.status(HttpStatus.OK).body(remorqueur);
      }
      catch (Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
      }
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
  }
  @GetMapping("/getAll/{idRemorqeur}")
  //@PreAuthorize("hasRole('REMORQEUR')")
  public ResponseEntity< List<DemandeRemorquage> > getListeDemandes(@PathVariable Long idRemorqeur) {
    if(idRemorqeur != null) {
      List<DemandeRemorquage> listeDemandeRemorquage = demandeRemorquageRepository.findAll();
      //
      List<DemandeRemorquage> liste = new ArrayList<>();
      for (DemandeRemorquage d:listeDemandeRemorquage ) {


                //if remorqeur == null ou un remorqeur a refusé cette demande
          if((d.getRemorqueur() == null) || (d.getRemorqueur().getId() != idRemorqeur  && d.getIsDeclined())) {
            //vérification de la liste des remorqueurs annulés de cette demande

            if(!d.getIsCanceledByClient() && d.getTypeRemorquage().equalsIgnoreCase("libre")) {
                if(d.getListeDemandesRemorquageChangesParClient().size()> 0) {
                    boolean res =demandeRemorquageService.VerfierExisistanceRemorqueurDansListeDesRefuse(d ,idRemorqeur);

                    if(!res) liste.add(d);
                }
                else {
                    liste.add(d);
                }
            }




        }
      }
    // organiser la liste du plus urgent au moins urgent
        Collections.sort(liste, Collections.reverseOrder(new DemandeRemorquage.UrgenceComparator()));
//      for(DemandeRemorquage d : liste) {
//          System.out.println(d.getId() +" "+d.getUrgenceDemande());
//      }
      return ResponseEntity.status(HttpStatus.OK).body(liste);
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);




  }

  @PutMapping("/accepterDemande/")
  public ResponseEntity<Object> accepterDemande(@RequestBody DemandeRemorquageAccepteDto demandeRemorquageAccepteDto) {
   if(demandeRemorquageAccepteDto.getIdDemande() != null && demandeRemorquageAccepteDto.getIdRemorqeur() != null ) {
     Optional<Remorqueur> remorqueur  = remorqueurService.getRemorqueur(demandeRemorquageAccepteDto.getIdRemorqeur() );
     Optional<DemandeRemorquage> demande  = demandeRemorquageRepository.findById(demandeRemorquageAccepteDto.getIdDemande());

     try {
       Instant now = Instant.now();
       Timestamp dateAcceptation = Timestamp.from(now);

       demande.get().setRemorqueur(remorqueur.get());
       demande.get().setIsDeclined(false);
       demande.get().setDurreeInMinutes(demandeRemorquageAccepteDto.getDureeInMin());
       demande.get().setDateAcceptation(dateAcceptation);
       demande.get().setIsdemandeChangedByClient(false);
       demande.get().setIsClientPickedUp(false);
       demande.get().setIsCanceledByRemorqueur(false);
       demande.get().setIsAdresseDepartReachedByRemorqueur(false);

       demandeRemorquageRepository.save(demande.get());
       return ResponseEntity.status(HttpStatus.OK).body(demande);
     }
     catch (Exception e) {
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body("erreur");
     }

   }


    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("idDemande et idRemorqeur ne peuvent pas étre null");
  }

 @PutMapping("/finirCourse/{idDemande}")
 public  ResponseEntity<Object> finirCourse (@PathVariable Long idDemande) {
    if(idDemande != null) {
      try{
        DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(idDemande).get();
        demandeRemorquage.setIsFinished(true);
        demandeRemorquageRepository.save(demandeRemorquage);
        return ResponseEntity.status(HttpStatus.OK).body(demandeRemorquage);
      }
      catch (Exception e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("erreur");
      }
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("l'id de la demande ne peut pas étre null");
 }

  @PutMapping("/confirmerPickedUp/{idDemande}")
  public  ResponseEntity<Object> updateCourse (@PathVariable Long idDemande ) {
    if(idDemande != null ) {
      try{
        Optional<DemandeRemorquage> demandeRemorquage = demandeRemorquageRepository.findById(idDemande);
        demandeRemorquage.get().setIsClientPickedUp(true);
        demandeRemorquage.get().setIsAdresseDepartReachedByRemorqueur(false);

        demandeRemorquageRepository.save(demandeRemorquage.get());
        return ResponseEntity.status(HttpStatus.OK).body(demandeRemorquage.get());
      }
      catch (Exception e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
      }
    }
    return ResponseEntity.status(HttpStatus.OK).body("l'id demande ne peut pas étre null");
  }

  @PutMapping("/declineDemande/{idRemorqeur}/{idDemande}")
  //@PreAuthorize("hasRole('REMORQEUR')")
  public ResponseEntity< Object > declineDemande(@PathVariable Long idRemorqeur ,@PathVariable Long idDemande  ) {
    if( idRemorqeur != null && idDemande != null) {

        try {

            Remorqueur remorqueur = remorqueurService.getRemorqueur(idRemorqeur).get();
            DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(idDemande).get();

            remorqueur.getListeDemandesRemorquage().add(demandeRemorquage);
            demandeRemorquage.setRemorqueur(remorqueur);
            demandeRemorquage.setIsDeclined(true);
            demandeRemorquageRepository.save(demandeRemorquage);
          return ResponseEntity.status(HttpStatus.OK).body("succes");
        } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("echec");
        }

    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("verifier l'id de la demande");
  }



  @PutMapping("/changerRemorqeur/{idDemande}")
  public ResponseEntity<Object> changerRemorqeur(@PathVariable Long idDemande) {
      if(idDemande != null) {
          try {
              DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(idDemande).get();
              Remorqueur remorqueurRefuse = demandeRemorquage.getRemorqueur();
              DemandeRemorqeurChangeParClient demandeRemorqeurChangeParClient = new DemandeRemorqeurChangeParClient();
              demandeRemorqeurChangeParClient.setDemande(demandeRemorquage);
              demandeRemorqeurChangeParClient.setRemorqeurRefuse(remorqueurRefuse);
              demandeRemorqeurChangeParClient.setRaisonChangement("retard");



              demandeRemorquage.getListeDemandesRemorquageChangesParClient().add(demandeRemorqeurChangeParClient);
              demandeRemorquage.setRemorqueur(null);
              demandeRemorquage.setDurreeInMinutes(0);
              demandeRemorquage.setDateAcceptation(null);

              demandeRemorquage.setIsDeclined(null);
              demandeRemorquage.setIsdemandeChangedByClient(true);
              demandeRemorquage.setIsAdresseDepartReachedByRemorqueur(false);


              //si Demande assurance affecter à un remorqeur d'assurance
              if(demandeRemorquage.getTypeRemorquage().equalsIgnoreCase("assurance")) {
                  List<Remorqueur> listeRemorqeur = remorqueurService.getRemorqueurs() ;
                  List<Remorqueur> listeRemorqueurAssurance = new ArrayList<>();

                  //get la liste des remorqueurs assurance
                  for(Remorqueur r : listeRemorqeur) {
                      Set<Role> listeRoles = r.getConsommateur().getRoles();
                      for(Role ro : listeRoles) {
                          if(ro.getRoleName().equals(ERole.ROLE_R_ASSUR)) {
                              listeRemorqueurAssurance.add(r);
                          }
                      }

                  }

                  Remorqueur remorqueurAssuranceEnCharge = null;
                  for(Remorqueur ra : listeRemorqueurAssurance) {
                      //check if he is disponible
                      if(ra.isDisponible() && !ra.isCommandeAssuranceAffected() && ra.isCompteAssurance()){

                          //check if he is not in the list of refused
                          for(DemandeRemorqeurChangeParClient d :demandeRemorquage.getListeDemandesRemorquageChangesParClient()){
                              if(d.getRemorqeurRefuse().getId() != ra.getId()) {
                                  remorqueurAssuranceEnCharge = ra ;
                                  break ;
                              }
                          }
                      }
                  }



                  //affecter le remorqueur choisi au demande

                  Instant now = Instant.now();
                  Timestamp dateAcceptation = Timestamp.from(now);

                  demandeRemorquage.setRemorqueur(remorqueurAssuranceEnCharge);
                  //informer le remorqueur en question
                  remorqueurAssuranceEnCharge.setCommandeAssuranceAffected(true);
                  remorqueurService.saveOrUpdateRemorqueur(remorqueurAssuranceEnCharge);

                  demandeRemorquage.setIsDeclined(false);

                  //statique à changer avec une methode  de mise à jour de distance et duree pour remorqueur d'assurance
                  demandeRemorquage.setDurreeInMinutes(3);

                  demandeRemorquage.setDateAcceptation(dateAcceptation);



                  demandeRemorquage.setIsAdresseDepartReachedByRemorqueur(false);



              }

              demandeRemorquageRepository.save(demandeRemorquage);



              //2)-------- ajouter et affecter la reclamation au remorqeur en question

              Reclamation reclamation = new Reclamation() ;
              //if(!reclamationDto.getDescription().isEmpty()) reclamation.setDescription(reclamationDto.getDescription());
              reclamation.setRemorqueur(remorqueurRefuse);
              reclamation.setTypeReclamation(ETypeReclamation.RETARD);

              Instant today = Instant.now();

              reclamation.setDateAjout( Timestamp.from(today));

              remorqueurRefuse.getListeReclamations().add(reclamation);

              remorqueurRefuse.setCommandeAssuranceAffected(false);


              remorqueurService.saveOrUpdateRemorqueur(remorqueurRefuse);

              return ResponseEntity.status(HttpStatus.OK).body(demandeRemorquage) ;
          }
          catch (Exception e) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("erreur") ;
          }

      }





      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("l'id de la demande ne peut pas étre null") ;
  }


  @PutMapping("/annulerDemandeDuRemorqueur/{idDemande}/{raison}")
  public ResponseEntity<Object> annulerCommandeRemorquageDuRemorqueur (@PathVariable Long idDemande ,@PathVariable String raison) {

      if(idDemande != null && raison !=null) {
          try {
              DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(idDemande).get();
              Remorqueur remorqueurRefuse = demandeRemorquage.getRemorqueur();


              if(demandeRemorquage.getTypeRemorquage().equalsIgnoreCase("assurance")) {
                  remorqueurRefuse.setCommandeAssuranceAffected(false);
              }
              remorqueurService.saveOrUpdateRemorqueur(remorqueurRefuse);


              DemandeRemorqeurChangeParClient demandeRemorqeurChangeParClient = new DemandeRemorqeurChangeParClient();
              demandeRemorqeurChangeParClient.setDemande(demandeRemorquage);
              demandeRemorqeurChangeParClient.setRemorqeurRefuse(remorqueurRefuse);
              demandeRemorqeurChangeParClient.setRaisonChangement(raison);



              demandeRemorquage.getListeDemandesRemorquageChangesParClient().add(demandeRemorqeurChangeParClient);
              demandeRemorquage.setRemorqueur(null);
              demandeRemorquage.setDurreeInMinutes(0);
              demandeRemorquage.setDateAcceptation(null);

              demandeRemorquage.setIsDeclined(null);
              demandeRemorquage.setIsCanceledByRemorqueur(true);

              if(demandeRemorquage.getUrgenceDemande() != null ) demandeRemorquage.setUrgenceDemande(demandeRemorquage.getUrgenceDemande()+1);





              //remorqueurService.saveOrUpdateRemorqueur(remorqueurAffecte);
              demandeRemorquageRepository.save(demandeRemorquage);

              return ResponseEntity.status(HttpStatus.OK).body(demandeRemorquage);

          }
          catch (Exception e) {
              return ResponseEntity.status(HttpStatus.NOT_FOUND).body("erreur");
          }
      }


      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("l'id de la demande et la raison ne peuvent pas étre null");
  }


  @PutMapping("/updateDemandeApresAnnulationRemorqeur/{idDemande}/{longitude}/{latitude}")
  public ResponseEntity<Object> updateCoordonnesCommandeRemorquageApresPickedUp (@PathVariable Long idDemande, @PathVariable Double longitude ,@PathVariable Double latitude) {

      if(idDemande != null && longitude!=null && latitude !=null) {
          try{
              DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(idDemande).get();

              //  si assurance affecter a un remorqeur disponible et non refuse de la demande
                if(demandeRemorquage.getTypeRemorquage().equalsIgnoreCase("assurance")) {
                    List<Remorqueur> listeRemorqeur = remorqueurService.getRemorqueurs() ;
                    List<Remorqueur> listeRemorqueurAssurance = new ArrayList<>();

                    //get la liste des remorqueurs assurance
                    for(Remorqueur r : listeRemorqeur) {
                        Set<Role> listeRoles = r.getConsommateur().getRoles();
                        for(Role ro : listeRoles) {
                            if(ro.getRoleName().equals(ERole.ROLE_R_ASSUR)) {
                                listeRemorqueurAssurance.add(r);
                            }
                        }

                    }

                    Remorqueur remorqueurAssuranceEnCharge =null;

                    //affecter à un remorqueur d'assurance
                    for(Remorqueur ra : listeRemorqueurAssurance) {
                        //check if he is disponible
                        if(ra.isDisponible() && !ra.isCommandeAssuranceAffected() && ra.isCompteAssurance()){

                            //check if he is not in the list of refused
                            for(DemandeRemorqeurChangeParClient d :demandeRemorquage.getListeDemandesRemorquageChangesParClient()){
                                if(d.getRemorqeurRefuse().getId() != ra.getId()) {
                                    remorqueurAssuranceEnCharge = ra ;
                                    break ;
                                }
                            }
                        }
                    }



                    //affecter le remorqueur choisi au demande

                    Instant now = Instant.now();
                    Timestamp dateAcceptation = Timestamp.from(now);

                    demandeRemorquage.setRemorqueur(remorqueurAssuranceEnCharge);
                    //informer le remorqueur en question
                    remorqueurAssuranceEnCharge.setCommandeAssuranceAffected(true);
                    remorqueurService.saveOrUpdateRemorqueur(remorqueurAssuranceEnCharge);

                    demandeRemorquage.setIsDeclined(false);

                    //statique à changer avec une methode  de mise à jour de distance et duree pour remorqueur d'assurance
                    demandeRemorquage.setDurreeInMinutes(3);

                    demandeRemorquage.setDateAcceptation(dateAcceptation);



                    demandeRemorquage.setIsAdresseDepartReachedByRemorqueur(false);



                }


              demandeRemorquage.getDepartRemorquage().setLongitude(longitude);
              demandeRemorquage.getDepartRemorquage().setLattitude(latitude);
              demandeRemorquage.setIsdemandeChangedByClient(false);
              demandeRemorquage.setIsClientPickedUp(false);
              demandeRemorquage.setIsCanceledByRemorqueur(false);
              //demandeRemorquage.setUrgenceDemande(demandeRemorquage.getUrgenceDemande()+1);
              demandeRemorquageRepository.save(demandeRemorquage);
              return ResponseEntity.status(HttpStatus.OK).body(demandeRemorquage);
          }
          catch (Exception e) {
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("erreur");
          }

      }

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("l'id demande et les coordonnes ne peuvent pas étre null");

  }

    @PutMapping("/updateDemandeApresAnnulationRemorqeurAvantPickedUp/{idDemande}")
    public ResponseEntity<Object> updateCoordonnesCommandeRemorquageAvantPickedUp (@PathVariable Long idDemande) {

        if(idDemande != null ) {
            try{
                DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(idDemande).get();

                //  si assurance affecter a un remorqeur disponible et non refuse de la demande
                if(demandeRemorquage.getTypeRemorquage().equalsIgnoreCase("assurance")) {
                    List<Remorqueur> listeRemorqeur = remorqueurService.getRemorqueurs() ;
                    List<Remorqueur> listeRemorqueurAssurance = new ArrayList<>();

                    //get la liste des remorqueurs assurance
                    for(Remorqueur r : listeRemorqeur) {
                        Set<Role> listeRoles = r.getConsommateur().getRoles();
                        for(Role ro : listeRoles) {
                            if(ro.getRoleName().equals(ERole.ROLE_R_ASSUR)) {
                                listeRemorqueurAssurance.add(r);
                            }
                        }

                    }

                    Remorqueur remorqueurAssuranceEnCharge = null;
                    for(Remorqueur ra : listeRemorqueurAssurance) {
                        //check if he is disponible
                        if(ra.isDisponible() && !ra.isCommandeAssuranceAffected() && ra.isCompteAssurance()){

                            //check if he is not in the list of refused
                            for(DemandeRemorqeurChangeParClient d :demandeRemorquage.getListeDemandesRemorquageChangesParClient()){
                                if(d.getRemorqeurRefuse().getId() != ra.getId()) {
                                    remorqueurAssuranceEnCharge = ra ;
                                    break ;
                                }
                            }
                        }
                    }



                    //affecter le remorqueur choisi au demande

                    Instant now = Instant.now();
                    Timestamp dateAcceptation = Timestamp.from(now);

                    demandeRemorquage.setRemorqueur(remorqueurAssuranceEnCharge);
                    //informer le remorqueur en question
                    remorqueurAssuranceEnCharge.setCommandeAssuranceAffected(true);
                    remorqueurService.saveOrUpdateRemorqueur(remorqueurAssuranceEnCharge);

                    demandeRemorquage.setIsDeclined(false);

                    //statique à changer avec une methode  de mise à jour de distance et duree pour remorqueur d'assurance
                    demandeRemorquage.setDurreeInMinutes(3);

                    demandeRemorquage.setDateAcceptation(dateAcceptation);



                    demandeRemorquage.setIsAdresseDepartReachedByRemorqueur(false);



                }


                demandeRemorquage.setIsdemandeChangedByClient(false);
                demandeRemorquage.setIsClientPickedUp(false);
                demandeRemorquage.setIsCanceledByRemorqueur(false);
                //demandeRemorquage.setUrgenceDemande(demandeRemorquage.getUrgenceDemande()+1);
                demandeRemorquageRepository.save(demandeRemorquage);
                return ResponseEntity.status(HttpStatus.OK).body(demandeRemorquage);
            }
            catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("erreur");
            }

        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("l'id demande ne peut pas étre null");

    }

  }
