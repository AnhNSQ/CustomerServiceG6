using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace CustomerService.Models
{
    /// <summary>
    /// Represents a staff member in the system
    /// </summary>
    public class Staff : BaseEntity
    {
        [Key]
        public int StaffId { get; set; }

        [Required]
        [MaxLength(100)]
        public string Name { get; set; }

        [Required]
        [EmailAddress]
        [MaxLength(255)]
        public string Email { get; set; }

        [Required]
        [MaxLength(50)]
        public string Username { get; set; }

        [Required]
        [MaxLength(255)]
        public string Password { get; set; }

        [Required]
        public int RoleId { get; set; }

        public DateTime RegisterDate { get; set; } = DateTime.UtcNow;

        [MaxLength(20)]
        public string Phone { get; set; }

        // Navigation properties
        public virtual Role Role { get; set; }
        public virtual ICollection<StaffShiftAssign> StaffShiftAssigns { get; set; } = new List<StaffShiftAssign>();
        public virtual ICollection<TicketAssign> AssignedTickets { get; set; } = new List<TicketAssign>();
        public virtual ICollection<TicketAssign> AssignedByTickets { get; set; } = new List<TicketAssign>();
        public virtual ICollection<Invoice> IssuedInvoices { get; set; } = new List<Invoice>();
    }
}
