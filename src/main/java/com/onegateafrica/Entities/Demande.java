package com.onegateafrica.Entities;


import java.util.Date;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Demande {
  @Id
  private String id;
  private Date DateDemande;
  private String description;

}
