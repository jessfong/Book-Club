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

        public DbSet<BookClub> BookClubs { get; set; }

        public DbSet<Invite> Invites { get; set; }

        public DbSet<Member> Members { get; set; }
    }
}