package ca.mohawkcollege.bookclub.objects;

import java.io.Serializable;
import java.util.Objects;

public class BookClub implements Serializable {
    public String name;
    public String clubOwner;
    public String imageUrl;
    public String recordId;

    public BookClub() {
    }

    public BookClub(String userId, String name, String imageUrl, String recordId) {
        this.clubOwner = userId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.recordId = recordId;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(name, clubOwner, imageUrl, recordId);
    }
}
