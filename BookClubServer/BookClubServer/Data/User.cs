using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace BookClubServer.Data
{
    public class User
    {
        [Key]
        public int ID { get; set; }

        [Required]
        public string Email { get; set; }

        [Required]
        public string Password { get; set; }
        
        public virtual List<BookClub> BookClubs { get; set; }
    }
}