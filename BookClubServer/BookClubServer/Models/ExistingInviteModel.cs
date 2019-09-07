namespace BookClubServer.Models
{
    public class ExistingInviteModel
    {
        public int InviteId { get; set; }

        public int SenderId { get; set; }

        public int RecieverId { get; set; }

        public int BookClubId { get; set; }
    }
}
