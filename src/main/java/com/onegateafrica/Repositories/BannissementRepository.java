package com.onegateafrica.Repositories;

import com.onegateafrica.Entities.Bannissement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BannissementRepository extends JpaRepository<Bannissement,Long> {
    @Query(value = "select * FROM bannissement as b  where b.remorqueur_id = :idRemorqueur"  , nativeQuery = true )
    Optional<List<Bannissement>> getBannissementOfRemorqeur(@Param(value="idRemorqueur") long idRemorqueur ) ;


    @Query(value = "select * FROM bannissement as c where c.consommateur_id = :idConsommateur"  , nativeQuery = true )
    Optional<List<Bannissement>> getBannissementOfClient(@Param(value="idConsommateur") long idConsommateur ) ;
}
