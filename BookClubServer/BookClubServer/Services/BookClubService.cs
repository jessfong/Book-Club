using BookClubServer.Data;
using BookClubServer.Helpers;
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
        /// Creates a new user
        /// </summary>
        /// <param name="userCreateModel"> New user to create </param>
        /// <returns> New user </returns>
        public async Task<User> RegisterNewUserAsync(UserCreateModel userCreateModel)
        {
            var passwordHasher = new PasswordHasher();
            var hash = passwordHasher.Hash(userCreateModel.Password);

            var newUser = new User
            { 
                Email = userCreateModel.Email,
                Password = hash
            };

            await _bookClubContext.Users.AddAsync(newUser);

            await _bookClubContext.SaveChangesAsync();

            return new User
            {
                Email = newUser.Email,
                Password = newUser.Password
            };
        }
        
        /// <summary>
        /// Verifies if username and password are valid
        /// </summary>
        /// <param name="user"> Entered username and password </param>
        /// <returns> If sign in data is valid or not </returns>
        public bool SignIn(User user)
        {
            if (DoesUserExist(user.Email))
            {
                var passwordHasher = new PasswordHasher();
                var hashedPassword = passwordHasher.Hash(user.Password);

                var validPassword = passwordHasher.Verify(user.Password, hashedPassword);

                if (validPassword)
                {
                    return true;
                }
            }

            return false;
        }

        /// <summary>
        /// Checks if user exists in database already
        /// </summary>
        /// <param name="email"> Email of user </param>
        /// <returns> If user exists or not </returns>
        public bool DoesUserExist(string email)
        {
            return _bookClubContext.Users.Any(u => u.Email.Equals(email));
        }

        /// <summary>
        /// Checks if entered password is strong or not
        /// </summary>
        /// <param name="password"> Password to check </param>
        /// <returns> If password is strong or not </returns>
        public bool IsStrongPassword(string password)
        {
            if (password.Length < 8 || !password.Contains(@"/.[!,@,#,$,%,^,&,*,?,_,~,-,£,(,)]/") || 
               !password.Contains("[A-Z]") || !password.Contains("[a-z]") || !password.Contains("[0-9]"))
            {
                return false;
            }

            return true;
        }
    }
}
