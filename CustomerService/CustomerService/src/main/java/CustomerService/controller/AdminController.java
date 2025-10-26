package CustomerService.controller;

import CustomerService.dto.*;
import CustomerService.entity.StaffDepartment;
import CustomerService.service.AdminService;
import CustomerService.service.SessionManager;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller cho ADMIN role - xử lý nghiệp vụ của ADMIN
 * Tuân thủ Single Responsibility Principle (SRP)
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final SessionManager sessionManager;

    /**
     * ADMIN: Lấy tất cả ticket trong hệ thống
     */
    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getAllTickets(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            // TODO: Kiểm tra quyền ADMIN (cần thêm role ADMIN)
            // if (!adminService.isAdmin(staffId)) {
            //     return ResponseEntity.status(HttpStatus.FORBIDDEN)
            //         .body(ApiResponse.error("Chỉ ADMIN mới có quyền truy cập"));
            // }
            
            log.info("ADMIN {} lấy tất cả ticket trong hệ thống", staffId);
            
            List<TicketResponse> tickets = adminService.getAllTickets();
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(tickets));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy tất cả ticket: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy tất cả ticket: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * ADMIN: Lấy thông tin ticket theo ID
     */
    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketById(
            @PathVariable Long ticketId,
            HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            // TODO: Kiểm tra quyền ADMIN (cần thêm role ADMIN)
            // if (!adminService.isAdmin(staffId)) {
            //     return ResponseEntity.status(HttpStatus.FORBIDDEN)
            //         .body(ApiResponse.error("Chỉ ADMIN mới có quyền truy cập"));
            // }
            
            log.info("ADMIN {} lấy thông tin ticket {}", staffId, ticketId);
            
            Optional<TicketResponse> ticket = adminService.getTicketById(ticketId);
            
            if (ticket.isPresent()) {
                return ResponseEntity.ok()
                    .body(ApiResponse.success(ticket.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy ticket với ID: " + ticketId));
            }
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy ticket: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy ticket: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * ADMIN: Lấy thống kê tổng quan hệ thống
     */
    @GetMapping("/stats/global")
    public ResponseEntity<ApiResponse<TicketDashboardStats>> getGlobalStats(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            // TODO: Kiểm tra quyền ADMIN (cần thêm role ADMIN)
            // if (!adminService.isAdmin(staffId)) {
            //     return ResponseEntity.status(HttpStatus.FORBIDDEN)
            //         .body(ApiResponse.error("Chỉ ADMIN mới có quyền truy cập"));
            // }
            
            log.info("ADMIN {} lấy thống kê tổng quan hệ thống", staffId);
            
            TicketDashboardStats stats = adminService.getGlobalDashboardStats();
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(stats));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy thống kê tổng quan: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy thống kê tổng quan: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * ADMIN: Lấy tất cả phòng ban
     */
    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<StaffDepartment>>> getAllDepartments(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            // TODO: Kiểm tra quyền ADMIN (cần thêm role ADMIN)
            // if (!adminService.isAdmin(staffId)) {
            //     return ResponseEntity.status(HttpStatus.FORBIDDEN)
            //         .body(ApiResponse.error("Chỉ ADMIN mới có quyền truy cập"));
            // }
            
            log.info("ADMIN {} lấy tất cả phòng ban", staffId);
            
            List<StaffDepartment> departments = adminService.getAllDepartments();
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(departments));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy phòng ban: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy phòng ban: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * ADMIN: Lấy tất cả staff trong hệ thống
     */
    @GetMapping("/staff")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getAllStaff(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            // TODO: Kiểm tra quyền ADMIN (cần thêm role ADMIN)
            // if (!adminService.isAdmin(staffId)) {
            //     return ResponseEntity.status(HttpStatus.FORBIDDEN)
            //         .body(ApiResponse.error("Chỉ ADMIN mới có quyền truy cập"));
            // }
            
            log.info("ADMIN {} lấy tất cả staff trong hệ thống", staffId);
            
            List<StaffResponse> staff = adminService.getAllStaff();
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(staff));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy staff: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy staff: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }
}
