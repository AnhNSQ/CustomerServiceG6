package CustomerService.controller;

import CustomerService.dto.ApiResponse;
import CustomerService.dto.StaffResponse;
import CustomerService.dto.TicketAssignRequest;
import CustomerService.dto.TicketResponse;
import CustomerService.service.IStaffService;
import CustomerService.service.SessionManager;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class StaffController {

    private final IStaffService staffService;
    private final SessionManager sessionManager;

    /**
     * Lấy thông tin staff hiện tại
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
     * LEADER: Lấy danh sách ticket của phòng ban
     */
    @GetMapping("/leader/tickets")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getDepartmentTickets(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            // Kiểm tra quyền LEADER
            if (!staffService.isLeader(staffId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ LEADER mới có quyền truy cập"));
            }
            
            log.info("LEADER {} lấy danh sách ticket của phòng ban", staffId);
            
            List<TicketResponse> tickets = staffService.getTicketsByLeaderDepartment(staffId);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(tickets));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy ticket phòng ban: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy ticket phòng ban: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * LEADER: Lấy danh sách nhân viên trong phòng ban
     */
    @GetMapping("/leader/staff")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getDepartmentStaff(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            // Kiểm tra quyền LEADER
            if (!staffService.isLeader(staffId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ LEADER mới có quyền truy cập"));
            }
            
            log.info("LEADER {} lấy danh sách nhân viên trong phòng ban", staffId);
            
            List<StaffResponse> staff = staffService.getStaffByLeaderDepartment(staffId);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(staff));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy nhân viên phòng ban: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy nhân viên phòng ban: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * LEADER: Phân công ticket cho nhân viên
     */
    @PostMapping("/leader/tickets/{ticketId}/assign")
    public ResponseEntity<ApiResponse<String>> assignTicket(
            @PathVariable Long ticketId,
            @Valid @RequestBody TicketAssignRequest request,
            HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long leaderId = sessionManager.getStaffId(session);
            
            // Kiểm tra quyền LEADER
            if (!staffService.isLeader(leaderId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ LEADER mới có quyền phân công"));
            }
            
            log.info("LEADER {} phân công ticket {} cho staff {}", leaderId, ticketId, request.getStaffId());
            
            boolean assigned = staffService.assignTicketToStaff(ticketId, request.getStaffId(), leaderId, request.getNote());
            
            if (assigned) {
                return ResponseEntity.ok()
                    .body(ApiResponse.success("Phân công ticket thành công"));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không thể phân công ticket"));
            }
                
        } catch (RuntimeException e) {
            log.error("Lỗi phân công ticket: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi phân công ticket: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * STAFF: Lấy danh sách ticket được phân công
     */
    @GetMapping("/my-tickets")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getMyAssignedTickets(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            log.info("Staff {} lấy danh sách ticket được phân công", staffId);
            
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
}