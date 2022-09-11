package com.onegateafrica.Payloads.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BannResponse {
    private boolean isBanned ;
    private String dateFinBann ;
    private String dateDebutBann ;
}
