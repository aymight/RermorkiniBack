package com.onegateafrica.ServiceImpl;

import com.onegateafrica.Entities.DemandeRemorqeurChangeParClient;
import com.onegateafrica.Entities.DemandeRemorquage;
import com.onegateafrica.Payloads.response.VerificationChangementRemorqeurResponse;
import com.onegateafrica.Repositories.DemandeRemorquageRepository;
import com.onegateafrica.Service.DemandeRemorquageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
@Transactional
public class DemandeRemorquageServiceImpl implements DemandeRemorquageService {

    private final DemandeRemorquageRepository demandeRemorquageRepository ;

    @Autowired
    public DemandeRemorquageServiceImpl(DemandeRemorquageRepository demandeRemorquageRepository) {
        this.demandeRemorquageRepository = demandeRemorquageRepository;
    }

    @Override
    public VerificationChangementRemorqeurResponse permettreChangementRemorqueur(Long idDemande) {
        DemandeRemorquage demandeRemorquage = demandeRemorquageRepository.findById(idDemande).get();

        Date now = Date.from(Instant.now());
        Instant dateAcceptationInInstant = demandeRemorquage.getDateAcceptation().toInstant();
        Date dateFin = Date.from(dateAcceptationInInstant.plus(Duration.ofMinutes(demandeRemorquage.getDurreeInMinutes())));

        VerificationChangementRemorqeurResponse res = new VerificationChangementRemorqeurResponse();
        res.setFinDuree(dateFin.toString());


        //verifier si on a dépassé la durée on retourne true sinon false
        if(now.compareTo((dateFin)) > 0 && (demandeRemorquage.getIsClientPickedUp() ==null || demandeRemorquage.getIsClientPickedUp()==false ) ) {
            res.setChangingPermitted(true);
            return res ;
        }
        res.setChangingPermitted(false);
        return res ;




    }

    @Override
    public boolean VerfierExisistanceRemorqueurDansListeDesRefuse(DemandeRemorquage demandeRemorquage, Long idRemorqeur) {

        for(DemandeRemorqeurChangeParClient d : demandeRemorquage.getListeDemandesRemorquageChangesParClient()){
            if(d.getRemorqeurRefuse().getId() == idRemorqeur) return true ;
        }

        return false;
    }
}
