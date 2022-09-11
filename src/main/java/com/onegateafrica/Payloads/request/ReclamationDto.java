package com.onegateafrica.Payloads.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReclamationDto {
    private Long idRemorqeur ;
    private String description ;
    private String typeReclamation ;
}
