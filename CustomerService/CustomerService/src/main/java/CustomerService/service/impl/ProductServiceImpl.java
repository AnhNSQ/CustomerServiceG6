package CustomerService.service.impl;

import CustomerService.entity.Product;
import CustomerService.entity.Vendor;
import CustomerService.entity.Category;
import CustomerService.repository.ProductRepository;
import CustomerService.repository.VendorRepository;
import CustomerService.repository.CategoryRepository;
import CustomerService.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final VendorRepository vendorRepository;
    private final CategoryRepository categoryRepository;

    /**
     * Lấy tất cả sản phẩm đang hoạt động
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllActiveProducts() {
        log.info("Fetching all active products");
        return productRepository.findByStatus(Product.ProductStatus.ACTIVE);
    }

    /**
     * Lấy tất cả sản phẩm
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll();
    }

    /**
     * Lấy sản phẩm nổi bật (limit số lượng)
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts(int limit) {
        log.info("Fetching {} featured products", limit);
        List<Product> products = productRepository.findFeaturedProducts(Product.ProductStatus.ACTIVE);
        return products.stream().limit(limit).toList();
    }

    /**
     * Lấy sản phẩm mới nhất
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getLatestProducts(int limit) {
        log.info("Fetching {} latest products", limit);
        List<Product> products = productRepository.findLatestProducts(Product.ProductStatus.ACTIVE);
        return products.stream().limit(limit).toList();
    }

    /**
     * Tìm sản phẩm theo ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long productId) {
        log.info("Finding product by ID: {}", productId);
        return productRepository.findById(productId);
    }

    /**
     * Tìm sản phẩm theo ID với thông tin vendor và category
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findByIdWithDetails(Long productId) {
        log.info("Finding product with details by ID: {}", productId);
        return productRepository.findByIdWithVendorAndCategory(productId);
    }

    /**
     * Tìm sản phẩm theo category
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> findByCategory(Long categoryId) {
        log.info("Finding products by category ID: {}", categoryId);
        return productRepository.findByCategoryCategoryIdAndStatus(categoryId, Product.ProductStatus.ACTIVE);
    }

    /**
     * Tìm sản phẩm theo vendor
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> findByVendor(Long vendorId) {
        log.info("Finding products by vendor ID: {}", vendorId);
        return productRepository.findByVendorVendorIdAndStatus(vendorId, Product.ProductStatus.ACTIVE);
    }

    /**
     * Tìm kiếm sản phẩm theo từ khóa
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> searchProducts(String keyword) {
        log.info("Searching products with keyword: {}", keyword);
        return productRepository.searchProductsByKeyword(keyword, Product.ProductStatus.ACTIVE);
    }

    /**
     * Tìm sản phẩm theo khoảng giá
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Finding products by price range: {} - {}", minPrice, maxPrice);
        return productRepository.findByPriceRange(minPrice.doubleValue(), maxPrice.doubleValue(), Product.ProductStatus.ACTIVE);
    }

    /**
     * Lấy sản phẩm còn hàng
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getInStockProducts() {
        log.info("Fetching in-stock products");
        return productRepository.findByQuantityGreaterThanAndStatus(0, Product.ProductStatus.ACTIVE);
    }

    /**
     * Tạo sản phẩm mới
     */
    @Override
    public Product createProduct(Long vendorId, Long categoryId, String name, String description, 
                               BigDecimal price, Integer quantity) {
        log.info("Creating new product: {}", name);
        
        // Kiểm tra vendor tồn tại
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vendor với ID: " + vendorId));
        
        // Kiểm tra category tồn tại
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy category với ID: " + categoryId));

        // Kiểm tra tên sản phẩm đã tồn tại chưa
        if (productRepository.existsByNameIgnoreCaseAndStatus(name, Product.ProductStatus.ACTIVE)) {
            throw new IllegalArgumentException("Sản phẩm với tên '" + name + "' đã tồn tại");
        }

        Product product = new Product(vendor, category, name, description, price, quantity);
        Product savedProduct = productRepository.save(product);
        
        log.info("Product created successfully with ID: {}", savedProduct.getProductId());
        return savedProduct;
    }

    /**
     * Cập nhật sản phẩm
     */
    @Override
    public Product updateProduct(Long productId, String name, String description, 
                               BigDecimal price, Integer quantity) {
        log.info("Updating product ID: {}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        // Kiểm tra tên mới có trùng với sản phẩm khác không
        if (!product.getName().equalsIgnoreCase(name) && 
            productRepository.existsByNameIgnoreCaseAndStatus(name, Product.ProductStatus.ACTIVE)) {
            throw new IllegalArgumentException("Sản phẩm với tên '" + name + "' đã tồn tại");
        }

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully: {}", updatedProduct.getName());
        
        return updatedProduct;
    }

    /**
     * Vô hiệu hóa sản phẩm
     */
    @Override
    public void deactivateProduct(Long productId) {
        log.info("Deactivating product ID: {}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        product.setStatus(Product.ProductStatus.INACTIVE);
        productRepository.save(product);
        
        log.info("Product deactivated successfully: {}", product.getName());
    }

    /**
     * Kích hoạt lại sản phẩm
     */
    @Override
    public void activateProduct(Long productId) {
        log.info("Activating product ID: {}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        product.setStatus(Product.ProductStatus.ACTIVE);
        productRepository.save(product);
        
        log.info("Product activated successfully: {}", product.getName());
    }

    /**
     * Xóa sản phẩm
     */
    @Override
    public void deleteProduct(Long productId) {
        log.info("Deleting product ID: {}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        productRepository.delete(product);
        log.info("Product deleted successfully: {}", product.getName());
    }

    /**
     * Cập nhật số lượng tồn kho
     */
    @Override
    public void updateStock(Long productId, Integer newQuantity) {
        log.info("Updating stock for product ID: {} to quantity: {}", productId, newQuantity);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + productId));

        product.setQuantity(newQuantity);
        productRepository.save(product);
        
        log.info("Stock updated successfully for product: {}", product.getName());
    }

    /**
     * Đếm số lượng sản phẩm đang hoạt động
     */
    @Override
    @Transactional(readOnly = true)
    public long countActiveProducts() {
        return productRepository.findByStatus(Product.ProductStatus.ACTIVE).size();
    }

    /**
     * Đếm số lượng sản phẩm theo category
     */
    @Override
    @Transactional(readOnly = true)
    public long countProductsByCategory(Long categoryId) {
        return productRepository.countByCategoryIdAndStatus(categoryId, Product.ProductStatus.ACTIVE);
    }

    /**
     * Đếm số lượng sản phẩm theo vendor
     */
    @Override
    @Transactional(readOnly = true)
    public long countProductsByVendor(Long vendorId) {
        return productRepository.countByVendorIdAndStatus(vendorId, Product.ProductStatus.ACTIVE);
    }

    /**
     * Kiểm tra sản phẩm có tồn tại không
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return productRepository.existsByNameIgnoreCaseAndStatus(name, Product.ProductStatus.ACTIVE);
    }

    /**
     * Lấy sản phẩm đang hoạt động với phân trang
     */
    @Override
    @Transactional(readOnly = true)
    public java.util.Map<String, Object> getAllActiveProductsPaginated(int page, int size) {
        log.info("Fetching paginated active products - page: {}, size: {}", page, size);
        
        List<Product> allProducts = productRepository.findByStatus(Product.ProductStatus.ACTIVE);
        long totalProducts = allProducts.size();
        int totalPages = (int) Math.ceil((double) totalProducts / size);
        
        // Calculate pagination bounds
        int start = page * size;
        int end = Math.min(start + size, (int) totalProducts);
        
        // Get paginated products
        List<Product> paginatedProducts = start < totalProducts 
            ? allProducts.subList(start, end)
            : java.util.Collections.emptyList();
        
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("products", paginatedProducts);
        result.put("totalProducts", totalProducts);
        result.put("totalPages", totalPages);
        result.put("currentPage", page);
        result.put("pageSize", size);
        
        log.info("Paginated products retrieved - showing {} of {} products", paginatedProducts.size(), totalProducts);
        return result;
    }
}
