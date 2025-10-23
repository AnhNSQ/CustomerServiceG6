package CustomerService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", nullable = false, unique = true, columnDefinition = "nvarchar(50)")
    private RoleName roleName;

    @Column(name = "description", columnDefinition = "nvarchar(255)")
    private String description;

    // Quan hệ với Customer
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Customer> customers;

    // Quan hệ với Staff
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Staff> staff;

    // Enum cho RoleName theo ERD
    public enum RoleName {
        CUSTOMER, ADMIN, LEAD, STAFF
    }

    public Role(RoleName roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }
}
