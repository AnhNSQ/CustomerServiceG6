package CustomerService.service;

/**
 * Interface cho dịch vụ xác thực đơn hàng
 * Tuân thủ Single Responsibility Principle (SRP)
 */
public interface OrderValidationService {
    
    /**
     * Kiểm tra customer có đơn hàng hay không
     * @param customerId ID của customer
     * @return true nếu có đơn hàng, false nếu không
     */
    boolean hasOrders(Long customerId);
    
    /**
     * Đếm số đơn hàng của customer
     * @param customerId ID của customer
     * @return số lượng đơn hàng
     */
    long countOrders(Long customerId);
}