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

    @Column(name = "role_name", nullable = false, unique = true, columnDefinition = "nvarchar(50)")
    private String roleName;

    @Column(name = "description", columnDefinition = "nvarchar(255)")
    private String description;

    // Quan hệ với Customer
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Customer> customers;

    // Quan hệ với Staff
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Staff> staff;

    public Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }
}
