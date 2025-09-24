package CustomerService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "shipping_methods")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_method_id")
    private Long shippingMethodId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "cost_estimate", nullable = false, precision = 10, scale = 2)
    private BigDecimal costEstimate;

    @Column(name = "estimated_time", length = 100)
    private String estimatedTime;

    // Constructor để tạo shipping method mới
    public ShippingMethod(String name, BigDecimal costEstimate, String estimatedTime) {
        this.name = name;
        this.costEstimate = costEstimate;
        this.estimatedTime = estimatedTime;
    }
}
