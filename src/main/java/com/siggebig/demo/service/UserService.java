package com.siggebig.demo.service;

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

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

    public Optional<User> findById(long userId) { return userRepository.findById(userId); }

    public User updateUserById(long userId, User user) {
        if (!userRepository.existsById(userId)) {
//            throw error
        }
        user.setId(userId);

        return userRepository.save(user);
    }

}
