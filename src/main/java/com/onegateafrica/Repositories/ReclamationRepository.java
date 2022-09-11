package com.onegateafrica.Repositories;

import com.onegateafrica.Entities.Reclamation;
import com.onegateafrica.Entities.Remorqueur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReclamationRepository extends JpaRepository<Reclamation , Long> {
    @Query(value = "select * FROM reclamation as r  where r.remorqueur_id = :idRemorqueur"  , nativeQuery = true )
    Optional<List<Reclamation>> getReclamationsOfRemorqeur(@Param(value="idRemorqueur") long idRemorqueur ) ;

    @Query(value = "select * FROM reclamation as c  where c.consommateur_id = :idConsommateur"  , nativeQuery = true )
    Optional<List<Reclamation>> getReclamationsOfClient(@Param(value="idConsommateur") long idConsommateur ) ;
}
