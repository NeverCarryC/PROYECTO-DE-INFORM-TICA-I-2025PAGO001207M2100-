package model;

public class ListItem {
    private final String text;
    private final String imagePath;

    public ListItem(String text, String imagePath) {
        this.text = text;
        this.imagePath = imagePath;
    }

    public String getText() {
        return text;
    }

    public String getImagePath() {
        return imagePath;
    }
}