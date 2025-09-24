using System;

namespace CustomerService.Models
{
    /// <summary>
    /// Base entity class containing common properties for all entities
    /// </summary>
    public abstract class BaseEntity
    {
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime? UpdatedAt { get; set; }
        public bool IsActive { get; set; } = true;
    }
}
