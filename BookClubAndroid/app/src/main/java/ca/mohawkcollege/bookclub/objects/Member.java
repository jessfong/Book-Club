package ca.mohawkcollege.bookclub.objects;

public class Member {
    public String userId;
    public String bookClubId;
    public String phoneNumber;

    public Member() {
    }

    public Member(String userId, String bookClubId, String phoneNumber) {
        this.userId = userId;
        this.bookClubId = bookClubId;
        this.phoneNumber = phoneNumber;
    }
}
