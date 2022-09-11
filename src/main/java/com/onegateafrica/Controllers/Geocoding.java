package com.onegateafrica.Controllers;

import com.google.common.collect.Lists;
import com.onegateafrica.Controllers.utils.DataValidationUtils;
import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.Conversation;
import com.onegateafrica.Entities.Message;
import com.onegateafrica.Entities.OTPSystem;
import com.onegateafrica.Payloads.request.UpdateForm;
import com.onegateafrica.Payloads.response.JwtResponse;
import com.onegateafrica.Security.jwt.JwtUtils;
import com.onegateafrica.Service.ConsommateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class Geocoding {
    private static Map<String, OTPSystem> otpData = new HashMap<>();
    private static final String ACCOUNT_SID = "AC3693efe44a8c5cf980d56cfc42ca0c5d";
    private static final String AUTH_ID = "78e44371dc1a458e7a54ffc1d7910c83";
    private static final String PHONE_NUMBER= "+18506053932";
    private final ConsommateurService consommateurService;
    private final JwtUtils jwtUtils;

    @Autowired
    public Geocoding(ConsommateurService consommateurService, JwtUtils jwtUtils) {
        this.consommateurService = consommateurService;
        this.jwtUtils = jwtUtils;
    }
    @GetMapping("/getMessagesById/{id}/{begins}/{ends}")
    public ResponseEntity<?> getMessagesById(@PathVariable(name = "id") Long id,
                                             @PathVariable(name = "begins") Integer begins,
                                             @PathVariable(name = "ends") Integer ends,
                                             @RequestHeader("Authorization") String auth) {
        System.out.println(begins + " " + ends + " " + id);
        if (!(begins != null && ends != null && id != null && begins >= 0 && ends >= begins)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Parameters");
        }
        String email = String.valueOf(jwtUtils.parseJwtToken(auth.substring(7)).getBody().get("sub"));
        Optional<Consommateur> user = consommateurService.getConsommateurByEmail(email);
        if (user.isPresent()) {
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid ID");
    }
    @GetMapping("/setGeocoding/{id}/{begins}/{ends}")
    public ResponseEntity<?> setGeoCoding(@PathVariable(name = "id") Long id,
                                             @PathVariable(name = "begins") Integer begins,
                                             @PathVariable(name = "ends") Integer ends,
                                             @RequestHeader("Authorization") String auth) {
        System.out.println(begins + " " + ends + " " + id);
        if (!(begins != null && ends != null && id != null && begins >= 0 && ends >= begins)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Parameters");
        }
        String email = String.valueOf(jwtUtils.parseJwtToken(auth.substring(7)).getBody().get("sub"));
        Optional<Consommateur> user = consommateurService.getConsommateurByEmail(email);
        if (user.isPresent()) {

        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid ID");
    }
}
