namespace BookClubServer.Models
{
    public class BookClubCreateModel : UserCreateModel
    {
        public int AdminId { get; set; }

        public string Name { get; set; }
    }
}
