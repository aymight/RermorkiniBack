package com.onegateafrica.Repositories;

import com.onegateafrica.Entities.DemandeRemorquage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DemandeRemorquageRepository extends JpaRepository<DemandeRemorquage, Long> {



}
