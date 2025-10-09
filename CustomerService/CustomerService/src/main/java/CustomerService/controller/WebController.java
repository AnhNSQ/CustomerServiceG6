package CustomerService.controller;

import CustomerService.dto.CustomerResponse;
import CustomerService.dto.StaffResponse;
import CustomerService.service.CustomerService;
import CustomerService.service.StaffService;
import CustomerService.service.TicketService;
import CustomerService.service.ProductService;
import CustomerService.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        return "customer/register";
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
}
