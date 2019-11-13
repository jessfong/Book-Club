package ca.mohawkcollege.bookclub.objects;

/**
 * Member
 */
public class Member {
    public String userId;
    public String bookClubId;
    public String phoneNumber;

    /**
     * Default empty member constructor
     */
    public Member() {}

    /**
     * Constructor to set information for member
     * @param userId - user id of member
     * @param bookClubId - book club id of member
     * @param phoneNumber - phone number of member
     */
    public Member(String userId, String bookClubId, String phoneNumber) {
        this.userId = userId;
        this.bookClubId = bookClubId;
        this.phoneNumber = phoneNumber;
    }
}
