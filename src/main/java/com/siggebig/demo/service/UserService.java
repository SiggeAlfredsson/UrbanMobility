package com.siggebig.demo.service;

import com.siggebig.demo.Exception.AuthenticationFailedException;
import com.siggebig.demo.Exception.EntityNotFoundException;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }



    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }


    // only admins can delete users by id and token
    public void deleteUserByIdAndToken(long userId, String token) {

        String username = jwtService.getUsernameFromToken(token);
        User user = findByUsername(username);

        if(user==null || username==null){
            throw new EntityNotFoundException("invalid token"); // this maybe should be authfailedexc
        }

        if(!existsById(userId)){
            throw new EntityNotFoundException("No user with that Id");
        }

        if(user.getRole().equals("ADMIN")){
            userRepository.deleteById(userId);
        } else {
            throw new AuthenticationFailedException("Only admins can delete users");
        }
    }

    public void deleteUserWithToken(String token) {

        String username = jwtService.getUsernameFromToken(token);
        if(username==null) {
            throw new EntityNotFoundException("no user found");
        } else {
            User user = findByUsername(username);
            userRepository.deleteById(user.getId());
        }
    }

    public Optional<User> findById(long userId) { return userRepository.findById(userId); }



    public User updateUserWithToken(User newInfo, String token) {

        String username = jwtService.getUsernameFromToken(token);


        if(username==null) {
            throw new EntityNotFoundException("No user was found");
        }

            User orgUser = findByUsername(username);

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

            // cant change id
            newInfo.setId(orgUser.getId());

            //cant change role
            newInfo.setRole(orgUser.getRole());

            // should not be able to update bookings here
            newInfo.setBookings(orgUser.getBookings());



            return userRepository.save(newInfo);

        }
    }





