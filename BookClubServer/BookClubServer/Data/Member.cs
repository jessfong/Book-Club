using Newtonsoft.Json;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BookClubServer.Data
{
    public class Member
    {
        [Key]
        public int ID { get; set; }

        public int? BookClubId { get; set; }

        [ForeignKey("BookClubId")]
        [JsonIgnore]
        public virtual BookClub BookClub { get; set; }

        public int? UserId { get; set; }

        [ForeignKey("UserId")]
        [JsonIgnore]
        public virtual User User { get; set; }
    }
}
