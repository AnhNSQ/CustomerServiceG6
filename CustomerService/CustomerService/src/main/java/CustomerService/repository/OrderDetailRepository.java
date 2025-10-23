package CustomerService.repository;

import CustomerService.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    
    /**
     * Tìm tất cả order detail của một order
     */
    @Query("SELECT od FROM OrderDetail od LEFT JOIN FETCH od.product WHERE od.order.orderId = :orderId")
    List<OrderDetail> findByOrderOrderId(@Param("orderId") Long orderId);
    
    /**
     * Tìm order detail theo order ID và product ID
     */
    @Query("SELECT od FROM OrderDetail od WHERE od.order.orderId = :orderId AND od.product.productId = :productId")
    List<OrderDetail> findByOrderIdAndProductId(@Param("orderId") Long orderId, @Param("productId") Long productId);
}
