package CustomerService.controller;

import CustomerService.dto.*;
import CustomerService.exception.AuthenticationException;
import CustomerService.service.AuthenticationService;
import CustomerService.service.StaffService;
import CustomerService.service.SessionManager;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

/**
 * Controller cho STAFF role - xử lý nghiệp vụ của STAFF
 * Tuân thủ Single Responsibility Principle (SRP)
 */
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StaffController {

    private final StaffService staffService;
    private final SessionManager sessionManager;
    private final AuthenticationService authenticationService;

    /**
     * Đăng nhập staff
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
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

            log.info("Đăng nhập thành công staff với ID: {}", staff.getStaffId());

            // Xác định URL redirect dựa trên role
            String redirectUrl = determineRedirectUrl(staff.getRoles());
            log.info("Staff {} sẽ được redirect đến: {}", staff.getStaffId(), redirectUrl);

            // Tạo response object bao gồm redirectUrl
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("staff", staff);
            responseData.put("redirectUrl", redirectUrl);

            return ResponseEntity.ok()
                .body(ApiResponse.success(responseData, "Đăng nhập thành công"));

        } catch (AuthenticationException e) {
            log.warn("Đăng nhập thất bại cho staff: {}", request.getEmailOrUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Email/Username hoặc mật khẩu không đúng"));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi đăng nhập staff: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * Xác định URL redirect dựa trên role của staff
     */
    private String determineRedirectUrl(Set<String> roles) {
        if (roles.contains("ADMIN")) {
            return "/admin/dashboard";
        } else if (roles.contains("LEAD")) {
            return "/leader/dashboard";
        } else {
            return "/staff/dashboard"; // STAFF
        }
    }

    /**
     * Đăng xuất staff
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpSession session) {
        try {
            session.invalidate();
            log.info("Staff đăng xuất thành công");
            
            return ResponseEntity.ok()
                .body(ApiResponse.success("Đăng xuất thành công"));
                
        } catch (Exception e) {
            log.error("Lỗi khi đăng xuất staff: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra khi đăng xuất"));
        }
    }

    /**
     * Lấy thông tin profile của staff hiện tại
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<StaffResponse>> getProfile(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            StaffResponse staff = staffService.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin staff"));
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(staff));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy profile: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy profile: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * STAFF: Lấy danh sách ticket được phân công
     */
    @GetMapping("/tickets/assigned")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getAssignedTickets(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            log.info("STAFF {} lấy danh sách ticket được phân công", staffId);
            
            List<TicketResponse> tickets = staffService.getTicketsAssignedToStaff(staffId);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(tickets));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy ticket được phân công: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy ticket được phân công: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * STAFF: Lấy thông tin ticket được phân công theo ID
     */
    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<ApiResponse<TicketResponse>> getAssignedTicketById(
            @PathVariable Long ticketId,
            HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            log.info("STAFF {} lấy thông tin ticket {} được phân công", staffId, ticketId);
            
            // Lấy danh sách ticket được phân công
            List<TicketResponse> assignedTickets = staffService.getTicketsAssignedToStaff(staffId);
            
            // Tìm ticket trong danh sách được phân công
            Optional<TicketResponse> ticket = assignedTickets.stream()
                .filter(t -> t.getTicketId().equals(ticketId))
                .findFirst();
            
            if (ticket.isPresent()) {
                return ResponseEntity.ok()
                    .body(ApiResponse.success(ticket.get()));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bạn không có quyền xem ticket này hoặc ticket không tồn tại"));
            }
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy ticket được phân công: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy ticket được phân công: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * STAFF: Lấy thống kê ticket được phân công
     */
    @GetMapping("/stats/assigned")
    public ResponseEntity<ApiResponse<TicketDashboardStats>> getAssignedStats(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            log.info("STAFF {} lấy thống kê ticket được phân công", staffId);
            
            TicketDashboardStats stats = staffService.getAssignedTicketStats(staffId);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(stats));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy thống kê ticket được phân công: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy thống kê ticket được phân công: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }
}