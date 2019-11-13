package ca.mohawkcollege.bookclub.objects;

/**
 * Attending
 */
public class Attending {
    public String meetingId;
    public String attendingUserId;

    /**
     * Default empty attending constructor
     */
    public Attending() {}

    /**
     * Constructor to set meeting id and attending user id
     * @param meetingId - id of meeting being attended
     * @param attendingUserId - id of attending user
     */
    public Attending(String meetingId, String attendingUserId) {
        this.meetingId = meetingId;
        this.attendingUserId = attendingUserId;
    }
}
