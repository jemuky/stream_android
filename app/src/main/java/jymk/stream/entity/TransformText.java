package jymk.stream.entity;


public class TransformText {
    public String text;

    public TransformText(String text){
        this.text = text;
    }

    @Override
    public String toString() {
        return "TransformText{" +
                "text='" + text + '\'' +
                '}';
    }
}
