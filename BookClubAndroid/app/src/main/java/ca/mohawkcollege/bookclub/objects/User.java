package ca.mohawkcollege.bookclub.objects;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * User
 */
public class User implements Serializable {
    public String userId;
    public String phoneNumber;
    public String email;
    public String token;
    public String name;
    public String imageUrl;

    /**
     * Default empty user constructor
     */
    public User() {}

    /**
     * Constructor to set information for user without an image
     * @param userId - id of user
     * @param phoneNumber - phone number of user
     * @param email - email of user
     * @param token - user token
     */
    public User(String userId, String phoneNumber, String email, String token) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.token = token;
    }

    /**
     * Constructor to set information for user with an image and name
     * @param userId - id of user
     * @param phoneNumber - phone number of user
     * @param email - email of user
     * @param token - user token
     * @param name - name of user
     * @param imageUrl - profile picture of user
     */
    public User(String userId, String phoneNumber, String email, String token, String name, String imageUrl) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.token = token;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the name of the user
     * @return name or phone number of user if their name is not set
     */
    public String getName() {
        if (name != null && !TextUtils.isEmpty(name)) {
            return name;
        }

        return phoneNumber;
    }
}
