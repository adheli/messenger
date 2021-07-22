package com.tavares.messenger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tavares.messenger.model.dto.MessageRequest;
import com.tavares.messenger.model.entity.Message;
import com.tavares.messenger.model.entity.User;
import com.tavares.messenger.service.bo.MessageService;
import com.tavares.messenger.service.dao.UserDao;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TestMessageApi {
    private static final String MESSAGE_V1_URL = "/message/v1";

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
    void testSendMessage() throws Exception {
        User user1 = mockUser("Matt Bellamy");
        User user2 = mockUser("Norah Jones");

        MessageRequest request = new MessageRequest();
        request.setMessage("hey what's up");
        request.setSenderId(user1.getUserId());
        request.setReceiverId(user2.getUserId());

        RequestBuilder post = post(MESSAGE_V1_URL)
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mock.perform(post)
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.content").isNotEmpty());
    }

    @Test
    void testSendMessage_UserDoesNotExist() throws Exception {
        User user1 = mockUser("Matt Bellamy");

        MessageRequest request = new MessageRequest();
        request.setMessage("hey what's up???");
        request.setSenderId(user1.getUserId());
        request.setReceiverId("abc-123-defg-4567");

        RequestBuilder post = post(MESSAGE_V1_URL)
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mock.perform(post)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("No user found with reference abc-123-defg-4567"));
    }

    @Test
    void testSendMessage_MessageIsEmpty() throws Exception {
        User user1 = mockUser("Matt Bellamy");
        User user2 = mockUser("Norah Jones");

        MessageRequest request = new MessageRequest();
        request.setMessage(" ");
        request.setSenderId(user1.getUserId());
        request.setReceiverId(user2.getUserId());

        RequestBuilder post = post(MESSAGE_V1_URL)
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mock.perform(post)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("You can't send a message without message, " +
                        "sender or receiver. Please check request."));
    }

    @Test
    void testEditMessage() throws Exception {
        User user1 = mockUser("Matt Bellamy");
        User user2 = mockUser("Norah Jones");
        Message msg = mockTexting(user1, user2);

        MessageRequest request = new MessageRequest();
        request.setMessage("heeeeey whats up");

        Assertions.assertNotEquals(msg.getContent(), request.getMessage());

        RequestBuilder put = put(MESSAGE_V1_URL + "/" + msg.getTextId())
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mock.perform(put)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.content").value("heeeeey whats up"));
    }

    @Test
    void testEditMessage_MessageIsEmpty() throws Exception {
        User user1 = mockUser("Matt Bellamy");
        User user2 = mockUser("Norah Jones");
        Message msg = mockTexting(user1, user2);

        MessageRequest request = new MessageRequest();
        request.setMessage(" ");

        RequestBuilder put = put(MESSAGE_V1_URL + "/" + msg.getTextId())
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mock.perform(put)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(ApiConstants.ERROR))
                .andExpect(jsonPath("$.message").value("Nothing to update on message"));
    }

    @Test
    void testLikeMessage() throws Exception {
        User user1 = mockUser("Matt Bellamy");
        User user2 = mockUser("Norah Jones");
        Message msg = mockTexting(user1, user2);

        Assertions.assertFalse(msg.isLiked());

        RequestBuilder put = put(MESSAGE_V1_URL + "/like/" + msg.getTextId());

        mock.perform(put)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.liked").value(true));
    }

    @Test
    void testGetConversation() throws Exception {
        User user1 = mockUser("Matt Bellamy");
        User user2 = mockUser("Norah Jones");
        msgService.createMessage("hi", user1, user2);
        Awaitility.await().during(Duration.ofSeconds(2));
        msgService.createMessage("hello", user2, user1);
        Awaitility.await().during(Duration.ofSeconds(2));
        msgService.createMessage("whats up", user1, user2);
        Awaitility.await().during(Duration.ofSeconds(2));
        msgService.createMessage("nothing", user2, user1);
        Awaitility.await().during(Duration.ofSeconds(2));
        msgService.createMessage("bye", user2, user1);

        MessageRequest request = new MessageRequest();
        request.setReceiverId(user1.getUserId());
        request.setSenderId(user2.getUserId());

        RequestBuilder get = get(MESSAGE_V1_URL + "/conversation")
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mock.perform(get)
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].content").value("hi"));
    }

    @Test
    void testGetConversation_UserDoesNotExist() throws Exception {
        User user1 = mockUser("Matt Bellamy");

        MessageRequest request = new MessageRequest();
        request.setReceiverId(user1.getUserId());
        request.setSenderId("abc-123-defg-4567");

        RequestBuilder get = get(MESSAGE_V1_URL + "/conversation")
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mock.perform(get)
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("No user found with reference abc-123-defg-4567"));
    }

    @Test
    void testGetConversation_UserIsEmpty() throws Exception {
        User user1 = mockUser("Matt Bellamy");

        MessageRequest request = new MessageRequest();
        request.setReceiverId(user1.getUserId());
        request.setSenderId(" ");

        RequestBuilder get = get(MESSAGE_V1_URL + "/conversation")
                .content(new ObjectMapper().writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON);

        mock.perform(get)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message")
                        .value("To get a list of conversation, need to inform sender and receiver. " +
                                "Please check request."));
    }

    private User mockUser(String name) {
        User mockUser = new User();
        mockUser.setUsername(name.replace(" ", "").toLowerCase());
        mockUser.setName(name);
        mockUser.setUserId(UUID.randomUUID().toString());
        return dao.saveAndFlush(mockUser);
    }

    private Message mockTexting(User sender, User receiver) {
        return msgService.createMessage("testing", sender, receiver);
    }
}
