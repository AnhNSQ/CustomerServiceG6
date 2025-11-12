# TỔNG HỢP CODE LIÊN QUAN ĐẾN SEARCH

## 📁 1. BACKEND - CONTROLLER

### File: `WebController.java`

#### 1.1. API Endpoint - Lấy gợi ý tìm kiếm
```java
/**
 * API endpoint để lấy gợi ý tìm kiếm
 */
@GetMapping("/api/search/suggestions")
@ResponseBody
public ResponseEntity<ApiResponse<List<String>>> getSearchSuggestions(@RequestParam String q) {
    try {
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success(List.of(), "No suggestions"));
        }
        
        String keyword = q.trim();
        List<Product> products = productService.searchProducts(keyword);
        
        // Lấy tên sản phẩm làm gợi ý
        List<String> suggestions = products.stream()
            .map(Product::getName)
            .distinct()
            .limit(5)
            .toList();
        
        return ResponseEntity.ok(ApiResponse.success(suggestions, "Search suggestions"));
        
    } catch (Exception e) {
        log.error("Error getting search suggestions: ", e);
        return ResponseEntity.badRequest()
            .body(ApiResponse.error("Error getting search suggestions"));
    }
}
```

#### 1.2. Endpoint - Trang tìm kiếm sản phẩm
```java
/**
 * Trang tìm kiếm sản phẩm
 */
@GetMapping("/search")
public String searchProducts(@RequestParam(required = false) String q,
                            @RequestParam(required = false) String sort,
                            @RequestParam(required = false) String view,
                            @RequestParam(required = false) String priceRange,
                            Model model) {
    try {
        log.info("Loading search page with query: {}, sort: {}, view: {}, priceRange: {}", 
                q, sort, view, priceRange);
        
        List<Product> searchResults = List.of();
        String searchQuery = "";
        
        // Thực hiện tìm kiếm nếu có từ khóa
        if (q != null && !q.trim().isEmpty()) {
            searchQuery = q.trim();
            searchResults = productService.searchProducts(searchQuery);
            
            // Áp dụng bộ lọc giá nếu có
            if (priceRange != null && !priceRange.isEmpty()) {
                searchResults = filterProductsByPriceRange(searchResults, priceRange);
            }
            
            // Áp dụng sắp xếp nếu có
            if (sort != null && !sort.isEmpty()) {
                searchResults = sortProducts(searchResults, sort);
            }
        }
        
        // Lấy sản phẩm mới nhất cho sidebar
        var newReleases = productService.getLatestProducts(3);
        
        // Lấy categories để hiển thị trong navigation
        var categories = categoryService.getAllActiveCategories();
        
        // Thêm thông tin vào model
        model.addAttribute("products", searchResults);
        model.addAttribute("newReleases", newReleases);
        model.addAttribute("categories", categories);
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("currentSort", sort != null ? sort : "default");
        model.addAttribute("currentView", view != null ? view : "grid");
        model.addAttribute("currentPriceRange", priceRange != null ? priceRange : "");
        model.addAttribute("totalProducts", searchResults.size());
        model.addAttribute("hasSearchQuery", !searchQuery.isEmpty());
        
        log.info("Search page loaded successfully with {} results for query: '{}'", 
                searchResults.size(), searchQuery);
        
        return "catalog";
        
    } catch (Exception e) {
        log.error("Error loading search page: ", e);
        return "redirect:/home";
    }
}
```

#### 1.3. Helper Method - Lọc theo giá
```java
private List<Product> filterProductsByPriceRange(List<Product> products, String priceRange) {
    return products.stream().filter(product -> {
        double price = product.getPrice().doubleValue();
        switch (priceRange) {
            case "under-500k":
                return price < 500000;
            case "500k-1m":
                return price >= 500000 && price <= 1000000;
            case "over-1m":
                return price > 1000000;
            default:
                return true;
        }
    }).toList();
}
```

#### 1.4. Helper Method - Sắp xếp sản phẩm
```java
/**
 * Sắp xếp sản phẩm
 */
private List<Product> sortProducts(List<Product> products, String sort) {
    return products.stream().sorted((p1, p2) -> {
        switch (sort) {
            case "price-low":
                return p1.getPrice().compareTo(p2.getPrice());
            case "price-high":
                return p2.getPrice().compareTo(p1.getPrice());
            case "name":
                return p1.getName().compareToIgnoreCase(p2.getName());
            case "newest":
                return p2.getProductId().compareTo(p1.getProductId());
            default:
                return 0;
        }
    }).toList();
}
```

---

## 📁 2. BACKEND - SERVICE

### File: `ProductService.java` (Interface)
```java
/**
 * Tìm kiếm sản phẩm theo từ khóa
 */
List<Product> searchProducts(String keyword);
```

### File: `ProductServiceImpl.java` (Implementation)
```java
/**
 * Tìm kiếm sản phẩm theo từ khóa
 */
@Override
@Transactional(readOnly = true)
public List<Product> searchProducts(String keyword) {
    log.info("Searching products with keyword: {}", keyword);
    return productRepository.searchProductsByKeyword(keyword, Product.ProductStatus.ACTIVE);
}
```

---

## 📁 3. BACKEND - REPOSITORY

### File: `ProductRepository.java`
```java
/**
 * Tìm sản phẩm theo từ khóa trong tên hoặc mô tả
 */
@Query("SELECT p FROM Product p LEFT JOIN FETCH p.vendor LEFT JOIN FETCH p.category WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.status = :status")
List<Product> searchProductsByKeyword(@Param("keyword") String keyword, @Param("status") Product.ProductStatus status);
```

---

## 📁 4. BACKEND - DTO

### File: `ApiResponse.java`
```java
package CustomerService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Thành công", data);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }
}
```

---

## 📁 5. FRONTEND - HTML TEMPLATE

### File: `fragments/header.html`
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<body>
<!-- Header -->
<header th:fragment="header">
    <div class="header-content">
        <a href="/" class="logo">TECHSAVE</a>
        <div class="search-box">
            <input type="text" id="searchInput" placeholder="Tìm kiếm sản phẩm..." 
                   onkeypress="handleSearchKeyPress(event)" 
                   oninput="handleSearchInput(event)">
            <button onclick="handleSearchClick()"><i class="fas fa-search"></i></button>
            <div class="search-suggestions" id="searchSuggestions">
                <!-- Suggestions will be populated here -->
            </div>
        </div>
        <div class="phone-info">
            <p>CÂU HỎI? GỌI CHO CHÚNG TÔI</p>
            <a href="tel:1900123456"><i class="fas fa-phone"></i> 1900.123.456</a>
        </div>
    </div>
</header>
</body>
</html>
```

---

## 📁 6. FRONTEND - CSS STYLES

### File: `home.html` (CSS Section)
```css
.search-box {
    display: flex;
    width: 500px;
    border-radius: 25px;
    overflow: hidden;
    box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    position: relative;
}

.search-box input {
    flex: 1;
    padding: 15px 20px;
    border: 1px solid #ddd;
    font-size: 14px;
    outline: none;
}

.search-box input:focus {
    border-color: #d32f2f;
}

.search-box button {
    padding: 15px 25px;
    background: #d32f2f;
    border: none;
    color: white;
    cursor: pointer;
    transition: all 0.3s ease;
}

.search-box button:hover {
    background: #b71c1c;
}

/* Search Suggestions */
.search-suggestions {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: white;
    border: 1px solid #ddd;
    border-top: none;
    border-radius: 0 0 25px 25px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    z-index: 1000;
    max-height: 200px;
    overflow-y: auto;
    display: none;
}

.search-suggestions.show {
    display: block;
}

.suggestion-item {
    padding: 12px 20px;
    cursor: pointer;
    border-bottom: 1px solid #f0f0f0;
    transition: background-color 0.2s ease;
}

.suggestion-item:hover {
    background-color: #f8f9fa;
}

.suggestion-item:last-child {
    border-bottom: none;
}

.suggestion-item i {
    margin-right: 10px;
    color: #d32f2f;
}
```

---

## 📁 7. FRONTEND - JAVASCRIPT

### File: `home.html` (JavaScript Section)

#### 7.1. Xử lý input tìm kiếm
```javascript
// Function để xử lý input tìm kiếm và hiển thị gợi ý
function handleSearchInput(event) {
    const searchTerm = event.target.value.trim();
    const suggestionsContainer = document.getElementById('searchSuggestions');
    
    if (searchTerm.length >= 2) {
        // Debounce search suggestions
        clearTimeout(window.searchTimeout);
        window.searchTimeout = setTimeout(() => {
            fetchSearchSuggestions(searchTerm);
        }, 300);
    } else {
        hideSuggestions();
    }
}
```

#### 7.2. Lấy gợi ý từ API
```javascript
// Function để lấy gợi ý tìm kiếm từ API
async function fetchSearchSuggestions(query) {
    try {
        const response = await fetch(`/api/search/suggestions?q=${encodeURIComponent(query)}`);
        const result = await response.json();
        
        if (result.success && result.data.length > 0) {
            showSuggestions(result.data);
        } else {
            hideSuggestions();
        }
    } catch (error) {
        console.error('Error fetching search suggestions:', error);
        hideSuggestions();
    }
}
```

#### 7.3. Hiển thị gợi ý
```javascript
// Function để hiển thị gợi ý
function showSuggestions(suggestions) {
    const suggestionsContainer = document.getElementById('searchSuggestions');
    suggestionsContainer.innerHTML = '';
    
    suggestions.forEach(suggestion => {
        const suggestionItem = document.createElement('div');
        suggestionItem.className = 'suggestion-item';
        suggestionItem.innerHTML = `<i class="fas fa-search"></i>${suggestion}`;
        suggestionItem.onclick = () => selectSuggestion(suggestion);
        suggestionsContainer.appendChild(suggestionItem);
    });
    
    suggestionsContainer.classList.add('show');
}
```

#### 7.4. Ẩn gợi ý
```javascript
// Function để ẩn gợi ý
function hideSuggestions() {
    const suggestionsContainer = document.getElementById('searchSuggestions');
    suggestionsContainer.classList.remove('show');
}
```

#### 7.5. Chọn gợi ý
```javascript
// Function để chọn gợi ý
function selectSuggestion(suggestion) {
    const searchInput = document.getElementById('searchInput');
    searchInput.value = suggestion;
    hideSuggestions();
    searchProducts();
}
```

#### 7.6. Tìm kiếm sản phẩm
```javascript
// Function để tìm kiếm sản phẩm
function searchProducts() {
    const searchTerm = document.querySelector('.search-box input').value;
    if (searchTerm.trim()) {
        // Redirect to search page with query parameter
        window.location.href = '/search?q=' + encodeURIComponent(searchTerm.trim());
    } else {
        // If no search term, redirect to catalog
        window.location.href = '/catalog';
    }
}
```

#### 7.7. Xử lý phím Enter
```javascript
// Function để tìm kiếm với Enter key
function handleSearchKeyPress(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        searchProducts();
    }
}
```

#### 7.8. Xử lý click nút tìm kiếm
```javascript
// Function để tìm kiếm với click
function handleSearchClick() {
    searchProducts();
}
```

#### 7.9. Event Listeners
```javascript
// Event listeners
document.addEventListener('DOMContentLoaded', function() {
    const searchButton = document.querySelector('.search-box button');
    if (searchButton) {
        searchButton.addEventListener('click', handleSearchClick);
    }

    const searchInput = document.querySelector('.search-box input');
    if (searchInput) {
        searchInput.addEventListener('keypress', handleSearchKeyPress);
    }
    
    // Hide suggestions when clicking outside
    document.addEventListener('click', function(event) {
        const searchBox = document.querySelector('.search-box');
        if (!searchBox.contains(event.target)) {
            hideSuggestions();
        }
    });
    
    // Cart count is automatically loaded by the topbar fragment
});
```

---

## 📊 TÓM TẮT LUỒNG HOẠT ĐỘNG

### Luồng 1: Auto-suggestions (Gợi ý tự động)
```
User nhập vào search box (>= 2 ký tự)
    ↓
handleSearchInput() → Debounce 300ms
    ↓
fetchSearchSuggestions() → GET /api/search/suggestions?q=...
    ↓
WebController.getSearchSuggestions()
    ↓
ProductService.searchProducts()
    ↓
ProductRepository.searchProductsByKeyword()
    ↓
Database Query (JPQL)
    ↓
Trả về 5 tên sản phẩm đầu tiên
    ↓
showSuggestions() → Hiển thị dropdown
```

### Luồng 2: Tìm kiếm chính
```
User nhấn Enter / Click button / Click suggestion
    ↓
searchProducts() → Redirect to /search?q=...
    ↓
WebController.searchProducts()
    ↓
ProductService.searchProducts()
    ↓
ProductRepository.searchProductsByKeyword()
    ↓
Database Query
    ↓
Áp dụng filters (priceRange, sort)
    ↓
Render template "catalog" với kết quả
```

---

## 🔍 CHI TIẾT QUERY DATABASE

### JPQL Query trong ProductRepository:
```sql
SELECT p 
FROM Product p 
LEFT JOIN FETCH p.vendor 
LEFT JOIN FETCH p.category 
WHERE (
    LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
    OR 
    LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
) 
AND p.status = :status
```

**Đặc điểm:**
- Tìm kiếm không phân biệt hoa thường (LOWER)
- Tìm trong cả `name` và `description`
- Chỉ lấy sản phẩm có status = ACTIVE
- Eager fetch vendor và category để tránh N+1 problem

---

## 📝 GHI CHÚ

1. **Debounce**: 300ms để giảm số lần gọi API khi user đang gõ
2. **Limit suggestions**: Chỉ hiển thị tối đa 5 gợi ý
3. **Auto-hide**: Suggestions tự động ẩn khi click bên ngoài
4. **Filter support**: Hỗ trợ lọc theo giá và sắp xếp
5. **Security**: Chỉ hiển thị sản phẩm ACTIVE
6. **Performance**: Sử dụng LEFT JOIN FETCH để tối ưu query

---

## 📂 DANH SÁCH FILE LIÊN QUAN

### Backend:
- `WebController.java` - Controller xử lý search
- `ProductService.java` - Interface service
- `ProductServiceImpl.java` - Implementation service
- `ProductRepository.java` - Repository với query search
- `ApiResponse.java` - DTO response

### Frontend:
- `fragments/header.html` - UI search box
- `home.html` - JavaScript xử lý search và CSS
- `catalog.html` - Template hiển thị kết quả search

