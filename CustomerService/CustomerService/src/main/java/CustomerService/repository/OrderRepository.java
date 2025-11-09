package CustomerService.repository;

import CustomerService.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * Tìm tất cả đơn hàng của một customer
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer WHERE o.customer.customerId = :customerId ORDER BY o.orderDate DESC")
    List<Order> findByCustomerIdOrderByOrderDateDesc(@Param("customerId") Long customerId);
    
    /**
     * Đếm số đơn hàng của customer
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.customer.customerId = :customerId")
    long countByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * Kiểm tra customer có đơn hàng không
     */
    @Query("SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END FROM Order o WHERE o.customer.customerId = :customerId")
    boolean existsByCustomerId(@Param("customerId") Long customerId);
    
    /**
     * Tìm đơn hàng theo ID với customer
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer WHERE o.orderId = :orderId")
    Optional<Order> findByIdWithCustomer(@Param("orderId") Long orderId);
    
    /**
     * Tìm tất cả đơn hàng PAID của một customer
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer WHERE o.customer.customerId = :customerId AND o.orderStatus = 'PAID' ORDER BY o.orderDate DESC")
    List<Order> findPaidOrdersByCustomerId(@Param("customerId") Long customerId);
    /*
     * Tìm tất cả đơn hàng với customer, sắp xếp theo ngày tạo (mới nhất trước)
     */
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.customer ORDER BY o.orderDate DESC")
    List<Order> findAllWithCustomerOrderByOrderDateDesc();
}
