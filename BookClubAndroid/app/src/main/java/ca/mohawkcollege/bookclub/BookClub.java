package ca.mohawkcollege.bookclub;

import java.io.Serializable;

public class BookClub implements Serializable {
    public String name;
    public String userId;
    public String imageUrl;
    public String recordId;

    public BookClub(){}

    public BookClub(String userId, String name, String imageUrl, String recordId){
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
        this.recordId = recordId;
    }
}
