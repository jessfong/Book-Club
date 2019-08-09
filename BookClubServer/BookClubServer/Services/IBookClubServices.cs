using BookClubServer.Data;
using System.Threading.Tasks;

namespace BookClubServer.Services
{
    public interface IBookClubServices
    {
        /// <summary>
        /// Create new users
        /// </summary>
        /// <param name="userCreateModel"> Data to create new user with </param>
        /// <returns> New user </returns>
        Task<User> RegisterNewUserAsync(UserCreateModel userCreateModel);

        /// <summary>
        /// Login user
        /// </summary>
        /// <param name="user"> User's entered data </param>
        /// <returns> If user is valid or not </returns>
        bool SignIn(User user);

        /// <summary>
        /// Checks if user already exists
        /// </summary>
        /// <param name="email"> Email of user trying to sign in </param>
        /// <returns> If user exists or not </returns>
        bool DoesUserExist(string email);

        /// <summary>
        /// Checks if entered pasword is strong enough
        /// </summary>
        /// <param name="password"> Password to check </param>
        /// <returns> If passwrod is strong or not </returns>
        bool IsStrongPassword(string password);

        // TODO: Make method to check if email is a valid email
    }
}
