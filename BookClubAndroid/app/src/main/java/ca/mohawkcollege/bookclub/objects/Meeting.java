package ca.mohawkcollege.bookclub.objects;

public class Meeting {
    public String meetingId;
    public String bookClubId;
    public String location;
    public String date;
    public String startTime;
    public String endTime;
    public String bookTitle;
    public String bookAuthor;
    public String bookThumb;

    public Meeting(){}

    public Meeting(String meetingId, String bookClubId, String location, String date, String startTime, String endTime, String bookTitle, String bookAuthor, String bookThumb) {
        this.meetingId = meetingId;
        this.bookClubId = bookClubId;
        this.location = location;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookThumb = bookThumb;
    }
}
