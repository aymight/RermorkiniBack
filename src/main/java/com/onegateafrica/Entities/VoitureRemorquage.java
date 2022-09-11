package com.onegateafrica.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@Entity
@Table(name = "voitureremorquage")
public class VoitureRemorquage {
    @Id
    @GeneratedValue
    private Long id ;

    @OneToOne(cascade = CascadeType.ALL)
    private Location position ;

    private Double heading ;

    private String nom ;


}
