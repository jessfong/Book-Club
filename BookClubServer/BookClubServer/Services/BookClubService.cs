using BookClubServer.Data;
using BookClubServer.Helpers;
using Microsoft.AspNetCore.Mvc;
using System.Linq;
using System.Threading.Tasks;

namespace BookClubServer.Services
{
    public class BookClubServices : IBookClubServices
    {
        private readonly BookClubContext _bookClubContext;

        public BookClubServices(BookClubContext bookClubContext)
        {
            _bookClubContext = bookClubContext;
        }

        /// <summary>
        /// Checks if new user is already a registered user in the database
        /// If not, a new user is created
        /// </summary>
        /// <param name="userCreateModel"> NEw user to be created </param>
        /// <returns> New user or null </returns>
        public async Task<User> RegisterNewUserAsync(UserCreateModel userCreateModel)
        {
            var exist = _bookClubContext.Users.Any(u => u.Username.Equals(userCreateModel.Username));
            
            if (!exist)
            {
                var passwordHasher = new PasswordHasher();
                var hash = passwordHasher.Hash(userCreateModel.Password);

                var newUser = new User
                { 
                    Username = userCreateModel.Username,
                    Password = hash,
                    Email = userCreateModel.Email
                };

                var addTask = _bookClubContext.Users.AddAsync(newUser);

                await addTask;

                var saveTask = _bookClubContext.SaveChangesAsync();

                return new User
                {
                    Username = newUser.Username,
                    Password = newUser.Password,
                    Email = newUser.Email
                };
            }
            return null;
        }

        /// <summary>
        /// Verifies if username and password are valid
        /// </summary>
        /// <param name="user"> Entered username and password </param>
        /// <returns> If data is valid or not </returns>
        public async Task<IActionResult> SignIn(User user)
        {
            var exist = _bookClubContext.Users.Any(u => u.Username.Equals(user.Username));

            if (exist)
            {
                var passwordHasher = new PasswordHasher();
                var hashedPassword = passwordHasher.Hash(user.Password);

                var validPassword = passwordHasher.Verify(user.Password, hashedPassword);

                if (validPassword)
                {
                    OkResult ok = new OkResult();
                    return ok;
                }
            }

            return null;
        }
    }
}
