package ca.mohawkcollege.bookclub.objects;

public class Meeting {
    public String bookClubId;
    public String location;
    public String date;
    public String time;

    public Meeting(){}

    public Meeting(String bookClubId, String location, String date, String time) {
        this.bookClubId = bookClubId;
        this.location = location;
        this.date = date;
        this.time = time;
    }
}
