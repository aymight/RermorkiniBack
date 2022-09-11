package com.onegateafrica.Service;

import com.onegateafrica.Entities.Remorqueur;


import java.util.List;
import java.util.Optional;

public interface RemorqueurService {


    Remorqueur saveOrUpdateRemorqueur(Remorqueur remorqueur);


    List<Remorqueur> getRemorqueurs();
    Optional<Remorqueur> getRemorqueur(Long id);
    void  deleteRemorqueur(Long id);
    Optional<Remorqueur> findRemorqueurByPhoneNumber(String phoneNumber);
    Optional<Remorqueur> findRemorqeurByCIN(String cin);

    List<Remorqueur> findAll();

    void updateDisponibility( long id,  boolean disponibility);

    Optional<Remorqueur> getConsommateurAsRemorqeur(Long idConsommateur);


}
