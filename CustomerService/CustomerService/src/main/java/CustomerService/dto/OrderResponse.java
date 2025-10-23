package CustomerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private String customerName;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private String shippingMethod;
    private BigDecimal shippingCost;
    private String estimatedTime;
    private String shippingAddress;
    private String recipientName;
    private String recipientPhone;
    private String orderStatus;
    private String shippingStatus;
    private String paymentMethod;
    private List<OrderDetailResponse> orderDetails;
}
