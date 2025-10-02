package CustomerService.controller;

import CustomerService.dto.CustomerResponse;
import CustomerService.service.CustomerService;
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
            log.info("Loading profile page...");
            Long customerId = (Long) session.getAttribute("customerId");
            log.info("Customer ID from session: {}", customerId);
            
            if (customerId == null) {
                log.warn("No customer ID in session, redirecting to login");
                return "redirect:/login";
            }
            
            log.info("Looking for customer with ID: {}", customerId);
            CustomerResponse customer = customerService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin customer với ID: " + customerId));
            
            log.info("Customer found: {}", customer.getEmail());
            model.addAttribute("customer", customer);
            
            return "customer/profile";
            
        } catch (RuntimeException e) {
            log.error("Runtime error loading profile: {}", e.getMessage());
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Unexpected error loading profile: ", e);
            return "redirect:/login";
        }
    }

    /**
     * Trang edit profile - chỉnh sửa thông tin cá nhân
     */
    @GetMapping("/customer/edit-profile")
    public String editProfile(Model model, HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                return "redirect:/login";
            }
            
            CustomerResponse customer = customerService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin customer"));
            
            model.addAttribute("customer", customer);
            
            return "customer/edit-profile";
            
        } catch (Exception e) {
            log.error("Error loading edit profile: ", e);
            return "redirect:/login";
        }
    }
}
