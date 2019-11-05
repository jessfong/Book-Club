package ca.mohawkcollege.bookclub.objects;

public class Meeting {
    public String meetingId;
    public String bookClubId;
    public String location;
    public String date;
    public String startTime;
    public String endTime;

    public Meeting(){}

    public Meeting(String meetingId, String bookClubId, String location, String date, String startTime, String endTime) {
        this.meetingId = meetingId;
        this.bookClubId = bookClubId;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
