package com.siggebig.demo.service;

import com.siggebig.demo.Exception.AuthenticationFailedException;
import com.siggebig.demo.Exception.EntityNotFoundException;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired(required = false)
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {

        return userRepository.existsByUsername(username);
    }

    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

    public Optional<User> findById(long userId) { return userRepository.findById(userId); }



    // should it be User oldInfo and do getId from that instead of passing in a id in the URL?
    public User updateUserById(long userId, User newInfo) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id"+userId+"does not exist in db");
        }



        if(!jwtService.authenticateToken("mocktoken")){
            throw new AuthenticationFailedException("Authentication Failed");
        }


        // this exception is never called just so it is not an optional
        User orgUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id"+userId+"does not exist in db"));




        if (newInfo.getUsername()==null){
            newInfo.setUsername(orgUser.getUsername());
        }

        if (newInfo.getPassword()==null){
            newInfo.setPassword(orgUser.getPassword());
        }

        if (newInfo.getEmail()==null){
            newInfo.setEmail(orgUser.getEmail());
        }

        if (newInfo.getPhoneNumber()==null){
            newInfo.setPhoneNumber(orgUser.getPhoneNumber());
        }

        if (newInfo.getPaymentMethod()==null){
            newInfo.setPaymentMethod(orgUser.getPaymentMethod());
        }

        newInfo.setId(orgUser.getId());
        newInfo.setRole(orgUser.getRole());
        newInfo.setBookings(orgUser.getBookings());



        return userRepository.save(newInfo);
    }


}
