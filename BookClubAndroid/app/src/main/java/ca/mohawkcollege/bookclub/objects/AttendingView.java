package ca.mohawkcollege.bookclub.objects;

/**
 * Attending view
 */
public class AttendingView {
    public String name;
    public String imageUrl;

    /**
     * Constructor to set attending view name and image url
     * @param name - name of attending view
     * @param imageUrl - url for attending image
     */
    public AttendingView(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
