package com.tavares.messenger.service.dao;

import com.tavares.messenger.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, String> {
    User findByUsername(String username);
}
