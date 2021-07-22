package com.tavares.messenger.api.impl;

import com.tavares.messenger.api.ApiConstants;
import com.tavares.messenger.api.UserController;
import com.tavares.messenger.exception.UserException;
import com.tavares.messenger.model.dto.Response;
import com.tavares.messenger.model.dto.UserRequest;
import com.tavares.messenger.model.entity.User;
import com.tavares.messenger.service.bo.MessageService;
import com.tavares.messenger.service.bo.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/user/v1")
public class UserControllerV1 implements UserController {
    private final UserService userService;
    private final MessageService messageService;

    @Autowired
    public UserControllerV1(UserService userService, MessageService messageService) {
        this.userService = userService;
        this.messageService = messageService;
    }

    @Override
    public ResponseEntity<Response<User>> createUser(UserRequest request) {
        Response<User> response = new Response<>();
        response.setCode(ApiConstants.ERROR);

        if (ObjectUtils.isEmpty(request.getName().trim()) || ObjectUtils.isEmpty(request.getUsername().trim())) {
            response.setMessage("Either name or username haven't been informed. Please check request.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            response.setData(userService.createUser(request));
            response.setCode(ApiConstants.COMPLETED);
            response.setMessage(String.format(ApiConstants.CREATED_MESSAGE, "User"));
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserException e) {
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<Response<User>> getUser(String user) {
        Response<User> response = new Response<>();

        try {
            response.setData(userService.findUser(user));
            response.setCode(ApiConstants.COMPLETED);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserException e) {
            response.setCode(ApiConstants.ERROR);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Response<List<User>>> listUsers() {
        Response<List<User>> response = new Response<>();
        response.setCode(ApiConstants.COMPLETED);
        response.setData(userService.getUsers());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Response<List<User>>> listUsersMessagingUser(String id) {
        Response<List<User>> response = new Response<>();
        response.setCode(ApiConstants.COMPLETED);

        try {
            var user = userService.findUser(id);
            Set<User> usersWithCommunications = new HashSet<>();

            // using new for solving Lazy relationship
            messageService.getMessagesSentByUser(user).forEach(message -> usersWithCommunications.add(new User(message.getReceiver())));
            messageService.getMessagesReceivedByUser(user).forEach(message -> usersWithCommunications.add(new User(message.getSender())));

            response.setData(new ArrayList<>(usersWithCommunications));
            response.setMessage(String.format("Found %s users communicating with user %s", usersWithCommunications.size(), user.getUsername()));

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserException e) {
            response.setMessage(ApiConstants.ERROR);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
