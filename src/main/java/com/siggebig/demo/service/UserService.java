package com.siggebig.demo.service;

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

    public User updateUserById(long userId, User newInfo) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User with id"+userId+"does not exist in db");
        }

        User orgUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User with id"+userId+"does not exist in db"));

//        Add checks so username and email is free

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
