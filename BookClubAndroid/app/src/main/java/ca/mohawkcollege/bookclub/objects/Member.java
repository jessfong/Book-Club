package ca.mohawkcollege.bookclub.objects;

public class Member {
    public String userId;
    public String bookClubId;

    public Member(){}

    public Member(String userId, String bookClubId){
        this.userId = userId;
        this.bookClubId = bookClubId;
    }
}
