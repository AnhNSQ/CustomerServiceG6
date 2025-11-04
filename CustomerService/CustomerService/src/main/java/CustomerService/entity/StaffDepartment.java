package CustomerService.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "staff_departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_department_id")
    private Long staffDepartmentId;

    @Column(name = "name", nullable = false, columnDefinition = "nvarchar(100)")
    private String name;

    // Quan hệ với Staff
    @JsonIgnore
    @OneToMany(mappedBy = "staffDepartment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Staff> staff;

    // Quan hệ với Ticket
    @JsonIgnore
    @OneToMany(mappedBy = "staffDepartment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> tickets;

    // Constructor để tạo department mới
    public StaffDepartment(String name) {
        this.name = name;
    }
}
