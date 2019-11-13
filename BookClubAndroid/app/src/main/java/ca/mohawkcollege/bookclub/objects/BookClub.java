package ca.mohawkcollege.bookclub.objects;

import java.io.Serializable;
import java.util.Objects;

/**
 * Book club
 */
public class BookClub implements Serializable {
    public String name;
    public String clubOwner;
    public String imageUrl;
    public String recordId;

    /**
     * Default empty book club constructor
     */
    public BookClub() {}

    /**
     * Constructor to set set book club information
     * @param userId - id of book club owner
     * @param name - name of book club
     * @param imageUrl - image for book club
     * @param recordId - book club id
     */
    public BookClub(String userId, String name, String imageUrl, String recordId) {
        this.clubOwner = userId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.recordId = recordId;
    }

    /**
     * Overrides method to check if two items are equal
     * @param o - item to check
     * @return if item is equal or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookClub bookClub = (BookClub) o;
        return Objects.equals(name, bookClub.name) &&
                Objects.equals(clubOwner, bookClub.clubOwner) &&
                Objects.equals(imageUrl, bookClub.imageUrl) &&
                Objects.equals(recordId, bookClub.recordId);
    }

    /**
     * Generates hash code for book club
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, clubOwner, imageUrl, recordId);
    }
}
