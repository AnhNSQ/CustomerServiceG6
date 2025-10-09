package CustomerService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "staff")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staff_id")
    private Long staffId;

    @Column(name = "name", nullable = false, columnDefinition = "nvarchar(100)")
    private String name;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "nvarchar(100)")
    private String email;

    @Column(name = "username", nullable = false, unique = true, columnDefinition = "nvarchar(50)")
    private String username;

    @Column(name = "password", nullable = false, columnDefinition = "nvarchar(255)")
    private String password;

    @Column(name = "phone", columnDefinition = "nvarchar(20)")
    private String phone;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "register_date", nullable = false, updatable = false)
    private LocalDateTime registerDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    // Quan hệ với TicketAssign - staff được assign
    @OneToMany(mappedBy = "assignedTo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketAssign> assignedTickets;

    // Quan hệ với TicketAssign - staff assign cho người khác
    @OneToMany(mappedBy = "assignedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketAssign> assignedByMe;

    // Quan hệ với StaffShiftAssign
    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StaffShiftAssign> shiftAssignments;

    // Quan hệ với Invoice - staff xuất hóa đơn
    @OneToMany(mappedBy = "issuedBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Invoice> issuedInvoices;

    // Constructor để tạo staff mới
    public Staff(String name, String email, String username, String password, String phone, Role role) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.isActive = true;
        this.role = role;
    }
}
