package com.onegateafrica.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class DemandeRemorqeurChangeParClient {

    @Id
    @GeneratedValue
    private Long id ;


    @ManyToOne
    @JoinColumn(name="id_Demande", nullable=false )
    private DemandeRemorquage Demande ;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="id_RemorqueurRefuse", nullable=false )
    private Remorqueur RemorqeurRefuse ;

    private Timestamp dateChangement ;
    private String raisonChangement ;


}
