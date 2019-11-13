package ca.mohawkcollege.bookclub.objects;

import java.io.Serializable;

/**
 * Invite
 */
public class Invite implements Serializable {
    public String userId;
    public String bookClubId;

    /**
     * Default empty invite constructor
     */
    public Invite() {}

    /**
     * Constructor to set user and book club id for invite
     * @param userId - id of user in invite
     * @param bookClubId - id of book club being invited to
     */
    public Invite(String userId, String bookClubId) {
        this.userId = userId;
        this.bookClubId = bookClubId;
    }
}
