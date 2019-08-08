using BookClubServer.Data;
using Microsoft.AspNetCore.Mvc;
using System.Threading.Tasks;

namespace BookClubServer.Services
{
    public interface IBookClubServices
    {
        /// <summary>
        /// Function to create new users
        /// </summary>
        /// <param name="userCreateModel"> Data to create new user with </param>
        /// <returns> New user </returns>
        Task<User> RegisterNewUserAsync(UserCreateModel userCreateModel);

        /// <summary>
        /// Function to login user
        /// </summary>
        /// <param name="user"> User's entered data </param>
        /// <returns> Message informing user if credentials are valid </returns>
        Task<IActionResult> SignIn(User user);
    }
}
