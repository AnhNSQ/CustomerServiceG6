using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using CustomerService.Models.Enums;

namespace CustomerService.Models
{
    /// <summary>
    /// Represents a customer support ticket
    /// </summary>
    public class Ticket : BaseEntity
    {
        [Key]
        public int TicketId { get; set; }

        [Required]
        public int CustomerId { get; set; }

        [Required]
        [MaxLength(200)]
        public string Subject { get; set; }

        [Required]
        public string Description { get; set; }

        [Required]
        public TicketPriority Priority { get; set; }

        [Required]
        public TicketStatus Status { get; set; } = TicketStatus.Open;

        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime? ClosedAt { get; set; }

        // Navigation properties
        public virtual Customer Customer { get; set; }
        public virtual ICollection<TicketAssign> TicketAssigns { get; set; } = new List<TicketAssign>();
        public virtual ICollection<Evaluation> Evaluations { get; set; } = new List<Evaluation>();
    }
}
