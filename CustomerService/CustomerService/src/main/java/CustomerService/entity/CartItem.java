package CustomerService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "sub_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal subTotal;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructor để tạo cart item mới
    public CartItem(Customer customer, Product product, Integer quantity) {
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice();
        this.subTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    // Method để tính lại sub total khi thay đổi quantity
    public void calculateSubTotal() {
        this.subTotal = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    // Method để cập nhật quantity và tính lại sub total
    public void updateQuantity(Integer newQuantity) {
        this.quantity = newQuantity;
        calculateSubTotal();
    }

    // Method để kiểm tra xem có thể thêm quantity không (kiểm tra stock)
    public boolean canAddQuantity(Integer additionalQuantity) {
        return (this.quantity + additionalQuantity) <= this.product.getQuantity();
    }
}
