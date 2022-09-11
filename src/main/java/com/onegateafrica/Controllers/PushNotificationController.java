package com.onegateafrica.Controllers;

import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Payloads.request.NotificationDto;
import com.onegateafrica.Service.ConsommateurService;
import com.onegateafrica.Service.PushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notification")
@CrossOrigin(origins = "*")
public class PushNotificationController {

    private final ConsommateurService consommateurService;
    private final PushNotificationService pushNotificationService ;

    @Autowired
    public PushNotificationController(ConsommateurService consommateurService, PushNotificationService pushNotificationService) {
        this.consommateurService = consommateurService;
        this.pushNotificationService = pushNotificationService;
    }

    @PostMapping("/envoyerNotification")
    public ResponseEntity<Object> envoyerPushNotification(@RequestBody NotificationDto notificationDto){
        if(notificationDto !=null) {
            try{
                Consommateur consommateur = consommateurService.getConsommateur(notificationDto.getIdRecipient()).get();
                String recipientExpoPushToken = consommateur.getExpoPushToken() ;
                pushNotificationService.ajouterPushNotification(recipientExpoPushToken ,notificationDto.getTitle(),notificationDto.getMessage());
                return ResponseEntity.status(HttpStatus.OK).body("notficaiton envoyée avec succés") ;
            }
            catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("erreur ") ;
            }



        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("vérifier le dto ") ;
    }
}
