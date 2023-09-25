package com.siggebig.demo.service;


import com.siggebig.demo.DTO.LoginDto;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("authService")
public class AuthServiceImpl implements AuthService{



    @Autowired
    UserRepository userRepository;



    @Override
    public boolean authenticate(LoginDto loginDto) {

        var username = loginDto.getUsername();
        var password = loginDto.getPassword();

        User auth = userRepository.findByUsername(username);

        return auth != null && auth.getPassword().equals(password);
    }


    @Override
    public User findUser(LoginDto loginDto) {

        if(authenticate(loginDto)) {
            return userRepository.findByUsername(loginDto.getUsername());
        } else {
            return null;
        }
    }
}
