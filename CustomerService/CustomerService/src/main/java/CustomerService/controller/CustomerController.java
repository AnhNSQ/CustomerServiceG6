package CustomerService.controller;

import CustomerService.dto.ApiResponse;
import CustomerService.dto.CustomerLoginRequest;
import CustomerService.dto.CustomerRegisterRequest;
import CustomerService.dto.CustomerResponse;
import CustomerService.dto.CustomerUpdateRequest;
import CustomerService.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Đăng ký tài khoản customer mới
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CustomerResponse>> register(
            @Valid @RequestBody CustomerRegisterRequest request) {
        try {
            log.info("Nhận yêu cầu đăng ký từ email: {}", request.getEmail());
            
            CustomerResponse customer = customerService.register(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký thành công", customer));
                
        } catch (RuntimeException e) {
            log.error("Lỗi đăng ký: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi đăng ký: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * Đăng nhập customer
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<CustomerResponse>> login(
            @Valid @RequestBody CustomerLoginRequest request,
            HttpSession session) {
        try {
            log.info("Nhận yêu cầu đăng nhập từ: {}", request.getEmailOrUsername());
            
            CustomerResponse customer = customerService.login(request);
            
            // Lưu thông tin customer vào session
            try {
                session.setAttribute("customerId", customer.getCustomerId());
                session.setAttribute("customerName", customer.getName());
                session.setAttribute("customerEmail", customer.getEmail());
                session.setAttribute("customerRoles", customer.getRoles());
                log.info("Session attributes set successfully");
            } catch (Exception sessionError) {
                log.error("Error setting session attributes: ", sessionError);
                // Vẫn trả về success nhưng không có session
            }
            
            log.info("Đăng nhập thành công cho customer ID: {}", customer.getCustomerId());
            
            return ResponseEntity.ok()
                .body(ApiResponse.success("Đăng nhập thành công", customer));
                
        } catch (RuntimeException e) {
            log.error("Lỗi đăng nhập: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi đăng nhập: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * Đăng xuất
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            if (customerId != null) {
                log.info("Customer ID {} đăng xuất", customerId);
            }
            
            // Xóa session
            session.invalidate();
            
            return ResponseEntity.ok()
                .body(ApiResponse.success("Đăng xuất thành công", null));
                
        } catch (Exception e) {
            log.error("Lỗi khi đăng xuất: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra khi đăng xuất"));
        }
    }

    /**
     * Lấy thông tin customer hiện tại
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<CustomerResponse>> getProfile(HttpSession session) {
        try {
            log.info("API: Getting profile...");
            Long customerId = (Long) session.getAttribute("customerId");
            log.info("API: Customer ID from session: {}", customerId);
            
            if (customerId == null) {
                log.warn("API: No customer ID in session");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Chưa đăng nhập"));
            }
            
            log.info("API: Looking for customer with ID: {}", customerId);
            CustomerResponse customer = customerService.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin customer với ID: " + customerId));
            
            log.info("API: Customer found: {}", customer.getEmail());
            return ResponseEntity.ok()
                .body(ApiResponse.success(customer));
                
        } catch (RuntimeException e) {
            log.error("API: Runtime error getting profile: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("API: Unexpected error getting profile: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        try {
            boolean exists = customerService.existsByEmail(email);
            return ResponseEntity.ok()
                .body(ApiResponse.success(exists));
        } catch (Exception e) {
            log.error("Lỗi kiểm tra email: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra khi kiểm tra email"));
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<ApiResponse<Boolean>> checkUsername(@RequestParam String username) {
        try {
            boolean exists = customerService.existsByUsername(username);
            return ResponseEntity.ok()
                .body(ApiResponse.success(exists));
        } catch (Exception e) {
            log.error("Lỗi kiểm tra username: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra khi kiểm tra username"));
        }
    }

    /**
     * Kiểm tra session hiện tại
     */
    @GetMapping("/session-check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkSession(HttpSession session) {
        try {
            Map<String, Object> sessionInfo = new HashMap<>();
            Long customerId = (Long) session.getAttribute("customerId");
            String customerName = (String) session.getAttribute("customerName");
            String customerEmail = (String) session.getAttribute("customerEmail");
            
            sessionInfo.put("customerId", customerId);
            sessionInfo.put("customerName", customerName);
            sessionInfo.put("customerEmail", customerEmail);
            sessionInfo.put("sessionId", session.getId());
            sessionInfo.put("isValid", customerId != null);
            
            log.info("Session check - Customer ID: {}, Name: {}, Email: {}", customerId, customerName, customerEmail);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success("Session info", sessionInfo));
                
        } catch (Exception e) {
            log.error("Lỗi kiểm tra session: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra khi kiểm tra session"));
        }
    }

    /**
     * Cập nhật thông tin customer
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateProfile(
            @Valid @RequestBody CustomerUpdateRequest request,
            HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Chưa đăng nhập"));
            }
            
            log.info("Nhận yêu cầu cập nhật thông tin từ customer ID: {}", customerId);
            
            CustomerResponse updatedCustomer = customerService.updateProfile(customerId, request);
            
            // Cập nhật session với thông tin mới
            session.setAttribute("customerName", updatedCustomer.getName());
            session.setAttribute("customerEmail", updatedCustomer.getEmail());
            
            return ResponseEntity.ok()
                .body(ApiResponse.success("Cập nhật thông tin thành công", updatedCustomer));
                
        } catch (RuntimeException e) {
            log.error("Lỗi cập nhật thông tin: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi cập nhật thông tin: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }
}
