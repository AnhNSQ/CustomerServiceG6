package CustomerService.service;

import CustomerService.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * Interface cho dịch vụ quản lý Category
 * Tuân thủ Dependency Inversion Principle (DIP)
 */
public interface CategoryService {

    /**
     * Lấy tất cả category đang hoạt động
     */
    List<Category> getAllActiveCategories();

    /**
     * Lấy tất cả category
     */
    List<Category> getAllCategories();

    /**
     * Tìm category theo ID
     */
    Optional<Category> findById(Long categoryId);

    /**
     * Tìm category theo tên
     */
    Optional<Category> findByName(String name);

    /**
     * Tìm category theo từ khóa
     */
    List<Category> searchCategories(String keyword);

    /**
     * Tạo category mới
     */
    Category createCategory(String name, String description);

    /**
     * Cập nhật category
     */
    Category updateCategory(Long categoryId, String name, String description);

    /**
     * Vô hiệu hóa category (soft delete)
     */
    void deactivateCategory(Long categoryId);

    /**
     * Kích hoạt lại category
     */
    void activateCategory(Long categoryId);

    /**
     * Xóa category (hard delete) - chỉ khi không có product nào
     */
    void deleteCategory(Long categoryId);

    /**
     * Đếm số lượng category đang hoạt động
     */
    long countActiveCategories();

    /**
     * Kiểm tra category có tồn tại không
     */
    boolean existsByName(String name);

    /**
     * Lấy category có nhiều product nhất
     */
    List<Category> getCategoriesOrderByProductCount();
}