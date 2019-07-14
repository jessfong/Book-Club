using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace BookClubServer.Data
{
    public class BookClubContext : DbContext
    {
        public BookClubContext(DbContextOptions<BookClubContext> options)
            : base(options)
        {
        }

        public DbSet<User> Users { get; set; }
    }
}
