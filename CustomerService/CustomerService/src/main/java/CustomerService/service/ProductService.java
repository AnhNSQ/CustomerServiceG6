package CustomerService.service;

import CustomerService.entity.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interface cho dịch vụ quản lý Product
 * Tuân thủ Dependency Inversion Principle (DIP)
 */
public interface ProductService {

    /**
     * Lấy tất cả sản phẩm đang hoạt động
     */
    List<Product> getAllActiveProducts();

    /**
     * Lấy tất cả sản phẩm
     */
    List<Product> getAllProducts();

    /**
     * Lấy sản phẩm nổi bật (limit số lượng)
     */
    List<Product> getFeaturedProducts(int limit);

    /**
     * Lấy sản phẩm mới nhất
     */
    List<Product> getLatestProducts(int limit);

    /**
     * Tìm sản phẩm theo ID
     */
    Optional<Product> findById(Long productId);

    /**
     * Tìm sản phẩm theo ID với thông tin vendor và category
     */
    Optional<Product> findByIdWithDetails(Long productId);

    /**
     * Tìm sản phẩm theo category
     */
    List<Product> findByCategory(Long categoryId);

    /**
     * Tìm sản phẩm theo vendor
     */
    List<Product> findByVendor(Long vendorId);

    /**
     * Tìm kiếm sản phẩm theo từ khóa
     */
    List<Product> searchProducts(String keyword);

    /**
     * Tìm sản phẩm theo khoảng giá
     */
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Lấy sản phẩm còn hàng
     */
    List<Product> getInStockProducts();

    /**
     * Tạo sản phẩm mới
     */
    Product createProduct(Long vendorId, Long categoryId, String name, String description, 
                         BigDecimal price, Integer quantity);

    /**
     * Cập nhật sản phẩm
     */
    Product updateProduct(Long productId, String name, String description, 
                         BigDecimal price, Integer quantity);

    /**
     * Vô hiệu hóa sản phẩm
     */
    void deactivateProduct(Long productId);

    /**
     * Kích hoạt lại sản phẩm
     */
    void activateProduct(Long productId);

    /**
     * Xóa sản phẩm
     */
    void deleteProduct(Long productId);

    /**
     * Cập nhật số lượng tồn kho
     */
    void updateStock(Long productId, Integer newQuantity);

    /**
     * Đếm số lượng sản phẩm đang hoạt động
     */
    long countActiveProducts();

    /**
     * Đếm số lượng sản phẩm theo category
     */
    long countProductsByCategory(Long categoryId);

    /**
     * Đếm số lượng sản phẩm theo vendor
     */
    long countProductsByVendor(Long vendorId);

    /**
     * Kiểm tra sản phẩm có tồn tại không
     */
    boolean existsByName(String name);
}