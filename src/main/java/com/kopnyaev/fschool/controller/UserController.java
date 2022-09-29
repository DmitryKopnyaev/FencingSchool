package com.kopnyaev.fschool.controller;

import com.kopnyaev.fschool.model.User;
import com.kopnyaev.fschool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping(produces = "application/json")
    public ResponseEntity<User> authentification(@RequestParam String login, @RequestParam String password) {
        User user = this.userRepository.getByLoginAndPassword(login, password);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<User> getUserById(@RequestParam long id) {
        User user = this.userRepository.findById(id).orElse(null);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        else return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping(produces = "application/json")
    public ResponseEntity<User> deleteUserById(@RequestParam long id) {
        User user = this.userRepository.findById(id).orElse(null);
        if (user == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        else {
            this.userRepository.deleteById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }
    }
}
