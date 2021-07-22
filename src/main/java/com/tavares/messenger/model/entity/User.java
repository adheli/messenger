package com.tavares.messenger.model.entity;

import com.tavares.messenger.model.dto.UserRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column
    private String username;

    public User(UserRequest request) {
        this.name = request.getName();
        this.username = request.getUsername();
    }

    public User(String id) {
        this.userId = id;
    }

    public User(User copyFrom) {
        this.userId = copyFrom.getUserId();
        this.name = copyFrom.getName();
        this.username = copyFrom.getUsername();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
