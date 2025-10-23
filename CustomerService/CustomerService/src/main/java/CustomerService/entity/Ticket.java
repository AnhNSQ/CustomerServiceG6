package CustomerService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "staff_department_id", nullable = false)
    private StaffDepartment staffDepartment;

    @Column(name = "subject", nullable = false, columnDefinition = "nvarchar(255)")
    private String subject;

    @Column(name = "description", columnDefinition = "nvarchar(MAX)")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    // Quan hệ với TicketAssign
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TicketAssign> ticketAssignments;

    // Quan hệ với Evaluation
    @OneToOne(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Evaluation evaluation;

    // Enum cho Priority
    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    // Enum cho Status
    public enum Status {
        PENDING, OPEN, ASSIGNED, IN_PROGRESS, RESOLVED, CLOSED
    }

    // Constructor để tạo ticket mới
    public Ticket(Customer customer, StaffDepartment staffDepartment, String subject, String description, Priority priority) {
        this.customer = customer;
        this.staffDepartment = staffDepartment;
        this.subject = subject;
        this.description = description;
        this.priority = priority;
        this.status = Status.OPEN;
    }

    // Constructor để tạo ticket mới với order
    public Ticket(Customer customer, Order order, StaffDepartment staffDepartment, String subject, String description, Priority priority) {
        this.customer = customer;
        this.order = order;
        this.staffDepartment = staffDepartment;
        this.subject = subject;
        this.description = description;
        this.priority = priority;
        this.status = Status.OPEN;
    }
}
