using Microsoft.EntityFrameworkCore;

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