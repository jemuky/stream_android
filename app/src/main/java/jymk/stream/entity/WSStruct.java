package jymk.stream.entity;

import java.util.UUID;

public class WSStruct<T> {
    public String event;
    public String ref;
    public String topic;
    public T payload;

    public WSStruct(String event, String topic, T payload) {
        this.event = event;
        this.topic = topic;
        this.ref = UUID.randomUUID().toString();
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "WSStruct{" +
                "event='" + event + '\'' +
                ", ref='" + ref + '\'' +
                ", topic='" + topic + '\'' +
                ", payload=" + payload +
                '}';
    }
}
