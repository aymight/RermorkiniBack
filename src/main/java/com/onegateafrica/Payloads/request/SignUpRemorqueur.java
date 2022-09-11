package com.onegateafrica.Payloads.request;

import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.RemorqeurType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRemorqueur {

    private String cinNumber;
    private String cinPhoto;
    private String raisonSociale;
    private String matriculeRemorquage;
    private String patentePhoto;
    private String phoneNumber;
}
