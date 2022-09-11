package com.onegateafrica.Payloads.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VerificationChangementRemorqeurResponse {
    private boolean isChangingPermitted ;
    private String finDuree ;
}
