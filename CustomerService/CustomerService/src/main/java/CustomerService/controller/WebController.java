package CustomerService.controller;

import CustomerService.dto.CustomerResponse;
import CustomerService.dto.StaffResponse;
import CustomerService.dto.CartResponse;
import CustomerService.entity.Product;
import CustomerService.entity.Category;
import CustomerService.service.CustomerService;
import CustomerService.service.StaffService;
import CustomerService.service.TicketService;
import CustomerService.service.ProductService;
import CustomerService.service.CategoryService;
import CustomerService.service.CartService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class WebController {

    private final CustomerService customerService;
    private final StaffService staffService;
    private final TicketService ticketService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;

    /**
     * Trang chủ - hiển thị sản phẩm từ database
     */
    @GetMapping("/")
    public String home(Model model) {
        return homePage(model);
    }

    /**
     * Trang chủ với endpoint /home
     */
    @GetMapping("/home")
    public String homePage(Model model) {
        try {
            log.info("Loading home page with products from database");
            
            // Lấy sản phẩm nổi bật từ database
            var featuredProducts = productService.getFeaturedProducts(8);
            model.addAttribute("featuredProducts", featuredProducts);
            
            // Lấy categories để hiển thị trong navigation
            var categories = categoryService.getAllActiveCategories();
            model.addAttribute("categories", categories);
            
            log.info("Home page loaded successfully with {} featured products and {} categories", 
                    featuredProducts.size(), categories.size());
            
            return "home";
            
        } catch (Exception e) {
            log.error("Error loading home page: ", e);
            // Fallback về trang login nếu có lỗi
            return "redirect:/login";
        }
    }

    /**
     * Trang đăng nhập
     */
    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        return "login";
    }

    /**
     * Trang đăng ký
     */
    @GetMapping("/register")
    public String registerPage(Model model, HttpSession session) {
        return "register";
    }

    /**
     * Trang dashboard - hiển thị thông tin profile
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                log.warn("Unauthorized access to dashboard - redirecting to login");
                return "redirect:/login";
            }
            
            log.info("Loading dashboard for customer ID: {}", customerId);
            
            // Lấy thông tin customer từ database
            CustomerResponse customer = customerService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin customer"));
            
            log.info("Customer found: {}", customer.getEmail());
            
            // Thêm thông tin vào model
            model.addAttribute("customer", customer);
            model.addAttribute("customerName", customer.getName());
            model.addAttribute("customerEmail", customer.getEmail());
            model.addAttribute("customerRoles", customer.getRoles());
            
            log.info("Customer {} accessed dashboard successfully", customerId);
            
            return "customer/dashboard";
            
        } catch (Exception e) {
            log.error("Error loading dashboard: ", e);
            return "redirect:/login";
        }
    }

    /**
     * Trang profile - chi tiết thông tin cá nhân
     */
    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                return "redirect:/login";
            }
            
            CustomerResponse customer = customerService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin customer"));
            
            model.addAttribute("customer", customer);
            
            return "customer/profile";
            
        } catch (Exception e) {
            log.error("Error loading profile: ", e);
            return "redirect:/login";
        }
    }

    /**
     * Trang đăng nhập staff
     */
    @GetMapping("/staff/login")
    public String staffLoginPage(Model model, HttpSession session) {
        return "staff/login";
    }

    /**
     * Trang dashboard staff
     */
    @GetMapping("/staff/dashboard")
    public String staffDashboard(Model model, HttpSession session) {
        try {
            Long staffId = (Long) session.getAttribute("staffId");
            
            if (staffId == null) {
                log.warn("Unauthorized access to staff dashboard - redirecting to login");
                return "redirect:/staff/login";
            }
            
            log.info("Loading staff dashboard for staff ID: {}", staffId);
            
            // Lấy thông tin staff từ database
            StaffResponse staff = staffService.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin staff"));
            
            log.info("Staff found: {}", staff.getEmail());
            
            // Thêm thông tin staff vào model
            model.addAttribute("staff", staff);
            model.addAttribute("staffName", staff.getName());
            model.addAttribute("staffEmail", staff.getEmail());
            model.addAttribute("staffRoles", staff.getRoles());

            // Thêm thống kê ticket vào model
            var stats = ticketService.getDashboardStats();
            model.addAttribute("totalTickets", stats.getTotalTickets());
            model.addAttribute("pendingTickets", stats.getPendingTickets());
            model.addAttribute("resolvedTickets", stats.getResolvedTickets());
            model.addAttribute("urgentTickets", stats.getUrgentTickets());

            // Ticket gần đây
            model.addAttribute("tickets", ticketService.getRecentTickets(5));
            
            log.info("Staff {} accessed dashboard successfully", staffId);
            
            return "staff/dashboard";
            
        } catch (Exception e) {
            log.error("Error loading staff dashboard: ", e);
            return "redirect:/staff/login";
        }
    }

    /**
     * Trang tất cả ticket cho staff
     */
    @GetMapping("/staff/tickets")
    public String staffTickets(Model model, HttpSession session) {
        try {
            Long staffId = (Long) session.getAttribute("staffId");
            if (staffId == null) {
                log.warn("Unauthorized access to staff tickets - redirecting to login");
                return "redirect:/staff/login";
            }

            // Optional: load staff info for header
            StaffResponse staff = staffService.findById(staffId)
                    .orElse(null);
            if (staff != null) {
                model.addAttribute("staff", staff);
                model.addAttribute("staffName", staff.getName());
                model.addAttribute("staffEmail", staff.getEmail());
                model.addAttribute("staffRoles", staff.getRoles());
            }

            return "staff/tickets";
        } catch (Exception e) {
            log.error("Error loading staff tickets page: ", e);
            return "redirect:/staff/login";
        }
    }

    /**
     * Trang chi tiết sản phẩm
     */
    @GetMapping("/product/{productId}")
    public String productDetail(@PathVariable Long productId, Model model, HttpSession session) {
        try {
            log.info("Loading product detail page for product ID: {}", productId);
            
            // Validate productId
            if (productId == null || productId <= 0) {
                log.warn("Invalid product ID: {}", productId);
                return "redirect:/home";
            }
            
            // Lấy thông tin sản phẩm với vendor và category
            var product = productService.findByIdWithDetails(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));
            
            // Kiểm tra sản phẩm có đang hoạt động không
            if (product.getStatus() != Product.ProductStatus.ACTIVE) {
                log.warn("Product {} is not active, redirecting to home", productId);
                return "redirect:/home";
            }
            
            // Lấy sản phẩm liên quan (cùng category) - với error handling
            List<Product> relatedProducts = List.of();
            try {
                relatedProducts = productService.findByCategory(product.getCategory().getCategoryId())
                    .stream()
                    .filter(p -> !p.getProductId().equals(productId))
                    .limit(4)
                    .toList();
            } catch (Exception e) {
                log.warn("Error loading related products: ", e);
            }
            
            // Lấy categories để hiển thị trong navigation - với error handling
            var categories = List.<Category>of();
            try {
                categories = categoryService.getAllActiveCategories();
            } catch (Exception e) {
                log.warn("Error loading categories: ", e);
            }
            
            // Thêm thông tin vào model
            model.addAttribute("product", product);
            model.addAttribute("relatedProducts", relatedProducts);
            model.addAttribute("categories", categories);
            model.addAttribute("isInStock", product.getQuantity() > 0);
            model.addAttribute("stockStatus", product.getQuantity() > 0 ? "In Stock" : "Out of Stock");
            
            log.info("Product detail page loaded successfully for: {}", product.getName());
            
            return "product-detail";
            
        } catch (Exception e) {
            log.error("Error loading product detail page for product ID {}: ", productId, e);
            return "redirect:/home";
        }
    }

    /**
     * Trang catalog - hiển thị tất cả sản phẩm
     */
    @GetMapping("/catalog")
    public String catalog(Model model, 
                          @RequestParam(required = false) String sort,
                          @RequestParam(required = false) String view,
                          @RequestParam(required = false) String priceRange) {
        try {
            log.info("Loading catalog page with sort: {}, view: {}, priceRange: {}", sort, view, priceRange);
            
            // Lấy tất cả sản phẩm đang hoạt động
            var allProducts = productService.getAllActiveProducts();
            
            // Áp dụng bộ lọc giá nếu có
            if (priceRange != null && !priceRange.isEmpty()) {
                allProducts = filterProductsByPriceRange(allProducts, priceRange);
            }
            
            // Áp dụng sắp xếp nếu có
            if (sort != null && !sort.isEmpty()) {
                allProducts = sortProducts(allProducts, sort);
            }
            
            // Lấy sản phẩm mới nhất cho sidebar
            var newReleases = productService.getLatestProducts(3);
            
            // Lấy categories để hiển thị trong navigation
            var categories = categoryService.getAllActiveCategories();
            
            // Thêm thông tin vào model
            model.addAttribute("products", allProducts);
            model.addAttribute("newReleases", newReleases);
            model.addAttribute("categories", categories);
            model.addAttribute("currentSort", sort != null ? sort : "default");
            model.addAttribute("currentView", view != null ? view : "grid");
            model.addAttribute("currentPriceRange", priceRange != null ? priceRange : "");
            model.addAttribute("totalProducts", allProducts.size());
            
            log.info("Catalog page loaded successfully with {} products", allProducts.size());
            
            return "catalog";
            
        } catch (Exception e) {
            log.error("Error loading catalog page: ", e);
            return "redirect:/home";
        }
    }

    /**
     * Trang catalog theo category
     */
    @GetMapping("/category/{categoryId}")
    public String catalogByCategory(@PathVariable Long categoryId, Model model,
                                    @RequestParam(required = false) String sort,
                                    @RequestParam(required = false) String view,
                                    @RequestParam(required = false) String priceRange) {
        try {
            log.info("Loading catalog page for category ID: {} with sort: {}, view: {}, priceRange: {}", 
                    categoryId, sort, view, priceRange);
            
            // Lấy thông tin category
            var category = categoryService.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy category với ID: " + categoryId));
            
            // Lấy sản phẩm theo category
            var products = productService.findByCategory(categoryId);
            
            // Áp dụng bộ lọc giá nếu có
            if (priceRange != null && !priceRange.isEmpty()) {
                products = filterProductsByPriceRange(products, priceRange);
            }
            
            // Áp dụng sắp xếp nếu có
            if (sort != null && !sort.isEmpty()) {
                products = sortProducts(products, sort);
            }
            
            // Lấy sản phẩm mới nhất cho sidebar
            var newReleases = productService.getLatestProducts(3);
            
            // Lấy categories để hiển thị trong navigation
            var categories = categoryService.getAllActiveCategories();
            
            // Thêm thông tin vào model
            model.addAttribute("products", products);
            model.addAttribute("newReleases", newReleases);
            model.addAttribute("categories", categories);
            model.addAttribute("currentCategory", category);
            model.addAttribute("currentSort", sort != null ? sort : "default");
            model.addAttribute("currentView", view != null ? view : "grid");
            model.addAttribute("currentPriceRange", priceRange != null ? priceRange : "");
            model.addAttribute("totalProducts", products.size());
            
            log.info("Category catalog page loaded successfully for {} with {} products", 
                    category.getName(), products.size());
            
            return "catalog";
            
        } catch (Exception e) {
            log.error("Error loading category catalog page: ", e);
            return "redirect:/catalog";
        }
    }

    /**
     * Bộ lọc sản phẩm theo khoảng giá
     */
    private List<Product> filterProductsByPriceRange(List<Product> products, String priceRange) {
        return products.stream().filter(product -> {
            double price = product.getPrice().doubleValue();
            switch (priceRange) {
                case "0-24":
                    return price >= 0 && price <= 24.99;
                case "25-49":
                    return price >= 25 && price <= 49.99;
                case "50+":
                    return price >= 50;
                default:
                    return true;
            }
        }).toList();
    }

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

    /**
     * Trang giỏ hàng
     */
    @GetMapping("/cart")
    public String cartPage(Model model, HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                log.warn("Unauthorized access to cart - redirecting to login");
                return "redirect:/login";
            }
            
            log.info("Loading cart page for customer ID: {}", customerId);
            
            // Lấy thông tin customer
            CustomerResponse customer = customerService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin customer"));
            
            // Lấy thông tin giỏ hàng
            CartResponse cart = cartService.getCart(customerId);
            
            model.addAttribute("customer", customer);
            model.addAttribute("customerName", customer.getName());
            model.addAttribute("cart", cart);
            model.addAttribute("cartItems", cart.getCartItems());
            model.addAttribute("totalItems", cart.getTotalItems());
            model.addAttribute("totalAmount", cart.getTotalAmount());
            model.addAttribute("shippingCost", cart.getShippingCost());
            model.addAttribute("grandTotal", cart.getGrandTotal());
            
            return "cart";
            
        } catch (Exception e) {
            log.error("Error loading cart page: ", e);
            return "redirect:/login";
        }
    }
}
