package CustomerService.service;

import CustomerService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderValidationService {
    
    private final OrderRepository orderRepository;
    
    /**
     * Kiểm tra customer có đơn hàng hay không
     * @param customerId ID của customer
     * @return true nếu có đơn hàng, false nếu không
     */
    public boolean hasOrders(Long customerId) {
        log.info("Kiểm tra customer {} có đơn hàng không", customerId);
        
        boolean hasOrders = orderRepository.existsByCustomerId(customerId);
        
        log.info("Customer {} có đơn hàng: {}", customerId, hasOrders);
        return hasOrders;
    }
    
    /**
     * Đếm số đơn hàng của customer
     * @param customerId ID của customer
     * @return số lượng đơn hàng
     */
    public long countOrders(Long customerId) {
        log.info("Đếm số đơn hàng của customer {}", customerId);
        
        long orderCount = orderRepository.countByCustomerId(customerId);
        
        log.info("Customer {} có {} đơn hàng", customerId, orderCount);
        return orderCount;
    }
}
