package com.tavares.messenger.service.bo;

import com.tavares.messenger.exception.MessageException;
import com.tavares.messenger.model.entity.Message;
import com.tavares.messenger.model.dto.MessageRequest;
import com.tavares.messenger.model.entity.User;
import com.tavares.messenger.service.dao.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
    private final MessageDao dao;

    @Autowired
    public MessageService(MessageDao dao) {
        this.dao = dao;
    }

    public Message createMessage(String messageContent, User sender, User receiver) {
        Message message = new Message();
        message.setTextId(UUID.randomUUID().toString());
        message.setContent(messageContent);
        message.setReceiver(receiver);
        message.setSender(sender);
        message.setTimeStamp(Date.from(Instant.now()));
        return dao.saveAndFlush(message);
    }

    public Message editMessage(String msgId, MessageRequest request) throws MessageException {
        Message message = dao.findById(msgId)
                .orElseThrow(() -> new MessageException(String.format("Couldn't find message with reference ID %s", msgId)));
        message.setContent(request.getMessage());
        return dao.saveAndFlush(message);
    }

    public Message giveLike(String msgId) throws MessageException {
        Message message = dao.findById(msgId)
                .orElseThrow(() -> new MessageException(String.format("Couldn't find message with reference ID %s", msgId)));
        message.setLiked(true);
        return dao.saveAndFlush(message);
    }

    public List<Message> getConversation(User sender, User receiver) {
        return dao.findBySenderAndReceiverOrderByTimeStampDesc(sender, receiver);
    }

    public List<Message> getMessagesSentByUser(User sender) {
        return dao.findBySender(sender);
    }

    public List<Message> getMessagesReceivedByUser(User receiver) {
        return dao.findByReceiver(receiver);
    }

    public void deleteAllMessages() {
        dao.deleteAll();
    }
}
