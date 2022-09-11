package com.onegateafrica.Repositories;

import java.util.Optional;

import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.Remorqueur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RemorqueurRepository extends JpaRepository<Remorqueur,Long> {
  Optional<Remorqueur> findByCinNumber(String cinNumber);
  @Query(value = "SELECT * FROM remorqeur WHERE consommateur_id = (SELECT id FROM consommateur WHERE phone_number = :phoneNumber)", nativeQuery = true)
  Optional<Remorqueur> findByPhoneNumber(@Param(value="phoneNumber") String phoneNumber);

  @Modifying
  @Query("update Remorqueur u set u.isDisponible = :disponibility where u.id = :id")
  void updateDisponibility(@Param(value = "id") long id, @Param(value = "disponibility") boolean disponibility);

  @Query(value = "select * FROM remorqeur as r  where r.consommateur_id = :idConsommateur" , nativeQuery = true )
  Optional<Remorqueur>  getUserAsRemorqeur(@Param(value="idConsommateur") long idConsommateur) ;
}
