package CustomerService.controller;

import CustomerService.dto.CustomerResponse;
import CustomerService.dto.StaffResponse;
import CustomerService.service.CustomerService;
import CustomerService.service.StaffService;
import CustomerService.service.ProductService;
import CustomerService.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class WebController {

    private final CustomerService customerService;
    private final StaffService staffService;
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

            log.info("Staff {} accessed dashboard successfully", staffId);
            
            return "staff/dashboard";
            
        } catch (Exception e) {
            log.error("Error loading staff dashboard: ", e);
            return "redirect:/staff/login";
        }
    }

    /**
     * Trang tạo ticket mới của customer
     */
    @GetMapping("/customer/tickets/create")
    public String createTicket(Model model, HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                log.warn("Unauthorized access to create ticket - redirecting to login");
                return "redirect:/login";
            }
            
            log.info("Loading create ticket page for customer {}", customerId);
            
            // Lấy thông tin customer từ database
            CustomerResponse customer = customerService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin customer"));
            
            model.addAttribute("customer", customer);
            model.addAttribute("customerName", customer.getName());
            model.addAttribute("customerEmail", customer.getEmail());
            
            return "customer/create-ticket";
            
        } catch (Exception e) {
            log.error("Error loading create ticket page: ", e);
            return "redirect:/login";
        }
    }

    /**
     * Trang danh sách ticket của customer
     */
    @GetMapping("/customer/tickets")
    public String customerTickets(Model model, HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                log.warn("Unauthorized access to customer tickets - redirecting to login");
                return "redirect:/login";
            }
            
            log.info("Loading tickets page for customer {}", customerId);
            
            // Lấy thông tin customer từ database
            CustomerResponse customer = customerService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin customer"));
            
            model.addAttribute("customer", customer);
            model.addAttribute("customerName", customer.getName());
            model.addAttribute("customerEmail", customer.getEmail());
            
            return "customer/my-tickets";
            
        } catch (Exception e) {
            log.error("Error loading customer tickets page: ", e);
            return "redirect:/login";
        }
    }

    /**
     * Trang chi tiết ticket của customer
     */
    @GetMapping("/customer/tickets/{ticketId}")
    public String viewTicket(@PathVariable Long ticketId, Model model, HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                log.warn("Unauthorized access to ticket view - redirecting to login");
                return "redirect:/login";
            }
            
            log.info("Loading ticket {} for customer {}", ticketId, customerId);
            
            // Lấy thông tin ticket từ API
            model.addAttribute("ticketId", ticketId);
            model.addAttribute("customerId", customerId);
            
            return "customer/ticket-detail";
            
        } catch (Exception e) {
            log.error("Error loading ticket detail page: ", e);
            return "redirect:/customer/tickets";
        }
    }

    // ==================== LEADER PAGES ====================

    /**
     * Leader Dashboard
     */
    @GetMapping("/leader/dashboard")
    public String leaderDashboard(Model model, HttpSession session) {
        try {
            Long staffId = (Long) session.getAttribute("staffId");
            
            if (staffId == null) {
                log.warn("Unauthorized access to leader dashboard - redirecting to staff login");
                return "redirect:/staff/login";
            }
            
            log.info("Loading leader dashboard for staff {}", staffId);
            
            // Lấy thông tin staff từ database
            StaffResponse staff = staffService.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin staff"));
            
            model.addAttribute("staff", staff);
            model.addAttribute("staffName", staff.getName());
            model.addAttribute("staffEmail", staff.getEmail());
            
            return "leader/dashboard";
            
        } catch (Exception e) {
            log.error("Error loading leader dashboard: ", e);
            return "redirect:/staff/login";
        }
    }

    /**
     * Leader Tickets Management
     */
    @GetMapping("/leader/tickets")
    public String leaderTickets(Model model, HttpSession session) {
        try {
            Long staffId = (Long) session.getAttribute("staffId");
            
            if (staffId == null) {
                log.warn("Unauthorized access to leader tickets - redirecting to staff login");
                return "redirect:/staff/login";
            }
            
            log.info("Loading leader tickets page for staff {}", staffId);
            
            // Lấy thông tin staff từ database
            StaffResponse staff = staffService.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin staff"));
            
            model.addAttribute("staff", staff);
            model.addAttribute("staffName", staff.getName());
            model.addAttribute("staffEmail", staff.getEmail());
            
            return "leader/tickets";
            
        } catch (Exception e) {
            log.error("Error loading leader tickets page: ", e);
            return "redirect:/staff/login";
        }
    }

    /**
     * Leader Staff Management
     */
    @GetMapping("/leader/staff")
    public String leaderStaff(Model model, HttpSession session) {
        try {
            Long staffId = (Long) session.getAttribute("staffId");
            
            if (staffId == null) {
                log.warn("Unauthorized access to leader staff - redirecting to staff login");
                return "redirect:/staff/login";
            }
            
            log.info("Loading leader staff page for staff {}", staffId);
            
            // Lấy thông tin staff từ database
            StaffResponse staff = staffService.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin staff"));
            
            model.addAttribute("staff", staff);
            model.addAttribute("staffName", staff.getName());
            model.addAttribute("staffEmail", staff.getEmail());
            
            return "leader/staff";
            
        } catch (Exception e) {
            log.error("Error loading leader staff page: ", e);
            return "redirect:/staff/login";
        }
    }
}
