package CustomerService.service;

import CustomerService.entity.Category;
import CustomerService.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Lấy tất cả category đang hoạt động
     */
    @Transactional(readOnly = true)
    public List<Category> getAllActiveCategories() {
        log.info("Fetching all active categories");
        return categoryRepository.findByIsActiveTrue();
    }

    /**
     * Lấy tất cả category
     */
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        log.info("Fetching all categories");
        return categoryRepository.findAll();
    }

    /**
     * Tìm category theo ID
     */
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long categoryId) {
        log.info("Finding category by ID: {}", categoryId);
        return categoryRepository.findById(categoryId);
    }

    /**
     * Tìm category theo tên
     */
    @Transactional(readOnly = true)
    public Optional<Category> findByName(String name) {
        log.info("Finding category by name: {}", name);
        return categoryRepository.findByNameIgnoreCase(name);
    }

    /**
     * Tìm category theo từ khóa
     */
    @Transactional(readOnly = true)
    public List<Category> searchCategories(String keyword) {
        log.info("Searching categories with keyword: {}", keyword);
        return categoryRepository.findByNameContainingIgnoreCase(keyword);
    }

    /**
     * Tạo category mới
     */
    public Category createCategory(String name, String description) {
        log.info("Creating new category: {}", name);
        
        // Kiểm tra category đã tồn tại chưa
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Category với tên '" + name + "' đã tồn tại");
        }

        Category category = new Category(name, description);
        Category savedCategory = categoryRepository.save(category);
        
        log.info("Category created successfully with ID: {}", savedCategory.getCategoryId());
        return savedCategory;
    }

    /**
     * Cập nhật category
     */
    public Category updateCategory(Long categoryId, String name, String description) {
        log.info("Updating category ID: {}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy category với ID: " + categoryId));

        // Kiểm tra tên mới có trùng với category khác không
        if (!category.getName().equalsIgnoreCase(name) && categoryRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Category với tên '" + name + "' đã tồn tại");
        }

        category.setName(name);
        category.setDescription(description);
        
        Category updatedCategory = categoryRepository.save(category);
        log.info("Category updated successfully: {}", updatedCategory.getName());
        
        return updatedCategory;
    }

    /**
     * Vô hiệu hóa category (soft delete)
     */
    public void deactivateCategory(Long categoryId) {
        log.info("Deactivating category ID: {}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy category với ID: " + categoryId));

        category.setIsActive(false);
        categoryRepository.save(category);
        
        log.info("Category deactivated successfully: {}", category.getName());
    }

    /**
     * Kích hoạt lại category
     */
    public void activateCategory(Long categoryId) {
        log.info("Activating category ID: {}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy category với ID: " + categoryId));

        category.setIsActive(true);
        categoryRepository.save(category);
        
        log.info("Category activated successfully: {}", category.getName());
    }

    /**
     * Xóa category (hard delete) - chỉ khi không có product nào
     */
    public void deleteCategory(Long categoryId) {
        log.info("Deleting category ID: {}", categoryId);
        
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy category với ID: " + categoryId));

        // Kiểm tra category có product không
        Long productCount = categoryRepository.countActiveProductsByCategoryId(categoryId);
        if (productCount > 0) {
            throw new IllegalStateException("Không thể xóa category '" + category.getName() + "' vì còn " + productCount + " sản phẩm");
        }

        categoryRepository.delete(category);
        log.info("Category deleted successfully: {}", category.getName());
    }

    /**
     * Đếm số lượng category đang hoạt động
     */
    @Transactional(readOnly = true)
    public long countActiveCategories() {
        return categoryRepository.findByIsActiveTrue().size();
    }

    /**
     * Kiểm tra category có tồn tại không
     */
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    /**
     * Lấy category có nhiều product nhất
     */
    @Transactional(readOnly = true)
    public List<Category> getCategoriesOrderByProductCount() {
        log.info("Fetching categories ordered by product count");
        return categoryRepository.findCategoriesOrderByProductCount();
    }
}
