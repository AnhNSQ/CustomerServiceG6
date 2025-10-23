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
public class OrderDetailResponse {
    private Long orderDetailId;
    private Long productId;
    private String productName;
    private String productDescription;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subTotal;
}
