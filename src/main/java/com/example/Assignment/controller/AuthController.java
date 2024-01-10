package com.example.Assignment.controller;

import com.example.Assignment.jwt.JwtHelper;
import com.example.Assignment.models.JwtRequest;
import com.example.Assignment.models.JwtResponse;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AuthenticationManager manager;


    @Autowired
    private JwtHelper helper;

    private Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping("/login")
    public JwtResponse login(@RequestBody JwtRequest request) {
        UsernamePasswordAuthenticationToken passwordAuthenticationToken=new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = manager.authenticate(passwordAuthenticationToken);
        if(authentication.isAuthenticated()){
            this.doAuthenticate(request.getEmail(), request.getPassword());
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            String token = this.helper.generateToken(userDetails);
            JwtResponse response = JwtResponse.builder()
                    .jwtToken(token)
                    .username(userDetails.getUsername()).build();
            SecurityContextHolder.getContext().setAuthentication(passwordAuthenticationToken);
            System.out.println("Abhi aaya hu auth controller pe");
            return response;
        }
        else{
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    @GetMapping("/refreshtoken")
    public ResponseEntity<JwtResponse> refreshtoken(@RequestBody HttpServletRequest jwtRequest){
        Claims claims= (Claims) jwtRequest.getAttribute("claims");
        Map<String,Object> map=getMap(claims);
        String token=this.helper.refreshToken(map,map.get("sub").toString());
        logger.info("Inside Auth Controller Did Something");
        return ResponseEntity.ok(new JwtResponse(token,"Something"));
    }

    private Map<String, Object> getMap(Claims claims) {
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }

    private void doAuthenticate(String email, String password) {

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, password);
        try {
            manager.authenticate(authentication);


        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Invalid Username or Password  !!");
        }

    }

    @ExceptionHandler(BadCredentialsException.class)
    public String exceptionHandler() {
        return "Credentials Invalid !!";
    }

}

