package CustomerService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_assign")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketAssign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_assignment_id")
    private Long ticketAssignmentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_to", nullable = false)
    private Staff assignedTo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_by", nullable = false)
    private Staff assignedBy;

    @CreationTimestamp
    @Column(name = "assigned_at", nullable = false, updatable = false)
    private LocalDateTime assignedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_needed", nullable = false)
    private RoleNeeded roleNeeded;

    // Enum cho RoleNeeded
    public enum RoleNeeded {
        FINANCIAL_STAFF, TECHNICAL_SUPPORT
    }

    // Constructor để tạo ticket assignment mới
    public TicketAssign(Ticket ticket, Staff assignedTo, Staff assignedBy, RoleNeeded roleNeeded) {
        this.ticket = ticket;
        this.assignedTo = assignedTo;
        this.assignedBy = assignedBy;
        this.roleNeeded = roleNeeded;
    }
}
