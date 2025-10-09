package CustomerService.repository;

import CustomerService.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Tìm category theo tên
     */
    Optional<Category> findByName(String name);

    /**
     * Tìm tất cả category đang hoạt động
     */
    List<Category> findByIsActiveTrue();

    /**
     * Tìm category theo tên (không phân biệt hoa thường)
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Category> findByNameIgnoreCase(@Param("name") String name);

    /**
     * Tìm category theo tên chứa từ khóa
     */
    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND c.isActive = true")
    List<Category> findByNameContainingIgnoreCase(@Param("keyword") String keyword);

    /**
     * Đếm số lượng product trong category
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId AND p.status = 'ACTIVE'")
    Long countActiveProductsByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * Tìm category có nhiều product nhất
     */
    @Query("SELECT c FROM Category c LEFT JOIN c.products p WHERE c.isActive = true GROUP BY c.categoryId ORDER BY COUNT(p) DESC")
    List<Category> findCategoriesOrderByProductCount();

    /**
     * Kiểm tra category có tồn tại không
     */
    boolean existsByName(String name);

    /**
     * Kiểm tra category có tồn tại không (không phân biệt hoa thường)
     */
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE LOWER(c.name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);
}
