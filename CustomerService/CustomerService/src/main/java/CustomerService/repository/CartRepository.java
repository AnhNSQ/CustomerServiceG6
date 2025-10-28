package CustomerService.repository;

import CustomerService.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {

    /**
     * Tìm tất cả cart items của một customer
     */
    @Query("SELECT ci FROM CartItem ci LEFT JOIN FETCH ci.product LEFT JOIN FETCH ci.product.vendor LEFT JOIN FETCH ci.product.category WHERE ci.customer.customerId = :customerId ORDER BY ci.createdAt DESC")
    List<CartItem> findByCustomerId(@Param("customerId") Long customerId);

    /**
     * Tìm cart item của customer với product cụ thể
     */
    @Query("SELECT ci FROM CartItem ci LEFT JOIN FETCH ci.product LEFT JOIN FETCH ci.product.vendor LEFT JOIN FETCH ci.product.category WHERE ci.customer.customerId = :customerId AND ci.product.productId = :productId")
    Optional<CartItem> findByCustomerIdAndProductId(@Param("customerId") Long customerId, @Param("productId") Long productId);

    /**
     * Đếm số lượng items trong cart của customer
     */
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.customer.customerId = :customerId")
    Long countByCustomerId(@Param("customerId") Long customerId);

    /**
     * Tính tổng số lượng sản phẩm trong cart của customer
     */
    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci WHERE ci.customer.customerId = :customerId")
    Long getTotalQuantityByCustomerId(@Param("customerId") Long customerId);

    /**
     * Tính tổng giá trị cart của customer
     */
    @Query("SELECT COALESCE(SUM(ci.subTotal), 0) FROM CartItem ci WHERE ci.customer.customerId = :customerId")
    Double getTotalAmountByCustomerId(@Param("customerId") Long customerId);

    /**
     * Xóa tất cả cart items của customer
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.customer.customerId = :customerId")
    void deleteByCustomerId(@Param("customerId") Long customerId);

    /**
     * Xóa cart item cụ thể của customer
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.customer.customerId = :customerId AND ci.product.productId = :productId")
    void deleteByCustomerIdAndProductId(@Param("customerId") Long customerId, @Param("productId") Long productId);

    /**
     * Kiểm tra xem customer có cart item với product này không
     */
    @Query("SELECT CASE WHEN COUNT(ci) > 0 THEN true ELSE false END FROM CartItem ci WHERE ci.customer.customerId = :customerId AND ci.product.productId = :productId")
    boolean existsByCustomerIdAndProductId(@Param("customerId") Long customerId, @Param("productId") Long productId);
}
