using BookClubServer.Data;

namespace BookClubServer.Models
{
    public class UserCreateModel
    {
        public string Email { get; set; }

        public string Password { get; set; }
        
        public User GetUser()
        {
            return new User
            {
                Email = Email,
                Password = Password
            };
        }
    }
}
