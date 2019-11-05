package ca.mohawkcollege.bookclub.objects;

import java.io.Serializable;

public class Meeting implements Serializable {
    public String meetingId;
    public String bookClubId;
    public String location;
    public String date;
    public String startTime;
    public String endTime;
    public String bookTitle;
    public String bookAuthor;
    public String bookThumb;
    public double latitude;
    public double longitude;

    public Meeting() {
    }

    public Meeting(String meetingId, String bookClubId, String location, String date, String startTime, String endTime, String bookTitle, String bookAuthor, String bookThumb, double latitude, double longitude) {
        this.meetingId = meetingId;
        this.bookClubId = bookClubId;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookThumb = bookThumb;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
