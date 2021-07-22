package com.tavares.messenger.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    private String textId;

    @Column
    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "sender_id", referencedColumnName = "user_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "receiver_id", referencedColumnName = "user_id")
    private User receiver;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamp;

    @Column
    private boolean liked;

    public Message(Message copyFrom) {
        this.textId = copyFrom.getTextId();
        this.content = copyFrom.getContent();
        this.timeStamp = copyFrom.getTimeStamp();
        this.liked = copyFrom.isLiked();
        this.receiver = new User(copyFrom.getReceiver());
        this.sender = new User(copyFrom.getSender());
    }

    @Override
    public String toString() {
        return "Message{" +
                "textId='" + textId + '\'' +
                ", messageContent='" + content + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", timeStamp=" + timeStamp +
                ", liked=" + liked +
                '}';
    }
}
