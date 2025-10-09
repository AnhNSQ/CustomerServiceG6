package CustomerService.controller;

import CustomerService.dto.CustomerResponse;
import CustomerService.entity.Order;
import CustomerService.repository.OrderRepository;
import CustomerService.service.CustomerService;
import CustomerService.service.ITicketService;
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
    private final ITicketService ticketService;
    private final OrderRepository orderRepository;

    /**
     * Trang chủ - redirect đến login
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
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
     * Trang dashboard customer - hiển thị thông tin profile
     */
    @GetMapping("/dashboard")
    public String customerDashboard(Model model, HttpSession session) {
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
            
            // Lấy danh sách ticket của customer
            var tickets = ticketService.getTicketsByCustomerId(customerId);
            
            // Thêm thông tin vào model
            model.addAttribute("customer", customer);
            model.addAttribute("customerName", customer.getName());
            model.addAttribute("customerEmail", customer.getEmail());
            model.addAttribute("customerRoles", customer.getRoles());
            model.addAttribute("tickets", tickets);
            
            // Thống kê ticket
            long totalTickets = tickets.size();
            long resolvedTickets = tickets.stream().filter(t -> "RESOLVED".equals(t.getStatus())).count();
            long pendingTickets = tickets.stream().filter(t -> "OPEN".equals(t.getStatus()) || "ASSIGNED".equals(t.getStatus()) || "IN_PROGRESS".equals(t.getStatus())).count();
            long closedTickets = tickets.stream().filter(t -> "CLOSED".equals(t.getStatus())).count();
            
            model.addAttribute("totalTickets", totalTickets);
            model.addAttribute("resolvedTickets", resolvedTickets);
            model.addAttribute("pendingTickets", pendingTickets);
            model.addAttribute("closedTickets", closedTickets);
            
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
    @GetMapping("customer/profile")
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
     * Trang tạo ticket mới
     */
    @GetMapping("/customer/tickets/create")
    public String createTicketPage(Model model, HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                log.warn("Unauthorized access to create ticket - redirecting to login");
                return "redirect:/login";
            }
            
            log.info("Loading create ticket page for customer ID: {}", customerId);
            
            // Lấy danh sách đơn hàng của customer
            var orders = orderRepository.findByCustomerIdOrderByOrderDateDesc(customerId);
            
            if (orders.isEmpty()) {
                log.warn("Customer {} has no orders - redirecting to dashboard", customerId);
                model.addAttribute("error", "Bạn cần có ít nhất một đơn hàng để tạo ticket hỗ trợ");
                return "customer/dashboard";
            }
            
            // Lấy thông tin customer
            CustomerResponse customer = customerService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin customer"));
            
            model.addAttribute("customer", customer);
            model.addAttribute("orders", orders);
            
            log.info("Create ticket page loaded successfully for customer {}", customerId);
            
            return "customer/create-ticket";
            
        } catch (Exception e) {
            log.error("Error loading create ticket page: ", e);
            return "customer/dashboard";
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
     * Trang dashboard staff - DISABLED (chỉ làm cho customer)
     */
    @GetMapping("/staff/dashboard")
    public String staffDashboard(Model model, HttpSession session) {
        return "redirect:/staff/login";
    }

    /**
     * Trang tất cả ticket cho staff - DISABLED (chỉ làm cho customer)
     */
    @GetMapping("/staff/tickets")
    public String staffTickets(Model model, HttpSession session) {
        return "redirect:/staff/login";
    }
}
