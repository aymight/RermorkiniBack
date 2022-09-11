package com.onegateafrica.ServiceImpl;

import com.onegateafrica.Controllers.utils.IntervalWeekUtils;
import com.onegateafrica.Entities.Bannissement;
import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.Reclamation;
import com.onegateafrica.Entities.Remorqueur;
import com.onegateafrica.Repositories.ReclamationRepository;
import com.onegateafrica.Service.BannissementService;
import com.onegateafrica.Service.ConsommateurService;
import com.onegateafrica.Service.ReclamationService;
import com.onegateafrica.Service.RemorqueurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReclamationServiceImpl implements ReclamationService {

    private final ReclamationRepository reclamationRepository ;
    private final BannissementService bannissementService ;
    private final RemorqueurService remorqueurService ;

    private final ConsommateurService consommateurService ;

    @Autowired
    public ReclamationServiceImpl(ReclamationRepository reclamationRepository, BannissementService bannissementService, RemorqueurService remorqueurService, ConsommateurService consommateurService) {
        this.reclamationRepository = reclamationRepository;
        this.bannissementService = bannissementService;
        this.remorqueurService = remorqueurService;
        this.consommateurService = consommateurService;
    }

    @Override
    public Reclamation saveOrUpdateReclamation(Reclamation reclamation) {
        return null;
    }

    @Override
    public List<Reclamation> getReclamations() {
        return null;
    }

    @Override
    public Optional<Reclamation> getReclamation(Long id) {
        return Optional.empty();
    }

    @Override
    public void deleteReclamation(Long id) {

    }

    @Override
    public Optional<List<Reclamation>> getReclamationsOfRemorqeur(Long idRemorqueur) {
        return reclamationRepository.getReclamationsOfRemorqeur(idRemorqueur);
    }

    @Override
    public Optional<List<Reclamation>> getReclamationsOfClient(Long idConsommateur) {

        return reclamationRepository.getReclamationsOfClient(idConsommateur);
    }

    //get the reclamations of a given remorqueur in a given week period
    @Override
    public List<Reclamation> getReclamationsOfWeek(List<Reclamation> listeReclamationOfRemorqueur, String leftWeekIntervall, String rightWeekIntervall) throws ParseException {
        List<Reclamation> searchedList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date parsedLeftDateIntervall = sdf.parse(leftWeekIntervall);
        Date parsedRightDateIntervall = sdf.parse(rightWeekIntervall);



        for( Reclamation r : listeReclamationOfRemorqueur) {
            Date  dateReclmationWithoutTime = sdf.parse(sdf.format(r.getDateAjout()));
            if(dateReclmationWithoutTime.compareTo(parsedLeftDateIntervall) >=0 && dateReclmationWithoutTime.compareTo(parsedRightDateIntervall) <=0){
                searchedList.add(r);
            }
        }



        return searchedList;
    }

    @Override
    public List<Reclamation> getReclamationsClientOfWeek(List<Reclamation> listeReclamationOfClient, String leftWeekIntervall, String rightWeekIntervall) throws ParseException {
        List<Reclamation> searchedList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date parsedLeftDateIntervall = sdf.parse(leftWeekIntervall);
        Date parsedRightDateIntervall = sdf.parse(rightWeekIntervall);



        for( Reclamation r : listeReclamationOfClient) {
            Date  dateReclmationWithoutTime = sdf.parse(sdf.format(r.getDateAjout()));
            if(dateReclmationWithoutTime.compareTo(parsedLeftDateIntervall) >=0 && dateReclmationWithoutTime.compareTo(parsedRightDateIntervall) <=0){
                searchedList.add(r);
            }
        }



        return searchedList;
    }

    //get the week of a given day
    @Override
    public IntervalWeekUtils calculateWeekFromToday(Instant today) {

        IntervalWeekUtils intervalWeekUtils = new IntervalWeekUtils();
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date todayInDateFormat = Date.from(today);
        String todayInDayFormat = dayFormat.format(todayInDateFormat).toLowerCase();
        System.out.println(todayInDayFormat+" this is the day received");



        String leftIntervall ;
        String rightIntervall ;
        Instant weekBeginning ;
        switch (todayInDayFormat) {
            case "lundi" :
                leftIntervall = dateFormat.format(todayInDateFormat);
                intervalWeekUtils.setLeftDateIntervall(leftIntervall);
                rightIntervall = dateFormat.format(Date.from(today.plus(Duration.ofDays(7))));
                intervalWeekUtils.setRightDateIntervall(rightIntervall);

                System.out.println("this is the week  from service "+ leftIntervall+" "+ rightIntervall);

                break;

            case "mardi" :

                weekBeginning = today.minus(Duration.ofDays(1)) ;

                leftIntervall = dateFormat.format(Date.from(weekBeginning));

                intervalWeekUtils.setLeftDateIntervall(leftIntervall);

                rightIntervall= dateFormat.format(Date.from(weekBeginning.plus(Duration.ofDays(7))));
                intervalWeekUtils.setRightDateIntervall(rightIntervall);

                System.out.println("this is the week from service"+ leftIntervall+" "+ rightIntervall);
                break;
            case "mercredi" :
                System.out.println("mercreddiiiiiiiiiiiiiii");
                weekBeginning = today.minus(Duration.ofDays(2)) ;

                leftIntervall = dateFormat.format(Date.from(weekBeginning)) ;
                intervalWeekUtils.setLeftDateIntervall(leftIntervall);

                rightIntervall= dateFormat.format(Date.from(weekBeginning.plus(Duration.ofDays(7))));
                intervalWeekUtils.setRightDateIntervall(rightIntervall);

                System.out.println("this is the week from service"+ leftIntervall+" "+ rightIntervall);

                break;
            case "jeudi" :
                System.out.println("jeudiiii");
                weekBeginning = today.minus(Duration.ofDays(3)) ;

                leftIntervall = dateFormat.format(Date.from(weekBeginning)) ;
                intervalWeekUtils.setLeftDateIntervall(leftIntervall);

                rightIntervall= dateFormat.format(Date.from(weekBeginning.plus(Duration.ofDays(7))));
                intervalWeekUtils.setRightDateIntervall(rightIntervall);
                System.out.println("this is the week from service"+ leftIntervall+" "+ rightIntervall);

                break;

            case "vendredi" :
                System.out.println("vendredi");
                weekBeginning = today.minus(Duration.ofDays(4)) ;

                leftIntervall = dateFormat.format(Date.from(weekBeginning)) ;
                intervalWeekUtils.setLeftDateIntervall(leftIntervall);

                rightIntervall= dateFormat.format(Date.from(weekBeginning.plus(Duration.ofDays(7))));
                intervalWeekUtils.setRightDateIntervall(rightIntervall);

                System.out.println("this is the week from service "+ leftIntervall+" "+ rightIntervall);

                break;

            case "samedi" :
                System.out.println("samedi");
                weekBeginning = today.minus(Duration.ofDays(5)) ;

                leftIntervall = dateFormat.format(Date.from(weekBeginning)) ;
                intervalWeekUtils.setLeftDateIntervall(leftIntervall);

                rightIntervall= dateFormat.format(Date.from(weekBeginning.plus(Duration.ofDays(7))));
                intervalWeekUtils.setRightDateIntervall(rightIntervall);
                System.out.println("this is the week from service"+ leftIntervall+" "+ rightIntervall);

                break;

            case "dimanche" :
                System.out.println("dimanche");
                weekBeginning = today.minus(Duration.ofDays(6)) ;

                leftIntervall = dateFormat.format(Date.from(weekBeginning)) ;
                intervalWeekUtils.setLeftDateIntervall(leftIntervall);

                rightIntervall= dateFormat.format(Date.from(weekBeginning.plus(Duration.ofDays(7))));
                intervalWeekUtils.setRightDateIntervall(rightIntervall);
                System.out.println("this is the week from service"+ leftIntervall+" "+ rightIntervall);

                break;

        }

        return intervalWeekUtils;
    }

    @Override
    public String traiterBannOfClient(Long idConsommateur, List<Reclamation> listeReclamationsOfClientInWeek) {
        List<Bannissement> listeBannOfClient = bannissementService.getBannissementOfClient(idConsommateur).get();
        System.out.println("le nombre des reclamations par week est "+listeReclamationsOfClientInWeek.size());
        if( listeReclamationsOfClientInWeek !=null && listeReclamationsOfClientInWeek.size()>=5 ) {

            Bannissement bannissement = new Bannissement();
            Timestamp dateDebutBann ;
            Timestamp dateFinBann ;
            long nbreJoursBann ;
            Instant today = Instant.now();
            Consommateur consommateur ;

            //la premiére fois => donc il n'a pas avant un bann
            if(listeBannOfClient.size()==0) {
                //1)------- définir la date debut et date fin de bann
                nbreJoursBann = 3;
                bannissement.setNbrJoursBann(nbreJoursBann);
                dateDebutBann = Timestamp.from(today);
                bannissement.setDateDebutBann( dateDebutBann);
                dateFinBann = Timestamp.from(today.plus(Duration.ofDays(nbreJoursBann)));
                bannissement.setDateFinBann(( dateFinBann));

                //2)-------- affecter le bann au concommateur
                consommateur = consommateurService.getConsommateur(idConsommateur).get();
                bannissement.setConsommateur(consommateur);
                consommateur.getListeBannissements().add(bannissement);

                //3)------------- set isBanned du consommateur à true
                consommateur.setIsBanned(true);

                consommateurService.saveOrUpdateConsommateur(consommateur);

                return "le client avec l'id "+idConsommateur+" est banni de "+nbreJoursBann +" jours";
            }

            //la deuxiéme fois => donc il a  avant un seul bann
            if(listeBannOfClient.size()==1) {
                //1)------- définir la date debut et date fin de bann
                nbreJoursBann = 10;
                bannissement.setNbrJoursBann(nbreJoursBann);
                dateDebutBann = Timestamp.from(today);
                bannissement.setDateDebutBann(( dateDebutBann));
                dateFinBann = Timestamp.from(today.plus(Duration.ofDays(nbreJoursBann)));
                bannissement.setDateFinBann((dateFinBann));

                //2)-------- affecter le bann au consommateur
                consommateur =consommateurService.getConsommateur(idConsommateur).get();
                bannissement.setConsommateur(consommateur);
                consommateur.getListeBannissements().add(bannissement);

                //3)------------- set isBanned du remorqueur à true
                consommateur.setIsBanned(true);

                consommateurService.saveOrUpdateConsommateur(consommateur);

                return "le client avec l'id "+idConsommateur+" est banni de "+nbreJoursBann +" jours";
            }

            //la troisiéme fois => donc il a  avant deux bann
            //a verifier si toujours aprés 3 bann on donne toujours un bann de 30 jours
            if(listeBannOfClient.size()>=2) {
                //1)------- définir la date debut et date fin de bann
                nbreJoursBann = 30;
                bannissement.setNbrJoursBann(nbreJoursBann);
                dateDebutBann = Timestamp.from(today);
                bannissement.setDateDebutBann( dateDebutBann);
                dateFinBann = Timestamp.from(today.plus(Duration.ofDays(nbreJoursBann)));
                bannissement.setDateFinBann( dateFinBann);

                //2)-------- affecter le bann au remorqeur
                consommateur = consommateurService.getConsommateur(idConsommateur).get();
                bannissement.setConsommateur(consommateur);
                consommateur.getListeBannissements().add(bannissement);
                //3)------------- set isBanned du remorqueur à true
                consommateur.setIsBanned(true);

                consommateurService.saveOrUpdateConsommateur(consommateur);

                return "le client avec l'id "+idConsommateur+" est banni de "+nbreJoursBann +" jours";
            }
        }

        return "le client avec l'id "+idConsommateur+" est n'est pas banni  ";
    }


    @Override
    public String traiterBann(Long idRemorqueur, List<Reclamation> listeReclamationOfRemorqueurInWeek) {
        List<Bannissement> listeBannOfRemorquer = bannissementService.getBannissementOfRemorqeur(idRemorqueur).get();

        if( listeReclamationOfRemorqueurInWeek !=null && listeReclamationOfRemorqueurInWeek.size()>=5 ) {

          Bannissement bannissement = new Bannissement();
          Timestamp dateDebutBann ;
          Timestamp dateFinBann ;
          long nbreJoursBann ;
          Instant today = Instant.now();
            Remorqueur remorqueur ;

            //la premiére fois => donc il n'a pas avant un bann
            if(listeBannOfRemorquer.size()==0) {
                //1)------- définir la date debut et date fin de bann
                nbreJoursBann = 3;
                bannissement.setNbrJoursBann(nbreJoursBann);
                dateDebutBann = Timestamp.from(today);
                bannissement.setDateDebutBann( dateDebutBann);
                dateFinBann = Timestamp.from(today.plus(Duration.ofDays(nbreJoursBann)));
                bannissement.setDateFinBann(( dateFinBann));

                //2)-------- affecter le bann au remorqeur
                remorqueur = remorqueurService.getRemorqueur(idRemorqueur).get();
                bannissement.setRemorqueur(remorqueur);
                remorqueur.getListeBannissements().add(bannissement);
                //3)------------- set isBanned du remorqueur à true
                remorqueur.setIsBanned(true);

                remorqueurService.saveOrUpdateRemorqueur(remorqueur);
                return "le remorqeur avec l'id "+idRemorqueur+" est banni de "+nbreJoursBann +" jours";
            }

            //la deuxiéme fois => donc il a  avant un seul bann
            if(listeBannOfRemorquer.size()==1) {
                //1)------- définir la date debut et date fin de bann
                nbreJoursBann = 10;
                bannissement.setNbrJoursBann(nbreJoursBann);
                dateDebutBann = Timestamp.from(today);
                bannissement.setDateDebutBann(( dateDebutBann));
                dateFinBann = Timestamp.from(today.plus(Duration.ofDays(nbreJoursBann)));
                bannissement.setDateFinBann((dateFinBann));

                //2)-------- affecter le bann au remorqeur
                remorqueur = remorqueurService.getRemorqueur(idRemorqueur).get();
                bannissement.setRemorqueur(remorqueur);
                remorqueur.getListeBannissements().add(bannissement);
                //3)------------- set isBanned du remorqueur à true
                remorqueur.setIsBanned(true);

                remorqueurService.saveOrUpdateRemorqueur(remorqueur);
                return "le remorqeur avec l'id "+idRemorqueur+" est banni de "+nbreJoursBann +" jours";
            }

            //la troisiéme fois => donc il a  avant deux bann
            //a verifier si toujours aprés 3 bann on donne toujours un bann de 30 jours
            if(listeBannOfRemorquer.size()>=2) {
                //1)------- définir la date debut et date fin de bann
                nbreJoursBann = 30;
                bannissement.setNbrJoursBann(nbreJoursBann);
                dateDebutBann = Timestamp.from(today);
                bannissement.setDateDebutBann( dateDebutBann);
                dateFinBann = Timestamp.from(today.plus(Duration.ofDays(nbreJoursBann)));
                bannissement.setDateFinBann( dateFinBann);

                //2)-------- affecter le bann au remorqeur
                remorqueur = remorqueurService.getRemorqueur(idRemorqueur).get();
                bannissement.setRemorqueur(remorqueur);
                remorqueur.getListeBannissements().add(bannissement);
                //3)------------- set isBanned du remorqueur à true
                remorqueur.setIsBanned(true);

                remorqueurService.saveOrUpdateRemorqueur(remorqueur);
                return "le remorqeur avec l'id "+idRemorqueur+" est banni de "+nbreJoursBann +" jours";
            }
        }

        return "le remorqeur avec l'id "+idRemorqueur+" est n'est pas banni  ";
    }

}
