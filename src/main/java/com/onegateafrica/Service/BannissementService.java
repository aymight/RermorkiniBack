package com.onegateafrica.Service;

import com.onegateafrica.Entities.Bannissement;
import com.onegateafrica.Entities.Reclamation;
import com.onegateafrica.Payloads.response.BannResponse;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BannissementService {
    Bannissement saveOrUpdateBannissement(Bannissement bannissement);

    List<Bannissement> getBannisements();

    Optional<Bannissement> getBannissement(Long id);

    void deleteBannissement(Long id);

    Optional<List<Bannissement>> getBannissementOfRemorqeur( long idRemorqueur ) ;
    BannResponse verifierBann(Long idRemorqeur );

    Optional<List<Bannissement>> getBannissementOfClient( long idConsommateur ) ;
    BannResponse verifierBannOfClient(Long idConsommateur );


}

