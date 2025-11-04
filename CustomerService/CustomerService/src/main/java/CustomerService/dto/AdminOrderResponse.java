package CustomerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrderResponse {
    private Long orderId;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String shippingStatus;
    private LocalDateTime createdAt;
    private String paymentMethod;
    private String deliveryMethod; // shipping method
}

