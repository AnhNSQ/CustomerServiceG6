package CustomerService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "action", nullable = false, columnDefinition = "nvarchar(100)")
    private String action; // e.g., "CREATED", "APPROVED", "STATUS_CHANGED", "SHIPPED", etc.

    @Column(name = "old_status", columnDefinition = "nvarchar(50)")
    private String oldStatus;

    @Column(name = "new_status", columnDefinition = "nvarchar(50)")
    private String newStatus;

    @Column(name = "description", columnDefinition = "nvarchar(MAX)")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "performed_by_staff_id")
    private Staff performedByStaff; // Null if performed by system or customer

    @Column(name = "performed_by_customer_id")
    private Long performedByCustomerId; // Null if performed by staff

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructor for status changes
    public OrderHistory(Order order, String action, String oldStatus, String newStatus, String description, Staff performedByStaff) {
        this.order = order;
        this.action = action;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.description = description;
        this.performedByStaff = performedByStaff;
        this.performedByCustomerId = null;
    }

    // Constructor for customer actions
    public OrderHistory(Order order, String action, String oldStatus, String newStatus, String description, Long performedByCustomerId) {
        this.order = order;
        this.action = action;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.description = description;
        this.performedByStaff = null;
        this.performedByCustomerId = performedByCustomerId;
    }

    // Constructor for system actions
    public OrderHistory(Order order, String action, String description) {
        this.order = order;
        this.action = action;
        this.oldStatus = null;
        this.newStatus = null;
        this.description = description;
        this.performedByStaff = null;
        this.performedByCustomerId = null;
    }
}

