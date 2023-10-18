package com.urjalabs.smartcontroller.models;

import java.util.Date;

/**
 * Created by tarun on 12-11-2017.
 * model class for received message
 */

public class Message {
    private final String topic;
    private final String message;
    private final Date timestamp;

    public Message(String topic, String message) {
        this.topic = topic;
        this.message = message;
        this.timestamp = new Date();
    }

    public String getTopic() {
        return topic;
    }

    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "topic='" + topic + '\'' +
                ", message=" + message +
                ", timestamp=" + timestamp +
                '}';
    }
}
