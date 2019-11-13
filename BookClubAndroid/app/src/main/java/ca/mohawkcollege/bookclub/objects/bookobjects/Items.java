package ca.mohawkcollege.bookclub.objects.bookobjects;

import java.util.Objects;

/**
 * Book item
 */
public class Items {
    public String kind;
    public String id;
    public String etag;
    public VolumeInfo volumeInfo;

    /**
     * Overrides method and converts book item to a string
     * @return book item as a string
     */
    @Override
    public String toString() {
        return volumeInfo.title + "\n" + getAuthors();
    }

    /**
     * Gets the authors of a book
     * @return authors of a book item
     */
    public String getAuthors() {
        StringBuilder stringBuilder = new StringBuilder();

        if (volumeInfo.authors == null) {
            return "No authors";
        }

        for (String author : volumeInfo.authors) {
            stringBuilder.append(author).append(", ");
        }

        String authors = stringBuilder.toString();
        return authors.substring(0, authors.length() - 2);
    }

    /**
     * Overrides method and checks if book item is equal
     * @param o - book item hash to check
     * @return if book items are equal or not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Items items = (Items) o;
        return Objects.equals(kind, items.kind) &&
                Objects.equals(id, items.id) &&
                Objects.equals(etag, items.etag);
    }

    /**
     * Sets hash code for book item
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(kind, id, etag);
    }
}
