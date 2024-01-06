//package com.example.Assignment.service;
//
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import java.util.logging.Logger;
//
//public class UserDetailsServiceImpl implements UserDetailsService{
//    @Autowired
//    UserRepository userRepository;
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user=userRepository.findByUsername(username);
//        if(user==null){
//            System.out.println("User Not Found");
//        }
//        return UserDetailsImpl.build(user);
//    }
//}
