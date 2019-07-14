using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using BookClubServer.Data;

namespace BookClubServer.Services
{
    public class BookClubServices : IBookClubServices
    {
        private readonly BookClubContext context;

        public async Task<User> RegisterNewUserAsync(User model)
        {
            var newUser = new User
            {
                ID = new Guid().ToString(),
                Password = model.Password,
                Email = model.Email
            };

            var addTask = context.Users.AddAsync(newUser);

            await addTask;

            var saveTask = context.SaveChangesAsync();

            await saveTask;
             
            return new User
            {
                ID = newUser.ID,
                Password = newUser.Password,
                Email = newUser.Email
            };
        }
    }
}
