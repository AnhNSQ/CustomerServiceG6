package CustomerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    private List<Long> cartItemIds;
    private String paymentMethod;
    private String shippingAddress;
    private String shippingMethod;
    private String recipientName;
    private String recipientPhone;
    private String notes;
}
