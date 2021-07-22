package com.tavares.messenger.service.dao;

import com.tavares.messenger.model.entity.Message;
import com.tavares.messenger.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageDao extends JpaRepository<Message, String> {

    List<Message> findBySenderAndReceiverOrderByTimeStampDesc(User sender, User receiver);

    List<Message> findBySender(User sender);

    List<Message> findByReceiver(User receiver);
}
