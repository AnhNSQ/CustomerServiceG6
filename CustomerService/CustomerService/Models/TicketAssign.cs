using System;
using System.ComponentModel.DataAnnotations;
using CustomerService.Models.Enums;

namespace CustomerService.Models
{
    /// <summary>
    /// Represents the assignment of tickets to staff members
    /// </summary>
    public class TicketAssign : BaseEntity
    {
        [Key]
        public int TicketAssignmentId { get; set; }

        [Required]
        public int TicketId { get; set; }

        [Required]
        public int AssignedTo { get; set; }

        [Required]
        public int AssignedBy { get; set; }

        public DateTime AssignedAt { get; set; } = DateTime.UtcNow;

        [Required]
        public RoleNeeded RoleNeeded { get; set; }

        // Navigation properties
        public virtual Ticket Ticket { get; set; }
        public virtual Staff AssignedToStaff { get; set; }
        public virtual Staff AssignedByStaff { get; set; }
    }
}
