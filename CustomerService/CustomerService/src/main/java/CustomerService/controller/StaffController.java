package CustomerService.controller;

import CustomerService.dto.ApiResponse;
import CustomerService.dto.StaffLoginRequest;
import CustomerService.dto.StaffResponse;
import CustomerService.exception.AuthenticationException;
import CustomerService.exception.UserNotFoundException;
import CustomerService.service.AuthenticationService;
import CustomerService.service.SessionManager;
import CustomerService.service.StaffService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffService staffService;
    private final AuthenticationService authenticationService;
    private final SessionManager sessionManager;

    /**
     * Đăng nhập staff
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<StaffResponse>> login(
            @Valid @RequestBody StaffLoginRequest request,
            HttpSession session) {
        try {
            log.info("Nhận yêu cầu đăng nhập staff từ: {}", request.getEmailOrUsername());
            
            // Sử dụng AuthenticationService để xác thực
            StaffResponse staff = authenticationService.authenticateStaff(request);
            
            // Lưu thông tin staff vào session
            sessionManager.setStaffSession(
                session, 
                staff.getStaffId(), 
                staff.getName(), 
                staff.getEmail(), 
                staff.getRoles()
            );
            
            log.info("Đăng nhập thành công cho staff ID: {}", staff.getStaffId());
            
            return ResponseEntity.ok()
                .body(ApiResponse.success("Đăng nhập thành công", staff));
                
        } catch (AuthenticationException e) {
            log.error("Lỗi xác thực staff: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi đăng nhập staff: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * Đăng xuất staff
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpSession session) {
        try {
            Long staffId = sessionManager.getStaffId(session);
            if (staffId != null) {
                log.info("Staff ID {} đăng xuất", staffId);
            }
            
            sessionManager.invalidateSession(session);

            return ResponseEntity.ok()
                .body(ApiResponse.success("Đăng xuất thành công", null));
                
        } catch (Exception e) {
            log.error("Lỗi khi đăng xuất staff: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra khi đăng xuất"));
        }
    }

    /**
     * Lấy thông tin staff hiện tại
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<StaffResponse>> getProfile(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Chưa đăng nhập"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            StaffResponse staff = staffService.findById(staffId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy thông tin staff"));
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(staff));
                
        } catch (UserNotFoundException e) {
            log.error("Lỗi lấy profile staff: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy profile staff: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * Lấy thông tin staff theo ID (chỉ dành cho admin)
     */
    @GetMapping("/{staffId}")
    public ResponseEntity<ApiResponse<StaffResponse>> getStaffById(
            @PathVariable Long staffId,
            HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Chưa đăng nhập"));
            }
            
            StaffResponse staff = staffService.findById(staffId)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy staff với ID: " + staffId));
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(staff));
                
        } catch (UserNotFoundException e) {
            log.error("Lỗi lấy thông tin staff: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy thông tin staff: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * Tìm staff theo email hoặc username
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<StaffResponse>> searchStaff(
            @RequestParam String emailOrUsername,
            HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Chưa đăng nhập"));
            }
            
            StaffResponse staff = staffService.findByEmailOrUsername(emailOrUsername)
                .orElseThrow(() -> new UserNotFoundException("Không tìm thấy staff với thông tin: " + emailOrUsername));
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(staff));
                
        } catch (UserNotFoundException e) {
            log.error("Lỗi tìm kiếm staff: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi tìm kiếm staff: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * Kiểm tra trạng thái đăng nhập của staff
     */
    @GetMapping("/auth-status")
    public ResponseEntity<ApiResponse<Boolean>> checkAuthStatus(HttpSession session) {
        try {
            boolean isLoggedIn = sessionManager.isStaffLoggedIn(session);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(isLoggedIn));
                
        } catch (Exception e) {
            log.error("Lỗi kiểm tra trạng thái đăng nhập: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra khi kiểm tra trạng thái đăng nhập"));
        }
    }
}
