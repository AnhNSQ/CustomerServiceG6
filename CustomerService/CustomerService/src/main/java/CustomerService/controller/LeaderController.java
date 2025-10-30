package CustomerService.controller;

import CustomerService.dto.*;
import CustomerService.entity.Staff;
import CustomerService.repository.StaffRepository;
import CustomerService.service.LeaderService;
import CustomerService.service.EvaluationService;
import CustomerService.service.SessionManager;
import CustomerService.service.UserConverter;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Controller cho LEADER role - xử lý nghiệp vụ của LEADER
 * Tuân thủ Single Responsibility Principle (SRP)
 */
@RestController
@RequestMapping("/api/leaders")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LeaderController {

    private final LeaderService leaderService;
    private final EvaluationService evaluationService;
    private final SessionManager sessionManager;
    private final StaffRepository staffRepository;
    private final UserConverter userConverter;

    /**
     * LEADER: Lấy danh sách ticket của phòng ban
     */
    @GetMapping("/tickets/department")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getDepartmentTickets(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);

            // Kiểm tra quyền LEADER từ database
            Staff staff = staffRepository.findByIdWithRole(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
            
            Set<String> roles = userConverter.extractRoleNames(staff.getRole());
            if (!roles.contains("LEAD")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ LEADER mới có quyền truy cập"));
            }
            
            log.info("LEADER {} lấy danh sách ticket của phòng ban", staffId);
            
            List<TicketResponse> tickets = leaderService.getTicketsByLeaderDepartment(staffId);
            
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
     * LEADER: Lấy danh sách ticket OPEN của phòng ban (để phân công)
     */
    @GetMapping("/tickets/department/open")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getOpenDepartmentTickets(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);

            // Kiểm tra quyền LEADER từ database
            Staff staff = staffRepository.findByIdWithRole(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
            
            Set<String> roles = userConverter.extractRoleNames(staff.getRole());
            if (!roles.contains("LEAD")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ LEADER mới có quyền truy cập"));
            }
            
            log.info("LEADER {} lấy danh sách ticket OPEN của phòng ban", staffId);
            
            List<TicketResponse> tickets = leaderService.getOpenTicketsByLeaderDepartment(staffId);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(tickets));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy ticket OPEN phòng ban: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy ticket OPEN phòng ban: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * LEADER: Lấy danh sách nhân viên trong phòng ban
     */
    @GetMapping("/staffs/department")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getDepartmentStaff(HttpSession session) {
        try {
            log.info("API /leaders/staff/department called - checking authentication");
            
            if (!sessionManager.isStaffLoggedIn(session)) {
                log.warn("Staff not logged in - returning 401");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            log.info("Staff ID from session: {}", staffId);
            
            // Kiểm tra quyền LEADER từ database
            Staff leaderStaff = staffRepository.findByIdWithRole(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
            
            Set<String> roles = userConverter.extractRoleNames(leaderStaff.getRole());
            log.info("Staff {} roles: {}", staffId, roles);
            
            if (!roles.contains("LEAD")) {
                log.warn("Staff {} is not a leader - returning 403", staffId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ LEADER mới có quyền truy cập"));
            }
            
            log.info("LEADER {} lấy danh sách nhân viên trong phòng ban", staffId);
            
            List<StaffResponse> staff = leaderService.getStaffByLeaderDepartment(staffId);
            log.info("Found {} staff members for leader {}", staff.size(), staffId);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(staff));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy nhân viên phòng ban: {}", e.getMessage(), e);
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
    @PostMapping("/tickets/{ticketId}/assign")
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
            
            // Kiểm tra quyền LEADER từ database
            Staff staff = staffRepository.findByIdWithRole(leaderId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
            
            Set<String> roles = userConverter.extractRoleNames(staff.getRole());
            if (!roles.contains("LEAD")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ LEADER mới có quyền phân công"));
            }
            
            log.info("LEADER {} phân công ticket {} cho staff {}", leaderId, ticketId, request.getStaffId());
            
            boolean assigned = leaderService.assignTicketToStaff(ticketId, request.getStaffId(), leaderId);
            
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
     * LEADER: Lấy thông tin ticket của phòng ban theo ID
     */
    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<ApiResponse<TicketResponse>> getDepartmentTicketById(
            @PathVariable Long ticketId,
            HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);

            // Kiểm tra quyền LEADER từ database
            Staff staff = staffRepository.findByIdWithRole(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
            
            Set<String> roles = userConverter.extractRoleNames(staff.getRole());
            if (!roles.contains("LEAD")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ LEADER mới có quyền truy cập"));
            }
            
            log.info("LEADER {} lấy thông tin ticket {} của phòng ban", staffId, ticketId);
            
            // Lấy danh sách ticket của phòng ban
            List<TicketResponse> departmentTickets = leaderService.getTicketsByLeaderDepartment(staffId);
            
            // Tìm ticket trong danh sách phòng ban
            Optional<TicketResponse> ticket = departmentTickets.stream()
                .filter(t -> t.getTicketId().equals(ticketId))
                .findFirst();
            
            if (ticket.isPresent()) {
                return ResponseEntity.ok()
                    .body(ApiResponse.success(ticket.get()));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Ticket không thuộc phòng ban của bạn hoặc không tồn tại"));
            }
                
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
     * LEADER: Lấy thống kê dashboard của phòng ban
     */
    @GetMapping("/stats/department")
    public ResponseEntity<ApiResponse<TicketDashboardStats>> getDepartmentStats(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);

            // Kiểm tra quyền LEADER từ database
            Staff staff = staffRepository.findByIdWithRole(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
            
            Set<String> roles = userConverter.extractRoleNames(staff.getRole());
            if (!roles.contains("LEAD")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ LEADER mới có quyền truy cập"));
            }
            
            log.info("LEADER {} lấy thống kê dashboard phòng ban", staffId);
            
            TicketDashboardStats stats = leaderService.getLeaderDashboardStats(staffId);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(stats));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy thống kê phòng ban: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy thống kê phòng ban: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * LEADER: Lấy danh sách ticket được phân công cho một nhân viên
     */
    @GetMapping("/staffs/{staffId}/tickets")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getStaffTickets(
            @PathVariable Long staffId,
            HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long leaderId = sessionManager.getStaffId(session);

            // Kiểm tra quyền LEADER từ database
            Staff staff = staffRepository.findByIdWithRole(leaderId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
            
            Set<String> roles = userConverter.extractRoleNames(staff.getRole());
            if (!roles.contains("LEAD")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ LEADER mới có quyền truy cập"));
            }
            
            log.info("LEADER {} lấy danh sách ticket của staff {}", leaderId, staffId);
            
            List<TicketResponse> tickets = leaderService.getTicketsAssignedToStaff(staffId, leaderId);

            return ResponseEntity.ok()
                .body(ApiResponse.success(tickets));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy ticket của staff: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy ticket của staff: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * LEADER: Lấy rating trung bình và tổng số đánh giá của một staff
     */
    @GetMapping("/staffs/{staffId}/rating")
    public ResponseEntity<ApiResponse<StaffRatingSummary>> getStaffRating(@PathVariable Long staffId,
                                                                          HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            Staff staff = staffRepository.findByIdWithRole(sessionManager.getStaffId(session))
                .orElseThrow(() -> new RuntimeException("Staff not found"));
            Set<String> roles = userConverter.extractRoleNames(staff.getRole());
            if (!roles.contains("LEAD")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ LEADER mới có quyền truy cập"));
            }

            var summary = evaluationService.getStaffRatingSummary(staffId);
            return ResponseEntity.ok(ApiResponse.success(summary));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * LEADER: Lấy danh sách đánh giá theo ticket của staff
     */
    @GetMapping("/staffs/{staffId}/evaluations")
    public ResponseEntity<ApiResponse<List<StaffTicketEvaluation>>> getStaffEvaluations(@PathVariable Long staffId,
                                                                                        HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            Staff staff = staffRepository.findByIdWithRole(sessionManager.getStaffId(session))
                .orElseThrow(() -> new RuntimeException("Staff not found"));
            Set<String> roles = userConverter.extractRoleNames(staff.getRole());
            if (!roles.contains("LEAD")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Chỉ LEADER mới có quyền truy cập"));
            }

            var list = evaluationService.getEvaluationsByStaff(staffId);
            return ResponseEntity.ok(ApiResponse.success(list));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }
}
