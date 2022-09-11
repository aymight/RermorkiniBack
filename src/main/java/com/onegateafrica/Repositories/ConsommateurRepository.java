package com.onegateafrica.Repositories;

import com.onegateafrica.Entities.Consommateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface ConsommateurRepository extends JpaRepository<Consommateur, Long> {


    Consommateur findByPhoneNumber(String PhoneNumber);


    Boolean existsByEmail(String email);


    Optional<Consommateur> findByEmail(String email);

    @Modifying
    @Query(value = "UPDATE consommateur SET is_connected=1, last_activity= :lastActivity WHERE email = :email", nativeQuery = true)
    int keepalive(@Param(value = "email") String email, @Param(value = "lastActivity") Date lastActivity);
	@Modifying
	@Query(value = "UPDATE consommateur SET is_connected=0 WHERE Datediff(s, last_activity, getdate())>6", nativeQuery = true)
	int kill();


}

