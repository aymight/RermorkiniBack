package com.onegateafrica.Entities;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
public class Consommateur extends User {

    @Column(name = "numeroinscription")
    private String numeroInscription;


    @JsonBackReference
    @OneToOne(mappedBy = "consommateur")
    private Remorqueur remorqueur;

    @Column(name="expopushtoken")
    private String expoPushToken ;


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(	name = "user_roles",
        joinColumns = @JoinColumn(name = "id"),
        inverseJoinColumns = @JoinColumn(name = "roleId"))
    private Set<Role> roles = new HashSet<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "consommateur",cascade = CascadeType.ALL  )
    private List<DemandeRemorquage> listeDemandesRemorquage ;

    @ManyToOne
    private Assurance assurance ;


    @OneToMany(fetch =FetchType.LAZY ,mappedBy = "consommateur",cascade = CascadeType.ALL)
    private List<Reclamation> listeReclamations ;

    @OneToMany(fetch =FetchType.LAZY ,mappedBy = "consommateur",cascade = CascadeType.ALL)
    private List<Bannissement> listeBannissements ;

    @Column(name="is_banned")
    private Boolean isBanned =false ;


    @Column(name ="nombreDeVote")
    private double nombreDeVote=1;

    @Column(name ="noteRemorqueurMoyenne")
    private double noteConsommateurMoyenne=1;


    @OneToMany(fetch =FetchType.LAZY ,mappedBy = "consommateur",cascade = CascadeType.ALL)
    private List<Location> listeEmplacementsFavoris ;


}
