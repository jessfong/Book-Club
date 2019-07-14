using BookClubServer.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace BookClubServer.Services
{
    public interface IBookClubServices
    {
        Task<User> RegisterNewUserAsync(User user);
    }
}
