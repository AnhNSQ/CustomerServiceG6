using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace CustomerService.Models
{
    /// <summary>
    /// Represents a customer in the system
    /// </summary>
    public class Customer : BaseEntity
    {
        [Key]
        public int CustomerId { get; set; }

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

        public DateTime RegisterDate { get; set; } = DateTime.UtcNow;

        [MaxLength(20)]
        public string Phone { get; set; }

        // Navigation properties
        public virtual ICollection<Order> Orders { get; set; } = new List<Order>();
        public virtual ICollection<Ticket> Tickets { get; set; } = new List<Ticket>();
        public virtual ICollection<Evaluation> Evaluations { get; set; } = new List<Evaluation>();
    }
}
