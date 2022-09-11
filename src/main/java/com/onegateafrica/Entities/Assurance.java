package com.onegateafrica.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity

public class Assurance {

    @Id
    @GeneratedValue
    private Long id  ;


    private String nom ;
    private String acrnonyme;

    @OneToMany(mappedBy = "assurance" ,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Consommateur> clientsAffectesListe ;

    @OneToMany(mappedBy = "assurance" ,fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Remorqueur> remorqueursAffectesListe ;
}
