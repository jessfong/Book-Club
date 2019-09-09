using Newtonsoft.Json;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BookClubServer.Data
{
    public class BookClub
    {
        // TODO: Add list of current and past meetings

        [Key]
        public int ID { get; set; }
        
        [Required]
        public string Name { get; set; }

        [Required]
        public int AdminId { get; set; }

        [ForeignKey("AdminId")]
        [JsonIgnore]
        public virtual User Admin { get; set; }
    }
}
