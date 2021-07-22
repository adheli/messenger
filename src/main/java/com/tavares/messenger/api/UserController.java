package com.tavares.messenger.api;

import com.tavares.messenger.model.dto.Response;
import com.tavares.messenger.model.entity.User;
import com.tavares.messenger.model.dto.UserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Set;

@RequestMapping("/user")
public interface UserController {

    @PostMapping
    ResponseEntity<Response<User>> createUser(@RequestBody UserRequest request);

    @GetMapping("/{user}")
    ResponseEntity<Response<User>> getUser(@PathVariable String user);

    @GetMapping
    ResponseEntity<Response<List<User>>> listUsers();

    @GetMapping("/talkedto/{id}")
    ResponseEntity<Response<List<User>>> listUsersMessagingUser(@PathVariable String id);
}
