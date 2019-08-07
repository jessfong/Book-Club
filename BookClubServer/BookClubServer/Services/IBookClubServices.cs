using BookClubServer.Data;
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
    }
}
