package CustomerService.repository;

import CustomerService.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Tìm tất cả sản phẩm đang hoạt động
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.vendor LEFT JOIN FETCH p.category WHERE p.status = :status")
    List<Product> findByStatus(@Param("status") Product.ProductStatus status);

    /**
     * Tìm sản phẩm theo category
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.vendor LEFT JOIN FETCH p.category WHERE p.category.categoryId = :categoryId AND p.status = :status")
    List<Product> findByCategoryCategoryIdAndStatus(@Param("categoryId") Long categoryId, @Param("status") Product.ProductStatus status);

    /**
     * Tìm sản phẩm theo vendor
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.vendor LEFT JOIN FETCH p.category WHERE p.vendor.vendorId = :vendorId AND p.status = :status")
    List<Product> findByVendorVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") Product.ProductStatus status);

    /**
     * Tìm sản phẩm theo tên (không phân biệt hoa thường)
     */
    List<Product> findByNameContainingIgnoreCaseAndStatus(String name, Product.ProductStatus status);

    /**
     * Tìm sản phẩm theo từ khóa trong tên hoặc mô tả
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.vendor LEFT JOIN FETCH p.category WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.status = :status")
    List<Product> searchProductsByKeyword(@Param("keyword") String keyword, @Param("status") Product.ProductStatus status);

    /**
     * Lấy sản phẩm có giá trong khoảng
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.vendor LEFT JOIN FETCH p.category WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.status = :status")
    List<Product> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice, @Param("status") Product.ProductStatus status);

    /**
     * Lấy sản phẩm có số lượng tồn kho > 0
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.vendor LEFT JOIN FETCH p.category WHERE p.quantity > :quantity AND p.status = :status")
    List<Product> findByQuantityGreaterThanAndStatus(@Param("quantity") Integer quantity, @Param("status") Product.ProductStatus status);

    /**
     * Lấy sản phẩm nổi bật (có thể mở rộng logic sau)
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.vendor LEFT JOIN FETCH p.category WHERE p.status = :status ORDER BY p.productId DESC")
    List<Product> findFeaturedProducts(@Param("status") Product.ProductStatus status);

    /**
     * Lấy sản phẩm mới nhất
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.vendor LEFT JOIN FETCH p.category WHERE p.status = :status ORDER BY p.productId DESC")
    List<Product> findLatestProducts(@Param("status") Product.ProductStatus status);

    /**
     * Đếm số sản phẩm theo category
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId AND p.status = :status")
    Long countByCategoryIdAndStatus(@Param("categoryId") Long categoryId, @Param("status") Product.ProductStatus status);

    /**
     * Đếm số sản phẩm theo vendor
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.vendor.vendorId = :vendorId AND p.status = :status")
    Long countByVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") Product.ProductStatus status);

    /**
     * Kiểm tra sản phẩm có tồn tại theo tên
     */
    boolean existsByNameIgnoreCaseAndStatus(String name, Product.ProductStatus status);

    /**
     * Lấy sản phẩm theo ID với thông tin vendor và category
     */
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.vendor LEFT JOIN FETCH p.category WHERE p.productId = :productId")
    Optional<Product> findByIdWithVendorAndCategory(@Param("productId") Long productId);
}
