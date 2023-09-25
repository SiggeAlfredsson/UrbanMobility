package com.siggebig.demo.controllers;

import com.siggebig.demo.models.User;
import com.siggebig.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers () {
        //this returns passwords to?...

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

    @GetMapping("{id}")
    public ResponseEntity<Optional<User>> getUserById (@PathVariable("id") long userId) {
        Optional<User> user = userService.findById(userId);

        if (user.isEmpty()) {
            return ResponseEntity.status(204).header("x-info", "No user with that id").build();
        } else {
            return ResponseEntity.ok(user);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") long userId) {
        //Add ADMIN check or token auth?
        userService.deleteUserById(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") long userId,@RequestBody User updatedUser) {
        //Add ADMIN check or token auth?

        if(!userService.existsById(userId)){
            return ResponseEntity.badRequest().header("x-info", "No user with that id").build();
        }

        userService.updateUserById(userId,updatedUser);
        return ResponseEntity.ok().body(updatedUser);

    }

}
