package com.onegateafrica.Service;

import com.onegateafrica.Controllers.utils.IntervalWeekUtils;
import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.Reclamation;
import org.springframework.data.repository.query.Param;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReclamationService {
    Reclamation saveOrUpdateReclamation(Reclamation reclamation);

    List<Reclamation> getReclamations();

    Optional<Reclamation> getReclamation(Long id);

    void deleteReclamation(Long id);

    Optional<List<Reclamation>> getReclamationsOfRemorqeur( Long idRemorqueur) ;
    Optional<List<Reclamation>> getReclamationsOfClient (Long idConsommateur);
    List<Reclamation> getReclamationsOfWeek(List<Reclamation> listeReclamationOfRemorqueur , String leftWeekIntervall , String rightWeekIntervall) throws ParseException;
    List<Reclamation> getReclamationsClientOfWeek(List<Reclamation> listeReclamationOfClient , String leftWeekIntervall , String rightWeekIntervall) throws ParseException;
    IntervalWeekUtils calculateWeekFromToday(Instant today );
    String traiterBannOfClient(Long idConsommateur , List<Reclamation> listeReclamationsOfClientInWeek);
    String traiterBann(Long idRemorqueur ,List<Reclamation> listeReclamationOfRemorqueurInWeek);
}
