package com.onegateafrica.Entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Geocode {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String cityName;
    private Float Latitude;
    private Float Longitude;

}
