package ca.mohawkcollege.bookclub.objects;

public class Attending {
    public String meetingId;
    public String attendingUserId;

    public Attending(){}

    public Attending(String meetingId, String attendingUserId) {
        this.meetingId = meetingId;
        this.attendingUserId = attendingUserId;
    }
}
