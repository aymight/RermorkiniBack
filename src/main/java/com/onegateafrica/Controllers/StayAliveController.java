package com.onegateafrica.Controllers;

import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Payloads.request.Coordinates;
import com.onegateafrica.Payloads.request.PushTokenDto;
import com.onegateafrica.Security.jwt.JwtUtils;
import com.onegateafrica.Service.ConsommateurService;
import com.onegateafrica.Service.ConversationService;
import com.onegateafrica.Service.MessageService;
import com.onegateafrica.Service.RemorqueurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
@EnableScheduling
public class StayAliveController {
    private final JwtUtils jwtUtils;
    private final ConsommateurService consommateurService;


    @Autowired
    public StayAliveController(ConsommateurService consommateurService, JwtUtils jwtUtils) {
        this.consommateurService = consommateurService;
        this.jwtUtils = jwtUtils;
    }

    /*@Scheduled(fixedRate = 5000)
    public void kill() {
        System.out.println(consommateurService.kill());
    }*/
    @PostMapping("/keepAlive")
    public ResponseEntity<?> keepAlive(@RequestHeader("Authorization") String auth) {
        String email = String.valueOf(jwtUtils.parseJwtToken(auth.substring(7)).getBody().get("sub"));
        consommateurService.keepAlive(email);
        return ResponseEntity.status(HttpStatus.OK).body("ok");
    }
    @PostMapping("/updateCoordinate")
    public ResponseEntity<?> updateCoordinate(@RequestHeader("Authorization") String auth, @RequestBody Coordinates coordinates) {
        String email = String.valueOf(jwtUtils.parseJwtToken(auth.substring(7)).getBody().get("sub"));
        Optional<Consommateur> user = consommateurService.getConsommateurByEmail(email);
        if(coordinates.getLatitude() == null || coordinates.getLongitude() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("invalid body");
        }
        if(user.isPresent()){
            Consommateur userF = user.get();
            userF.setLongitude(coordinates.getLongitude());
            userF.setLatitude(coordinates.getLatitude());
            userF.setLastActivity(new Date());
            consommateurService.saveOrUpdateConsommateur(userF);
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("something went wrong");
    }
}
