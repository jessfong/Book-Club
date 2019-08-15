using BookClubServer.Data;

namespace BookClubServer.Models
{
    public class UserCreateModel
    {
        public string Email { get; set; }

        public string Password { get; set; }

        public User getUser()
        {
            return new User
            {
                Email = this.Email,
                Password = this.Password
            };
        }
    }
}
