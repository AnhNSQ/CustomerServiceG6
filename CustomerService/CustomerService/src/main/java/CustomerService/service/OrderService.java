package CustomerService.service;

import CustomerService.dto.CheckoutRequest;
import CustomerService.dto.OrderResponse;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    
    /**
     * Tạo đơn hàng mới từ giỏ hàng
     * @param customerId ID của customer
     * @param request Thông tin checkout
     * @return OrderResponse
     */
    OrderResponse createOrder(Long customerId, CheckoutRequest request);
    
    /**
     * Lấy đơn hàng theo ID
     * @param orderId ID của đơn hàng
     * @return OrderResponse
     */
    Optional<OrderResponse> getOrderById(Long orderId);
    
    /**
     * Lấy tất cả đơn hàng của customer
     * @param customerId ID của customer
     * @return Danh sách đơn hàng
     */
    List<OrderResponse> getOrdersByCustomerId(Long customerId);
    
    /**
     * Cập nhật trạng thái đơn hàng
     * @param orderId ID của đơn hàng
     * @param status Trạng thái mới
     * @return OrderResponse
     */
    OrderResponse updateOrderStatus(Long orderId, String status);
}
