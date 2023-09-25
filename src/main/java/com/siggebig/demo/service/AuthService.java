package com.siggebig.demo.service;

import com.siggebig.demo.DTO.LoginDto;
import com.siggebig.demo.models.User;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    boolean authenticate(LoginDto loginDto);

    User findUser(LoginDto loginDto);

}
