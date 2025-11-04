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
public class AdminOrderDetailResponse {
    private Long orderId;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private LocalDateTime orderDate;
    private BigDecimal totalAmount;
    private BigDecimal shippingCost;
    private String shippingMethod;
    private String estimatedTime;
    private String orderStatus;
    private String shippingStatus;
    private String paymentMethod;
    private String shippingAddress;
    private String recipientName;
    private String recipientPhone;
    private String notes;
    private List<OrderDetailResponse> orderDetails;
    private List<OrderHistoryResponse> activityHistory;
}

