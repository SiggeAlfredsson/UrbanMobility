package com.siggebig.demo.controllers;

import com.siggebig.demo.Exception.AuthenticationFailedException;
import com.siggebig.demo.Exception.EntityNotFoundException;
import com.siggebig.demo.models.User;
import com.siggebig.demo.repository.UserRepository;
import com.siggebig.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    //create user is in authcontroller

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers () {   //this returns passwords to?...
        List<User> users = userService.getAllUsers();
        if(users.isEmpty()) {
            return ResponseEntity
                    .status(204)
                    .header("x-info", "No users found in db")
                    .build();
        } else {
            return ResponseEntity.ok(users);
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUserById (@PathVariable("id") long userId) {
        Optional<User> user = userService.findById(userId);
        if (user.isEmpty()) {
            return ResponseEntity.status(204).header("x-info", "No user with that id").build();
        } else {
            return ResponseEntity.ok(user);
        }
    }

    //not needed for assignment but fun to do, would finish if more time
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<String> deleteUserWithId(@PathVariable("id") long userId, @RequestHeader("JWTToken") String token) {
//        try {
//            userService.deleteUserByIdAndToken(userId, token);
//            System.out.println("HELLLOOOO");
//
//        } catch (EntityNotFoundException e) {
//            // 404
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//        } catch (AuthenticationFailedException e) {
//            //401
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Auth failed");
//        }
//
//        return ResponseEntity.ok("User deleted successfully");
//
//    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUserWithToken(@RequestHeader("JWTToken") String token) {
        try {
            userService.deleteUserWithToken(token);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        return ResponseEntity.ok("User deleted successfully");
    }
    @PutMapping("/update")
    public ResponseEntity<User> updateUserWithToken(@Valid @RequestBody User updatedUser, BindingResult result, @RequestHeader("JWTToken") String token) {

        if(result.hasErrors()) {
            return ResponseEntity.badRequest().header("x-info", "Invalid data, check inputs").build();        }

        try {
            if(updatedUser==null || token==null) {
                throw new EntityNotFoundException("No new info / bad token");
            } else {
                userService.updateUserWithToken(updatedUser, token);
            }
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().header("x-info", "Invalid data, check new info or token").build(); //would be nice to have one for each
        }
        return ResponseEntity.ok().body(updatedUser);
    }
}
