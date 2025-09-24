package jymk.stream.entity;

public class TransformWSResult {
    public String ref;
    public Object payload;
    public String event;
    public String topic;

    public static class Payload{
        public String status;
        public Object response;

        @Override
        public String toString() {
            return "Payload{" +
                    "status='" + status + '\'' +
                    ", response=" + response +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "TransformWSResult{" +
                "ref='" + ref + '\'' +
                ", payload=" + payload +
                ", event='" + event + '\'' +
                ", topic='" + topic + '\'' +
                '}';
    }
}
