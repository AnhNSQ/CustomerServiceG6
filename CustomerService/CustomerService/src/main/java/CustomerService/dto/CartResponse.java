package CustomerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    
    private Long customerId;
    private String customerName;
    private List<CartItemResponse> cartItems;
    private Integer totalItems;
    private BigDecimal totalAmount;
    private BigDecimal shippingCost;
    private BigDecimal grandTotal;
}
