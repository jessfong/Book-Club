package ca.mohawkcollege.bookclub.objects;

import java.io.Serializable;

public class User implements Serializable {
    public String userId;
    public String phoneNumber;
    public String email;
    public String token;
    public String name;
    public String imageUrl;

    public User() {
    }

    public User(String userId, String phoneNumber, String email, String token) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.token = token;
    }

    public User(String userId, String phoneNumber, String email, String token, String name, String imageUrl) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.token = token;
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
