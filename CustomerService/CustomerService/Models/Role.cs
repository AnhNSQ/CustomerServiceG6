using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace CustomerService.Models
{
    /// <summary>
    /// Represents different roles within the system
    /// </summary>
    public class Role : BaseEntity
    {
        [Key]
        public int RoleId { get; set; }

        [Required]
        [MaxLength(50)]
        public string RoleName { get; set; }

        // Navigation properties
        public virtual ICollection<Staff> Staff { get; set; } = new List<Staff>();
    }
}
