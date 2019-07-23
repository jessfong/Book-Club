using BookClubServer.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace BookClubServer.Services
{
    public class BookClubServices : IBookClubServices
    {
        private readonly BookClubContext context; // Should be _bookClubContext

        public BookClubServices(BookClubContext bookClubContext)
        {
            context = bookClubContext;
        }

        public async Task<User> RegisterNewUserAsync(UserCreateModel model) // shouldn't be model as name. userCreateModel
        {

            /*BAD: var result = (from u in context.Users
                          where u.Username == model.Username
                          select u).Count();*/

            var exist = context.Users.Any(u => u.Username.Equals(model.Username));

            if (exist)
            {
                // hash password

                var newUser = new User
                {
                    Username = model.Username,
                    Password = model.Password,
                    Email = model.Email
                };

                var addTask = context.Users.AddAsync(newUser);

                await addTask;

                var saveTask = context.SaveChangesAsync();

                return new User
                {
                    Username = newUser.Username,
                    Password = newUser.Password,
                    Email = newUser.Email
                };
            }
            else
            {
                return null;
            }
        }
    }
}
