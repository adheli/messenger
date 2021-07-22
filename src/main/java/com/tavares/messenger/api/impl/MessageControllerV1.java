package com.tavares.messenger.api.impl;

import com.tavares.messenger.api.ApiConstants;
import com.tavares.messenger.api.MessageController;
import com.tavares.messenger.exception.MessageException;
import com.tavares.messenger.exception.UserException;
import com.tavares.messenger.model.dto.MessageRequest;
import com.tavares.messenger.model.dto.Response;
import com.tavares.messenger.model.entity.Message;
import com.tavares.messenger.service.bo.MessageService;
import com.tavares.messenger.service.bo.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/message/v1")
@Slf4j
public class MessageControllerV1 implements MessageController {
    private final MessageService messageService;
    private final UserService userService;

    @Autowired
    public MessageControllerV1(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @Override
    public ResponseEntity<Response<Message>> sendMessage(MessageRequest request) {
        Response<Message> response = new Response<>();
        response.setCode(ApiConstants.ERROR);

        if (ObjectUtils.isEmpty(request.getMessage().trim())
                || ObjectUtils.isEmpty(request.getSenderId().trim())
                || ObjectUtils.isEmpty(request.getReceiverId().trim())) {
            response.setMessage("You can't send a message without message, sender or receiver. Please check request.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            var sender = userService.findUser(request.getSenderId());
            var receiver = userService.findUser(request.getReceiverId());
            response.setData(messageService.createMessage(request.getMessage(), sender, receiver));
            response.setCode(ApiConstants.COMPLETED);
            response.setMessage(String.format(ApiConstants.CREATED_MESSAGE, "Message"));

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (UserException e) {
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Response<Message>> editMessage(String id, MessageRequest request) {
        Response<Message> response = new Response<>();
        response.setCode(ApiConstants.ERROR);

        if (ObjectUtils.isEmpty(request.getMessage().trim())) {
            response.setMessage("Nothing to update on message");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        try {
            response.setData(new Message(messageService.editMessage(id, request)));
            response.setCode(ApiConstants.COMPLETED);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (MessageException e) {
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Response<Message>> likeMessage(String id) {
        Response<Message> response = new Response<>();
        try {
            response.setData(new Message(messageService.giveLike(id)));
            response.setCode(ApiConstants.COMPLETED);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (MessageException e) {
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<Response<List<Message>>> getConversation(MessageRequest request) {
        Response<List<Message>> response = new Response<>();
        response.setCode(ApiConstants.ERROR);

        if (ObjectUtils.isEmpty(request.getSenderId().trim()) || ObjectUtils.isEmpty(request.getReceiverId().trim())) {
            response.setMessage("To get a list of conversation, need to inform sender and receiver. Please check request.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            var sender = userService.findUser(request.getSenderId());
            var receiver = userService.findUser(request.getReceiverId());

            List<Message> conversation = new ArrayList<>();

            conversation.addAll(messageService.getConversation(sender, receiver));
            conversation.addAll(messageService.getConversation(receiver, sender));

            conversation = conversation.stream().sorted(Comparator.comparing(Message::getTimeStamp)).collect(Collectors.toList());
            log.info(String.valueOf(conversation));
            response.setData(conversation);

            response.setCode(ApiConstants.COMPLETED);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UserException e) {
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
