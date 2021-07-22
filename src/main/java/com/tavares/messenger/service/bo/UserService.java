package com.tavares.messenger.service.bo;

import com.tavares.messenger.exception.UserException;
import com.tavares.messenger.model.entity.User;
import com.tavares.messenger.model.dto.UserRequest;
import com.tavares.messenger.service.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private final UserDao dao;

    @Autowired
    public UserService(UserDao dao) {
        this.dao = dao;
    }

    public User createUser(UserRequest request) throws UserException {
        if (dao.findByUsername(request.getUsername()) != null) {
            throw new UserException(String.format("User with username %s already exists!", request.getUsername()));
        }
        var user = new User(request);
        user.setUserId(UUID.randomUUID().toString());
        return dao.saveAndFlush(user);
    }

    public List<User> getUsers() {
        return dao.findAll();
    }

    public User getByUsername(String username) {
        return dao.findByUsername(username);
    }

    public User findUser(String user) throws UserException {
        User userObj = dao.findById(user).orElse(getByUsername(user));
        if (userObj == null) {
            throw new UserException(String.format("No user found with reference %s", user));
        }
        return userObj;
    }
}
