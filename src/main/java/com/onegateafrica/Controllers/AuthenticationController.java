package com.onegateafrica.Controllers;

import com.onegateafrica.Entities.Consommateur;
import com.onegateafrica.Entities.ERole;
import com.onegateafrica.Entities.OTPSystem;
import com.onegateafrica.Entities.Role;
import com.onegateafrica.Payloads.request.LoginForm;
import com.onegateafrica.Payloads.request.SignUpForm;
import com.onegateafrica.Payloads.request.tokenForm;
import com.onegateafrica.Payloads.response.JwtResponse;
import com.onegateafrica.Repositories.RoleRepository;
import com.onegateafrica.Security.jwt.JwtUtils;
import com.onegateafrica.Service.ConsommateurService;
import com.onegateafrica.ServiceImpl.UserDetailsImpl;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final ConsommateurService consommateurService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;


    @Autowired
    public AuthenticationController(ConsommateurService consommateurService, AuthenticationManager authenticationManager,
                                    BCryptPasswordEncoder bCryptPasswordEncoder, JwtUtils jwtUtils,
                                    RoleRepository roleRepository) {
        this.consommateurService = consommateurService;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtUtils = jwtUtils;
        this.roleRepository = roleRepository;
    }


    @PostMapping("/signin")
    public ResponseEntity<?> AuthenticatedUserRealm(@RequestBody LoginForm loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("some of the fields are null");
        }
        Optional<Consommateur> consommateur = consommateurService.getConsommateurByEmail(loginRequest.getEmail());
        if (consommateur.isPresent()) {
            if (bCryptPasswordEncoder.matches(loginRequest.getPassword(), consommateur.get().getPassword())) {

                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);
                UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

                List<String> roles = userDetails.getAuthorities().stream()
                        .map(item -> item.getAuthority())
                        .collect(Collectors.toList());
                return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        roles,
                        userDetails.getPhoneNumber(),
                        userDetails.getFirstName(),
                        userDetails.getLastName()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Check email or password");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NOT FOUND");
        }
    }

    @PostMapping("/signinGoogle")
    public ResponseEntity<?> AthenticateWithGoogle(@RequestBody tokenForm form) {
        if (form.getToken() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("null token");
        }
        String token = form.getToken();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.googleapis.com/userinfo/v2/me"))
                .timeout(Duration.ofMinutes(1))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> response =
                null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (response.statusCode() == 200) {
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject jsonObject = (JSONObject) jsonParser.parse(response.body());
                String email = (String) jsonObject.get("email");
                Optional<Consommateur> consommateurO = consommateurService.getConsommateurByEmail(email);
                if (consommateurO.isPresent()) {
                    Consommateur consommateur = consommateurO.get();
                    List<String> roles = consommateur.getRoles().stream()
                            .map(item -> item.getRoleName().toString())
                            .collect(Collectors.toList());
                    String jwt = jwtUtils.generateJwtToken(consommateur.getEmail(), consommateur.getRoles());
                    return ResponseEntity.ok(new JwtResponse(jwt, consommateur.getId(),
                            consommateur.getUserName(),
                            consommateur.getEmail(),
                            roles,
                            consommateur.getPhoneNumber(),
                            consommateur.getFirstName(),
                            consommateur.getLastName()));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.body());
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        System.out.println("'error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.body());
    }

    @PostMapping("/signinFacebook")
    public ResponseEntity<?> AthenticateWithFacebook(@RequestBody tokenForm form) {
        if (form.getToken() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("null token");
        }
        String token = form.getToken();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://graph.facebook.com/me?fields=id,first_name,last_name,email&access_token=" + token))
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json ")
                .GET()
                .build();
        HttpResponse<String> response =
                null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (response.statusCode() == 200) {
            JSONParser jsonParser = new JSONParser();
            try {
                JSONObject jsonObject = (JSONObject) jsonParser.parse(response.body());
                String email = (String) jsonObject.get("email");
                System.out.println(email);
                Optional<Consommateur> consommateurO = consommateurService.getConsommateurByEmail(email);
                if (consommateurO.isPresent()) {
                    Consommateur consommateur = consommateurO.get();
                    List<String> roles = consommateur.getRoles().stream()
                            .map(item -> item.getRoleName().toString())
                            .collect(Collectors.toList());
                    String jwt = jwtUtils.generateJwtToken(consommateur.getEmail(), consommateur.getRoles());
                    return ResponseEntity.ok(new JwtResponse(jwt, consommateur.getId(),
                            consommateur.getUserName(),
                            consommateur.getEmail(),
                            roles,
                            consommateur.getPhoneNumber(),
                            consommateur.getFirstName(),
                            consommateur.getLastName()));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.body());
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        System.out.println("'error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.body());
    }

    @PostMapping("/signupConsommateur")
    public ResponseEntity<?> registerClient(@RequestBody SignUpForm body) {
        System.out.println(body.toString());
        if (body.getEmail() == null || body.getPassword() == null
                || body.getLastName() == null || body.getFirstName() == null || body.getPhoneNumber() == null || body.getOtp() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("some of the mandatory fields is null");
        }
        if (consommateurService.getConsommateurByPhoneNumber(body.getPhoneNumber()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Phone number already exists.");
        }
        if (consommateurService.getConsommateurByEmail(body.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already exists.");
        }
        if (OTPSystemController.getOtpData().containsKey(body.getPhoneNumber())) {
            OTPSystem otpSystem = OTPSystemController.getOtpData().get(body.getPhoneNumber());
            if(!(otpSystem.getExpireTime() >= System.currentTimeMillis())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Expired Code");
            }
            else if(!otpSystem.getOtp().equals(body.getOtp())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Code");
            }
        }
        Consommateur consommateur = new Consommateur();
        consommateur.setEmail(body.getEmail());
        consommateur.setFirstName(body.getFirstName());
        consommateur.setLastName(body.getLastName());
        consommateur.setUserName(body.getFirstName() + body.getLastName());
        consommateur.setPassword(bCryptPasswordEncoder.encode(body.getPassword()));
        consommateur.setPhoneNumber(body.getPhoneNumber());
        Set<Role> roles = new HashSet<>();
        Optional<Role> role = roleRepository.findByRoleName(ERole.ROLE_CONSOMMATEUR);
        if (role.get() != null)
            roles.add(role.get());
        else
            throw new RuntimeException("role not found");
        consommateur.setRoles(roles);
        consommateur.setUserPicture("DEFAULT");
        consommateur = consommateurService.saveOrUpdateConsommateur(consommateur);
        List<String> listRoles = consommateur.getRoles().stream()
                .map(item -> item.getRoleName().toString())
                .collect(Collectors.toList());
        String jwt = jwtUtils.generateJwtToken(consommateur.getEmail(), consommateur.getRoles());
        return ResponseEntity.ok(new JwtResponse(jwt, consommateur.getId(),
                consommateur.getUserName(),
                consommateur.getEmail(),
                listRoles,
                consommateur.getPhoneNumber(),
                consommateur.getFirstName(),
                consommateur.getLastName()));
    }
    @GetMapping("/isConsommateurByEmail/{email}")
    public ResponseEntity<String> verifyConsommateur(@PathVariable(name="email") String email) {
        if(email == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not enough information provided");
        }
        Optional <Consommateur> consommateur =consommateurService.getConsommateurByEmail(email);
        if(consommateur.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body("Is consumer");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not registred");
    }
}
