package ca.mohawkcollege.bookclub.objects;

public class Member {
    public String memberId;
    public String userId;
    public String bookClubId;

    public Member(){}

    public Member(String memberId, String userId, String bookClubId){
        this.memberId = memberId;
        this.userId = userId;
        this.bookClubId = bookClubId;
    }
}
