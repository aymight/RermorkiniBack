package com.onegateafrica.Controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/consommateur")
    @PreAuthorize("hasRole('CONSOMMATEUR')")
    public String welcomeConsommateur(){
        return "welcome consommateur";
    }


}
