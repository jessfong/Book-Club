package ca.mohawkcollege.bookclub.objects.bookobjects;

import java.util.Objects;

public class Items {
    public String kind;
    public String id;
    public String etag;
    public VolumeInfo volumeInfo;

    @Override
    public String toString() {
        return volumeInfo.title + "\n" + getAuthors();
    }

    public String getAuthors() {
        StringBuilder stringBuilder = new StringBuilder();

        if(volumeInfo.authors == null){
            return "No authors";
        }

        for (String author: volumeInfo.authors) {
            stringBuilder.append(author).append(", ");
        }

        String authors = stringBuilder.toString();
        return authors.substring(0, authors.length() - 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Items items = (Items) o;
        return Objects.equals(kind, items.kind) &&
                Objects.equals(id, items.id) &&
                Objects.equals(etag, items.etag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, id, etag);
    }
}
