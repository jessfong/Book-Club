namespace BookClubServer.Models
{
    public class InviteCreateModel
    {
        public int BookClubId { get; set; }

        public int SenderId { get; set; }

        public int RecieverId { get; set; }  
    }
}
