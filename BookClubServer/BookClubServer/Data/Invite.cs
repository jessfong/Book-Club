using Newtonsoft.Json;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BookClubServer.Data
{
    public class Invite
    {
        // TODO: Add int MeetingID so that users can send invites to meetings
        
        [Key]
        public int ID { get; set; }

        public int? SenderId { get; set; }

        [ForeignKey("SenderId")]
        [JsonIgnore]
        public virtual User Sender { get; set; }

        public int? RecieverId { get; set; }

        [ForeignKey("RecieverId")]
        [JsonIgnore]
        public virtual User Reciever { get; set; }
        
        [Required]
        public int BookClubId { get; set; }
                
        [ForeignKey("BookClubId")]
        [JsonIgnore]
        public virtual BookClub InvitedBookClub { get; set; }     
    } 
}
