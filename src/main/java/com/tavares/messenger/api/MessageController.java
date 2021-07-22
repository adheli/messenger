package com.tavares.messenger.api;

import com.tavares.messenger.model.dto.MessageRequest;
import com.tavares.messenger.model.dto.Response;
import com.tavares.messenger.model.entity.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/message")
public interface MessageController {

    @PostMapping
    ResponseEntity<Response<Message>> sendMessage(@RequestBody MessageRequest request);

    @PutMapping("/{id}")
    ResponseEntity<Response<Message>> editMessage(@PathVariable String id, @RequestBody MessageRequest request);

    @PutMapping("/like/{id}")
    ResponseEntity<Response<Message>> likeMessage(@PathVariable String id);

    @GetMapping("/conversation")
    ResponseEntity<Response<List<Message>>> getConversation(@RequestBody MessageRequest request);
}
