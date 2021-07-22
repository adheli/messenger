package com.tavares.messenger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tavares.messenger.model.dto.UserRequest;
import com.tavares.messenger.model.entity.User;
import com.tavares.messenger.service.bo.MessageService;
import com.tavares.messenger.service.dao.UserDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TestUserApi {
    private static final String USER_V1_URL = "/user/v1";

    @Autowired
    private MockMvc mock;

    @Autowired
    private UserDao dao;

    @Autowired
    private MessageService msgService;

    @AfterEach
    void cleanUp() {
        msgService.deleteAllMessages();
        dao.deleteAll();
    }

    @Test
    void testCreateUser() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("adheli");
        request.setName("Adheli Tavares");

        RequestBuilder post = post(USER_V1_URL)
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mock.perform(post)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.name").value(request.getName()));
    }

    @Test
    void testCreateUser_UserExists() throws Exception {
        UserRequest request = new UserRequest();
        request.setUsername("adheliExists");
        request.setName("Adheli Tavares");

        User mockUser = new User(request);
        mockUser.setUserId(UUID.randomUUID().toString());
        dao.saveAndFlush(mockUser);

        RequestBuilder post = post(USER_V1_URL)
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        String expectedMessage = String.format("User with username %s already exists!", request.getUsername());
        mock.perform(post)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    @Test
    void testGetUser() throws Exception {
        User mockUser = mockUser("Adheli Tavares");

        RequestBuilder get = get(USER_V1_URL + "/" + mockUser.getUserId())
                .contentType(MediaType.APPLICATION_JSON);

        mock.perform(get)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.name").value("Adheli Tavares"));
    }

    @Test
    void testGetUser_DoesNotExists() throws Exception {
        RequestBuilder get = get(USER_V1_URL + "/abbcc-123")
                .contentType(MediaType.APPLICATION_JSON);

        mock.perform(get)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("No user found with reference abbcc-123"));
    }

    @Test
    void testGetUsers() throws Exception {
        mockUser("Adheli Tavares");
        mockUser("Matt Bellamy");

        RequestBuilder get = get(USER_V1_URL)
                .contentType(MediaType.APPLICATION_JSON);

        mock.perform(get)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    void testGetUsersTexting() throws Exception {
        User mockUser1 = mockUser("Adheli Tavares");
        User mockUser2 = mockUser("Matt Bellamy");
        User mockUser3 = mockUser("Dom Howard");

        mockTexting(mockUser1, mockUser2);
        mockTexting(mockUser3, mockUser1);

        RequestBuilder get = get(USER_V1_URL + "/talkedto/" + mockUser1.getUsername())
                .contentType(MediaType.APPLICATION_JSON);

        mock.perform(get)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Found 2 users communicating with user adhelitavares"))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    private User mockUser(String name) {
        User mockUser = new User();
        mockUser.setUsername(name.replace(" ", "").toLowerCase());
        mockUser.setName(name);
        mockUser.setUserId(UUID.randomUUID().toString());
        return dao.saveAndFlush(mockUser);
    }

    private void mockTexting(User sender, User receiver) {
        msgService.createMessage("testing", sender, receiver);
    }
}
