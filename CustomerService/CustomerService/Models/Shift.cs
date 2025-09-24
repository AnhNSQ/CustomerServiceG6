using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;

namespace CustomerService.Models
{
    /// <summary>
    /// Represents work shifts in the system
    /// </summary>
    public class Shift : BaseEntity
    {
        [Key]
        public int ShiftId { get; set; }

        [Required]
        [MaxLength(50)]
        public string Name { get; set; }

        [Required]
        public TimeSpan StartTime { get; set; }

        [Required]
        public TimeSpan EndTime { get; set; }

        // Navigation properties
        public virtual ICollection<StaffShiftAssign> StaffShiftAssigns { get; set; } = new List<StaffShiftAssign>();
    }
}
