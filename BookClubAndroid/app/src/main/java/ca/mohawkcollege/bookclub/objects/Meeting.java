package ca.mohawkcollege.bookclub.objects;

import java.io.Serializable;

/**
 * Meeting
 */
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

    /**
     * Default empty meeting constructor
     */
    public Meeting() {}

    /**
     * Constructor to set meeting information
     * @param meetingId - id of meeting
     * @param bookClubId - id of book club meeting is for
     * @param location - location of meeting
     * @param date - date fo meeting
     * @param startTime - meeting start time
     * @param endTime - meeting end time
     * @param bookTitle - title of book being reviewed during meeting
     * @param bookAuthor - author of book being reviewed
     * @param bookThumb - thumbnail of book being reviewed
     * @param latitude - latitude of meeting location
     * @param longitude - longitude of meeting location
     */
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
