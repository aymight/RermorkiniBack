package com.onegateafrica.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class DemandeRemorquage implements Serializable {


  @Id
  @GeneratedValue
  private Long id ;


  @ManyToOne()
  @JoinColumn(name="idConsommateur", nullable=false )
  private Consommateur consommateur ;

  @JsonIgnore
  @ManyToOne()
  @JoinColumn(name="idRemorquer" )
  private Remorqueur remorqueur ;

  @Column(name = "description")
  private String description ;

  @Column(name = "finished")
  private Boolean isFinished ;

  @Column(name = "declined")
  private Boolean isDeclined  ;

  @Column(name = "Clientpickedup")
  private Boolean isClientPickedUp  ;

  private String marqueVoiture;
  private String nbrePersonnes ;
  private String typePanne ;

  @Column(name = "canceledbyclient")
  private Boolean isCanceledByClient ;

  @Column(name="datecreation")
  private Timestamp dateCreation ;

  @Column(name="dateacceptation")
  private Timestamp dateAcceptation ;

  @Column(name="durreinmin")
  private long DurreeInMinutes=0 ;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "departRemorquage")
  private Location departRemorquage ;

  @OneToOne(cascade = CascadeType.ALL )
  @JoinColumn(name = "destinationRemorquage")
  private Location destinationRemorquage ;


  @JsonIgnore
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "Demande",cascade = CascadeType.ALL  )
  private List<DemandeRemorqeurChangeParClient> listeDemandesRemorquageChangesParClient ;

  private Boolean isdemandeChangedByClient ;

  @Column(name = "adresseDepartReachedByRemorqueur")
  private Boolean isAdresseDepartReachedByRemorqueur ;

  @Column(name = "adresseDestinationReachedByRemorqueur")
  private Boolean isAdresseDestinationReachedByRemorqueur ;

  private String typeRemorquage ;

  @Column(name="canceledbyremorqueur")
  private Boolean isCanceledByRemorqueur ;

  private Integer urgenceDemande =0;




  public static class UrgenceComparator implements Comparator<DemandeRemorquage>
  {
    //    @Override
    public int compare(DemandeRemorquage d1, DemandeRemorquage d2) {
      return d1.getUrgenceDemande().compareTo(d2.getUrgenceDemande());
    }


  }



}
