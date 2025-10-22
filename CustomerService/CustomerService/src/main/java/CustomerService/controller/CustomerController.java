package CustomerService.controller;

import CustomerService.dto.ApiResponse;
import CustomerService.dto.CustomerLoginRequest;
import CustomerService.dto.CustomerRegisterRequest;
import CustomerService.dto.CustomerResponse;
import CustomerService.exception.AuthenticationException;
import CustomerService.exception.UserNotFoundException;
import CustomerService.service.AuthenticationService;
import CustomerService.service.CustomerService;
import CustomerService.service.SessionManager;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;
    private final AuthenticationService authenticationService;
    private final SessionManager sessionManager;

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
                .body(ApiResponse.success(customer, "Đăng ký thành công"));
                
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
            
            // Sử dụng AuthenticationService để xác thực
            CustomerResponse customer = authenticationService.authenticateCustomer(request);
            
            // Lưu thông tin customer vào session
            sessionManager.setCustomerSession(
                session, 
                customer.getCustomerId(), 
                customer.getName(), 
                customer.getEmail(), 
                customer.getRoles()
            );
            
            log.info("Đăng nhập thành công cho customer ID: {}", customer.getCustomerId());
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(customer, "Đăng nhập thành công"));
                
        } catch (AuthenticationException e) {
            log.error("Lỗi xác thực: {}", e.getMessage());
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
            Long customerId = sessionManager.getCustomerId(session);
            if (customerId != null) {
                log.info("Customer ID {} đăng xuất", customerId);
            }
            
            sessionManager.invalidateSession(session);

            return ResponseEntity.ok()
                .body(ApiResponse.success(null, "Đăng xuất thành công"));
                
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
            if (!sessionManager.isCustomerLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Chưa đăng nhập"));
            }
            
            Long customerId = sessionManager.getCustomerId(session);
            CustomerResponse customer = customerService.findById(customerId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy thông tin customer"));
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(customer));
                
        } catch (UserNotFoundException e) {
            log.error("Lỗi lấy profile: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy profile: ", e);
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
     * Cập nhật thông tin customer
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateProfile(
            @RequestBody Map<String, Object> updateData,
            HttpSession session) {
        try {
            if (!sessionManager.isCustomerLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Chưa đăng nhập"));
            }
            
            Long customerId = sessionManager.getCustomerId(session);
            log.info("Nhận yêu cầu cập nhật profile cho customer ID: {}", customerId);
            
            CustomerResponse updatedCustomer = customerService.updateProfile(customerId, updateData);
            
            log.info("Cập nhật profile thành công cho customer ID: {}", customerId);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(updatedCustomer, "Cập nhật thông tin thành công"));
                
        } catch (UserNotFoundException e) {
            log.error("Lỗi cập nhật profile: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi cập nhật profile: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }
}
