package com.onegateafrica.Controllers;

import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.OTPSystem;
import com.onegateafrica.Payloads.response.JwtResponse;
import com.onegateafrica.Security.jwt.JwtUtils;
import com.onegateafrica.Service.ConsommateurService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/OTP")
@CrossOrigin(origins = "*")
public class OTPSystemController {

    private static Map<String, OTPSystem> otpData = new HashMap<>();
    private static final String ACCOUNT_SID = "AC3693efe44a8c5cf980d56cfc42ca0c5d";
    private static final String AUTH_ID = "78e44371dc1a458e7a54ffc1d7910c83";
    private static final String PHONE_NUMBER= "+18506053932";
    private final ConsommateurService consommateurService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Autowired
    public OTPSystemController(ConsommateurService consommateurService, AuthenticationManager authenticationManager,
                               JwtUtils jwtUtils) {
        this.consommateurService = consommateurService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    static {
        Twilio.init(ACCOUNT_SID, AUTH_ID);
    }

    @PostMapping("/sendOTP")
    public ResponseEntity<String>  sendOTP(@RequestParam("phoneNumberInput") String phoneNumberInput) {

        if (phoneNumberInput != null) {
            Consommateur consommateur = consommateurService.getConsommateurByPhoneNumber(phoneNumberInput);
            if (consommateur == null) {
                OTPSystem otpSystem = new OTPSystem();
                otpSystem.setPhoneNumber(phoneNumberInput);
                otpSystem.setOtp(String.valueOf(((int) (Math.random() * (10000 - 1000))) + 1000));
                otpSystem.setExpireTime(System.currentTimeMillis() + 60000);
                otpData.put(phoneNumberInput, otpSystem);
                Message.creator(new PhoneNumber("+216" + phoneNumberInput), new PhoneNumber(PHONE_NUMBER),
                        "Your OTP is: " + otpSystem.getOtp()).create();
                return ResponseEntity.status(HttpStatus.OK).body("OTP Sent");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("déjà inscrit");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter a valid phone number");
        }
    }

    @PostMapping("verifyOTP")
    ResponseEntity<String> verifyOtp(@RequestBody OTPSystem requestBodyOTPSystem) {
        System.out.println(requestBodyOTPSystem.getOtp());
        System.out.println(requestBodyOTPSystem.getPhoneNumber());
        if (requestBodyOTPSystem == null || requestBodyOTPSystem.getOtp() == null
                || requestBodyOTPSystem.getPhoneNumber() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(" provided not enough information");
        }
        if (otpData.containsKey(requestBodyOTPSystem.getPhoneNumber())) {
            OTPSystem otpSystem = otpData.get(requestBodyOTPSystem.getPhoneNumber());
            if (otpSystem.getExpireTime() >= System.currentTimeMillis() && otpSystem.getOtp().equals(requestBodyOTPSystem.getOtp())) {
                return ResponseEntity.status(HttpStatus.OK).body("Verified");
            }
            else if(!(otpSystem.getExpireTime() >= System.currentTimeMillis())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expired Code");
            }
            else if(!otpSystem.getOtp().equals(requestBodyOTPSystem.getOtp())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Code");
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
    }
    @PostMapping("/sendOTPSignin")
    public ResponseEntity<String> sendOTPSignIn(@RequestParam("phoneNumber") String phoneNumber) {
        if (phoneNumber != null) {
            Consommateur consommateur = consommateurService.getConsommateurByPhoneNumber(phoneNumber);
            if (consommateur != null) {
                OTPSystem otpSystem = new OTPSystem();
                otpSystem.setPhoneNumber(phoneNumber);
                otpSystem.setOtp(String.valueOf(((int) (Math.random() * (10000 - 1000))) + 1000));
                otpSystem.setExpireTime(System.currentTimeMillis() + 60000);
                otpData.put(phoneNumber, otpSystem);
                Message.creator(new PhoneNumber("+216" + phoneNumber), new PhoneNumber(PHONE_NUMBER),
                        "Your OTP is: " + otpSystem.getOtp()).create();
                return ResponseEntity.status(HttpStatus.OK).body("OTP sent");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Consumer not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("phone number must be provided");
        }
    }
  @PostMapping("verifyOTPSignIn")
  public ResponseEntity<?> verifyOtpSignIn (@RequestBody OTPSystem requestBodyOTPSystem) {

      System.out.println(requestBodyOTPSystem.getOtp());
      System.out.println(requestBodyOTPSystem.getPhoneNumber());
      if (requestBodyOTPSystem == null || requestBodyOTPSystem.getOtp() == null
              || requestBodyOTPSystem.getPhoneNumber() == null) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("not enough provided information");
      } else if (otpData.containsKey(requestBodyOTPSystem.getPhoneNumber())) {
          OTPSystem otpSystem = otpData.get(requestBodyOTPSystem.getPhoneNumber());
          System.out.println(otpSystem.getPhoneNumber()+" "+otpSystem.getOtp()+" "+ otpSystem.getExpireTime());
          System.out.println(System.currentTimeMillis());
          System.out.println(otpSystem.getExpireTime() >= System.currentTimeMillis());
          System.out.print(otpSystem.getOtp() == requestBodyOTPSystem.getOtp());
          if (otpSystem.getExpireTime() >= System.currentTimeMillis() && otpSystem.getOtp().equals(requestBodyOTPSystem.getOtp())) {
              Consommateur consommateur = consommateurService.getConsommateurByPhoneNumber(requestBodyOTPSystem.getPhoneNumber());

              List<String> roles =consommateur.getRoles().stream()
                      .map(item -> item.getRoleName().toString())
                      .collect(Collectors.toList());
              String jwt = jwtUtils.generateJwtToken(consommateur.getEmail(), consommateur.getRoles());
              return ResponseEntity.ok(new JwtResponse(jwt,consommateur.getId(),
                      consommateur.getUserName(),
                      consommateur.getEmail(),
                      roles,
                      consommateur.getPhoneNumber(),
                      consommateur.getFirstName(),
                      consommateur.getLastName()));
          }
      }
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
  }

    public static Map<String, OTPSystem> getOtpData() {
        return otpData;
    }
}
