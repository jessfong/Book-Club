using BookClubServer.Data;
using BookClubServer.Models;
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
        User SignIn(User user);

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

        /// <summary>
        /// Checks if email is valid
        /// </summary>
        /// <param name="email"> Email to check </param>
        /// <returns> If email is valid or not </returns>
        bool IsValidEmail(string email);

        /// <summary>
        /// Creates new book club
        /// </summary>
        /// <param name="bookClubCreateModel"> Data to create new book club with </param>
        /// <returns> A new book club </returns>
        Task<BookClub> CreateBookClubAsync(BookClubCreateModel bookClubCreateModel);

        /// <summary>
        /// Deletes book clubs
        /// </summary>
        /// <param name="bookClub"> Book club that will be deleted </param>
        /// <returns> If book club was deleted or not </returns>
        Task<int> DeleteBookClubAsync(BookClub bookClub);

        /// <summary>
        /// Retrieves a user
        /// </summary>
        /// <param name="userId"> User to return </param>
        /// <returns> User ore null if user doesn't exist </returns>
        Task<User> RetrieveUser(int userId);

        /// <summary>
        /// Creates an invite
        /// </summary>
        /// <param name="inviteCreateModel"> Invite to create </param>
        /// <returns> A new invitation  </returns>
        Task<Invite> CreateInviteAsync(InviteCreateModel inviteCreateModel);

        Task<bool> AcceptInviteAsync(AcceptInviteModel acceptInviteModel);

        /// <summary>
        /// Checks if user sending invite is the book club's admin
        /// </summary>
        /// <param name="inviteCreateModel"> Data to create invitation </param>
        /// <returns> If user is the club admin or not </returns>
        bool IsBookClubAdmin(InviteCreateModel inviteCreateModel);
    }
}
