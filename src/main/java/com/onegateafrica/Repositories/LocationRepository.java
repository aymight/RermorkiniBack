package com.onegateafrica.Repositories;


import com.onegateafrica.Entities.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
