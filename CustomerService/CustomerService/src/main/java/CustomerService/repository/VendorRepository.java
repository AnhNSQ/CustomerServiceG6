package CustomerService.repository;

import CustomerService.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    /**
     * Tìm vendor theo tên (không phân biệt hoa thường)
     */
    Optional<Vendor> findByNameIgnoreCase(String name);

    /**
     * Tìm vendor theo từ khóa trong tên hoặc thông tin liên hệ
     */
    @Query("SELECT v FROM Vendor v WHERE LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(v.contactInfo) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Vendor> searchVendorsByKeyword(@Param("keyword") String keyword);

    /**
     * Lấy vendor có nhiều sản phẩm nhất
     */
    @Query("SELECT v FROM Vendor v LEFT JOIN v.products p GROUP BY v ORDER BY COUNT(p) DESC")
    List<Vendor> findVendorsOrderByProductCount();

    /**
     * Đếm số sản phẩm của vendor
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.vendor.vendorId = :vendorId AND p.status = 'ACTIVE'")
    Long countActiveProductsByVendorId(@Param("vendorId") Long vendorId);

    /**
     * Kiểm tra vendor có tồn tại theo tên
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Lấy vendor theo ID với thông tin sản phẩm
     */
    @Query("SELECT v FROM Vendor v LEFT JOIN FETCH v.products WHERE v.vendorId = :vendorId")
    Optional<Vendor> findByIdWithProducts(@Param("vendorId") Long vendorId);
}
