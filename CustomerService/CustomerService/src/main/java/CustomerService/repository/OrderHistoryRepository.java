package CustomerService.repository;

import CustomerService.entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {
    
    /**
     * Tìm tất cả lịch sử của một đơn hàng, sắp xếp theo thời gian tạo (mới nhất trước)
     */
    @Query("SELECT h FROM OrderHistory h LEFT JOIN FETCH h.performedByStaff WHERE h.order.orderId = :orderId ORDER BY h.createdAt DESC")
    List<OrderHistory> findByOrderIdOrderByCreatedAtDesc(@Param("orderId") Long orderId);
}

