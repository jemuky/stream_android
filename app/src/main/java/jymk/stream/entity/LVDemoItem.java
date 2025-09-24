package jymk.stream.entity;

public class LVDemoItem {
    public String left;
    public String right;

    public LVDemoItem(String left, String right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "LVDemo{" +
                "left='" + left + '\'' +
                ", right='" + right + '\'' +
                '}';
    }
}
