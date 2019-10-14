package ca.mohawkcollege.bookclub.objects;

import java.io.Serializable;

public class Invite implements Serializable {
    public String userId;
    public String bookClubId;

    public Invite(){}

    public Invite(String userId, String bookClubId) {
        this.userId = userId;
        this.bookClubId = bookClubId;
    }
}
