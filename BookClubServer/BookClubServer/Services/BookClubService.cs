using BookClubServer.Data;
using BookClubServer.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Linq;
using System.Net.Mail;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Web.Helpers;

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
            var hash = Crypto.HashPassword(userCreateModel.Password);

            var newUser = new User
            { 
                Email = userCreateModel.Email,
                Password = hash
            };

            await _bookClubContext.Users.AddAsync(newUser);

            await _bookClubContext.SaveChangesAsync();

            int userId = newUser.ID;

            var user = _bookClubContext.Users.First(u => u.ID.Equals(userId));

            return user;
        }
        
        /// <summary>
        /// Verifies if username and password are valid
        /// </summary>
        /// <param name="user"> Entered username and password </param>
        /// <returns> If sign in data is valid or not </returns>
        public User SignIn(User user)
        {
            if (user != null)
            {
                if (DoesUserExist(user.Email) == true)
                {
                    var userFromDatabase = _bookClubContext.Users.First(u => u.Email.Equals(user.Email));

                    var validPassword = Crypto.VerifyHashedPassword(userFromDatabase.Password, user.Password);

                    if (validPassword)
                    {
                        return _bookClubContext.Users.Include(u => u.BookClubs).First(u => u.Email.Equals(user.Email));
                    }
                }
            }

            return null;
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
        /// Checks if book club exists in database already
        /// </summary>
        /// <param name="bookClubId"> Id of book club </param>
        /// <returns> If book club exists or not </returns>
        public bool DoesBookClubExist(int bookClubId)
        {
            return _bookClubContext.BookClubs.Any(b => b.ID.Equals(bookClubId));
        }

        /// <summary>
        /// Retrieves a user
        /// </summary>
        /// <param name="userId"> User to retrieve </param>
        /// <returns> User or null if user doesn't exist </returns>
        public async Task<User> RetrieveUser(int userId)
        {
            return await _bookClubContext.Users.FirstOrDefaultAsync(u => u.ID.Equals(userId));
        }

        /// <summary>
        /// Checks if entered password is strong or not
        /// </summary>
        /// <param name="password"> Password to check </param>
        /// <returns> If password is strong or not </returns>
        public bool IsStrongPassword(string password)
        {
            var hasMinEightChars = password.Length >= 8;
            var hasSpecialChar = password.Any(ch => !char.IsLetterOrDigit(ch));
            var hasUpperChar = new Regex(@"[A-Z]");
            var hasLowerChar = new Regex(@"[a-z]");
            var hasNumbers = new Regex(@"[0-9]");

            return hasMinEightChars && hasSpecialChar && hasUpperChar.IsMatch(password) && hasLowerChar.IsMatch(password) && hasNumbers.IsMatch(password);
        }

        /// <summary>
        /// Checks if entered email is valid
        /// </summary>
        /// <param name="email"> Email to check </param>
        /// <returns> If email is valid or not </returns>
        public bool IsValidEmail(string email)
        {
            try
            {
                MailAddress mail = new MailAddress(email);

                return true;
            }
            catch (FormatException)
            {
                return false;
            }
        }

        /// <summary>
        /// Creates a new book club 
        /// </summary>
        /// <param name="bookClubCreateModel"> Data to create new book club with </param>
        /// <returns> A new book club </returns>
        public async Task<BookClub> CreateBookClubAsync(BookClubCreateModel bookClubCreateModel)
        {
            var newBookClub = new BookClub
            {
                AdminId = bookClubCreateModel.AdminId,
                Name = bookClubCreateModel.Name
            };

            await _bookClubContext.BookClubs.AddAsync(newBookClub);

            await _bookClubContext.SaveChangesAsync();

            int bookClubId = newBookClub.ID;

            var bookClub = _bookClubContext.BookClubs.First(b => b.ID.Equals(bookClubId));

            return bookClub;
        }

        /// <summary>
        /// Deletes a book club
        /// </summary>
        /// <param name="bookClub"> Book club to delete </param>
        /// <returns> If the book club was deleted or not </returns>
        public async Task<int> DeleteBookClubAsync(DeleteBookClubModel deleteBookClubModel)
        {
            var clubToDelete = await _bookClubContext.BookClubs.FirstOrDefaultAsync(b => b.ID.Equals(deleteBookClubModel.ID));

            if (clubToDelete == null)
            {
                return -1;
            }

            var bookDeleted = _bookClubContext.BookClubs.Remove(clubToDelete);

            await _bookClubContext.SaveChangesAsync();

            var deletedClub = _bookClubContext.BookClubs.Find(clubToDelete.ID);

            if (deletedClub == null)
            {
                return 0;
            }

            return 1;
        }

        /// <summary>
        /// Creates an invite
        /// </summary>
        /// <param name="inviteCreateModel"> Invite to create </param>
        /// <returns> A new invitation </returns>
        public async Task<Invite> CreateInviteAsync(InviteCreateModel inviteCreateModel)
        {
            var newInvite = new Invite
            {
                SenderId = inviteCreateModel.SenderId,
                RecieverId = inviteCreateModel.RecieverId,
                BookClubId = inviteCreateModel.BookClubId
            };

            await _bookClubContext.Invites.AddAsync(newInvite);

            await _bookClubContext.SaveChangesAsync();

            int inviteId = newInvite.ID;

            var invite = _bookClubContext.Invites.First(i => i.ID.Equals(inviteId));

            return invite;
        }

        /// <summary>
        /// Accepts invite and adds user as a member of that book club
        /// </summary>
        /// <param name="acceptInviteModel"> Invite to accept </param>
        /// <returns> If invite was accepted </returns>
        public async Task<bool> AcceptInviteAsync(AcceptInviteModel acceptInviteModel)
        {
            var invitation =  _bookClubContext.Invites.FirstOrDefault(i => i.ID.Equals(acceptInviteModel.InviteId));

            if (invitation != null)
            {
                var bookClub = await _bookClubContext.BookClubs.FirstAsync(b => b.ID.Equals(invitation.BookClubId));
                var reciever = await _bookClubContext.Users.FirstAsync(r => r.ID.Equals(invitation.RecieverId));

                var newMember = new Member
                {
                    BookClubId = bookClub.ID,
                    UserId = reciever.ID
                };

                await _bookClubContext.Members.AddAsync(newMember);

                await _bookClubContext.SaveChangesAsync();

                int memberId = newMember.ID;

                var bookClubAdded = _bookClubContext.Members.Any(m => m.ID.Equals(memberId));                

                if (bookClubAdded)
                {
                    return true;
                }
            }

            return false;
        }

        /// <summary>
        /// Checks if user sending invite is the book club's admin
        /// </summary>
        /// <param name="inviteCreateModel"> Data to create invitation </param>
        /// <returns> If user is the club admin or not </returns>
        public bool IsBookClubAdmin(InviteCreateModel inviteCreateModel)
        {
            var sender = _bookClubContext.Users.First(u => u.ID.Equals(inviteCreateModel.SenderId));
            var bookClub = _bookClubContext.BookClubs.First(b => b.ID.Equals(inviteCreateModel.BookClubId));

            var usersClubs = _bookClubContext.Users.Include(u => u.BookClubs).First(u => u.ID.Equals(inviteCreateModel.SenderId));
            if (usersClubs.BookClubs.Contains(bookClub))
            {
                return true;
            }

            return false;
        }

        /// <summary>
        /// Checks if an invite exists
        /// </summary>
        /// <param name="existingInviteModel"> Invite details </param>
        /// <returns> If an existing invite exists with the same details </returns>
        public bool InviteExists(ExistingInviteModel existingInviteModel)
        {
            var invite = new Invite{ };
            
            if (existingInviteModel.SenderId != 0 && existingInviteModel.RecieverId != 0)
            {
                invite = _bookClubContext.Invites.FirstOrDefault(i => i.SenderId == existingInviteModel.SenderId && i.RecieverId == existingInviteModel.RecieverId && i.BookClubId == existingInviteModel.BookClubId);
            }
            else
            {
                invite = _bookClubContext.Invites.FirstOrDefault(i => i.ID.Equals(existingInviteModel.InviteId));
            }

            if (invite != null)
            {
                return true;
            }

            return false;
        }

        /// <summary>
        /// Checks if an invite has already been accepted
        /// </summary>
        /// <param name="acceptInviteModel"> Invite details </param>
        /// <returns> If the invite has already been accepted </returns>
        public bool userAlreadyMember(AcceptInviteModel acceptInviteModel)
        {            
            if (acceptInviteModel.InviteId != 0)
            {
                var invite = _bookClubContext.Invites.First(i => i.ID.Equals(acceptInviteModel.InviteId));

                var member = _bookClubContext.Members.FirstOrDefault(m => m.BookClubId == invite.BookClubId && m.UserId == invite.RecieverId);
                if (member != null)
                {
                    return true;
                }
            }

            return false;
        }
    }
}