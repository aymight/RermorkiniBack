package com.onegateafrica.Service;

import com.onegateafrica.Entities.DemandeRemorquage;
import com.onegateafrica.Payloads.response.VerificationChangementRemorqeurResponse;

public interface DemandeRemorquageService {
    VerificationChangementRemorqeurResponse permettreChangementRemorqueur(Long idDemande);
    boolean VerfierExisistanceRemorqueurDansListeDesRefuse (DemandeRemorquage demandeRemorquage ,Long idRemorqeur);
}
