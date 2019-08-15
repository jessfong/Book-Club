using BookClubServer.Data;
using BookClubServer.Helpers;
using BookClubServer.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Linq;
using System.Net.Mail;
using System.Text.RegularExpressions;
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
        public User SignIn(User user)
        {
            if (DoesUserExist(user.Email))
            {
                var passwordHasher = new PasswordHasher();
                var hashedPassword = passwordHasher.Hash(user.Password);

                var validPassword = passwordHasher.Verify(user.Password, hashedPassword);

                if (validPassword)
                {
                    return _bookClubContext.Users.Include(u => u.BookClubs).First(u => u.Email.Equals(user.Email));
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
    }
}
