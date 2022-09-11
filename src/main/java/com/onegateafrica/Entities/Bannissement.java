package com.onegateafrica.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "bannissement")
public class Bannissement {

    @Id
    @GeneratedValue
    private Long id ;

    @Column(name = "nbrjoursbann")
    private long nbrJoursBann ;

    @Column(name = "datedebutbann")
    private Timestamp dateDebutBann ;

    @Column(name = "datefinbann")
    private Timestamp dateFinBann ;

    @Column(name="ispassed")
    private Boolean isCompleted =false ;

    @JsonIgnore
    @ManyToOne
    private Remorqueur remorqueur ;

    @JsonIgnore
    @ManyToOne
    private Consommateur consommateur ;


}
