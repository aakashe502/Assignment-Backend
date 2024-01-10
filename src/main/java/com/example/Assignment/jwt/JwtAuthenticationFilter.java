package com.example.Assignment.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);
    @Autowired
    private JwtHelper jwtHelper;


    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal( HttpServletRequest request,  HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Authorization

        String requestHeader = request.getHeader("Authorization");
        //Bearer 2352345235sdfrsfgsdfsdf
        logger.info(" Header :  {}", requestHeader);
        String username = null;
        String token = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            //looking good
            token = requestHeader.substring(7);
            logger.info("token is:{} ",token);

           // logger.info("username is token is upar: {}",this.jwtHelper.getUsernameFromToken((token)));
            try {
//                if(SecurityContextHolder.getContext().getAuthentication()==null){
//
//                    allowForRefreshToken(request);
//                    logger.info("Ha Hua Refresh ,{}",SecurityContextHolder.getContext().getAuthentication());
//                }
//                else{
                    username = this.jwtHelper.getUsernameFromToken(token);
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    logger.info("User name is {}",username);

                //}


            }
            catch (ExpiredJwtException e) {
                if(requestHeader.contains("refreshtoken")){
                    allowForRefreshToken(request);
                }
                System.out.println("Given jwt token is expired !!");
                request.setAttribute("expired",e.getMessage());

            }
            catch (Exception e) {
                //e.printStackTrace();
                logger.info("Not Generatig Token :{} ",token);


            }


        } else {
            System.out.println("Invalid Header Value !! ");
        }



        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {


            //fetch user detail from username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
            if (validateToken) {

                //set the authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);


            } else {

                System.out.println("Validation fails !!");
            }
            System.out.println("filter par hain");

        }
        else{
            //Here I can code for refresh tokken
            logger.info("Here it is failing{}",token);
        }

        filterChain.doFilter(request, response);


    }

    private void allowForRefreshToken( HttpServletRequest request) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new UsernamePasswordAuthenticationToken(null,null,null);
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        logger.info("Haa refresh ho rha h{}");
//        request.setAttribute("claims",e.getClaims());
    }
}