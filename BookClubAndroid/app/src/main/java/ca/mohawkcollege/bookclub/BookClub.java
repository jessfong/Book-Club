package ca.mohawkcollege.bookclub;

public class BookClub {
    public String name;
    public String userId;
    public String imageUrl;

    public BookClub(){}

    public BookClub(String userId, String name, String imageUrl){
        this.userId = userId;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
