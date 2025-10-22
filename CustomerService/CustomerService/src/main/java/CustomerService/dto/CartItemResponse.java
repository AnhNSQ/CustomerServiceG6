package CustomerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    
    private Long cartItemId;
    private Long productId;
    private String productName;
    private String productDescription;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subTotal;
    private String productImage; // For future use
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
