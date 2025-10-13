package CustomerService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @CreationTimestamp
    @Column(name = "order_date", nullable = false, updatable = false)
    private LocalDateTime orderDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "shipping_method", nullable = false, columnDefinition = "nvarchar(100)")
    private String shippingMethod;

    @Column(name = "cost_estimate", nullable = false, precision = 10, scale = 2)
    private BigDecimal costEstimate;

    @Column(name = "estimated_time", columnDefinition = "nvarchar(100)")
    private String estimatedTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "shipping_status", nullable = false)
    private ShippingStatus shippingStatus = ShippingStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Column(name = "shipping_address", columnDefinition = "nvarchar(MAX)")
    private String shippingAddress;

    // Quan hệ với OrderDetail
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;

    // Quan hệ với Invoice
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Invoice invoice;

    // Quan hệ với Ticket
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ticket> tickets;

    // Enum cho ShippingStatus
    public enum ShippingStatus {
        PENDING, SHIPPED, DELIVERED, RETURNED
    }

    // Enum cho OrderStatus
    public enum OrderStatus {
        PENDING, PAID, SHIPPED, CANCELLED, COMPLETED
    }

    // Constructor để tạo order mới
    public Order(Customer customer, String shippingMethod, BigDecimal costEstimate, String estimatedTime, String shippingAddress) {
        this.customer = customer;
        this.shippingMethod = shippingMethod;
        this.costEstimate = costEstimate;
        this.estimatedTime = estimatedTime;
        this.shippingAddress = shippingAddress;
        this.totalAmount = BigDecimal.ZERO;
        this.shippingStatus = ShippingStatus.PENDING;
        this.orderStatus = OrderStatus.PENDING;
    }
}
