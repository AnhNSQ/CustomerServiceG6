package CustomerService.controller;

import CustomerService.dto.*;
import CustomerService.entity.StaffDepartment;
import jakarta.validation.Valid;
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

    /**
     * ADMIN: Lấy tất cả customer trong hệ thống
     */
    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            log.info("ADMIN {} lấy tất cả customer trong hệ thống", staffId);
            
            List<CustomerResponse> customers = adminService.getAllCustomers();
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(customers));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy customers: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy customers: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * ADMIN: Lấy tất cả staff members (role STAFF) trong hệ thống
     */
    @GetMapping("/staff-members")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getAllStaffMembers(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            log.info("ADMIN {} lấy tất cả staff members trong hệ thống", staffId);
            
            List<StaffResponse> staffMembers = adminService.getAllStaffMembers();
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(staffMembers));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy staff members: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy staff members: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * ADMIN: Lấy tất cả leads (role LEAD) trong hệ thống
     */
    @GetMapping("/leads")
    public ResponseEntity<ApiResponse<List<StaffResponse>>> getAllLeads(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            log.info("ADMIN {} lấy tất cả leads trong hệ thống", staffId);
            
            List<StaffResponse> leads = adminService.getAllLeads();
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(leads));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy leads: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy leads: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * ADMIN: Tạo tài khoản staff hoặc lead mới
     */
    @PostMapping("/staff")
    public ResponseEntity<ApiResponse<StaffResponse>> createStaff(
            @Valid @RequestBody StaffCreateRequest request,
            HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            log.info("ADMIN {} tạo tài khoản staff/lead với email: {}", staffId, request.getEmail());
            
            StaffResponse createdStaff = adminService.createStaff(request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdStaff, "Tạo tài khoản thành công"));
                
        } catch (RuntimeException e) {
            log.error("Lỗi tạo staff: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi tạo staff: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * ADMIN: Lấy tất cả đơn hàng trong hệ thống
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<AdminOrderResponse>>> getAllOrders(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            log.info("ADMIN {} lấy tất cả đơn hàng trong hệ thống", staffId);
            
            List<AdminOrderResponse> orders = adminService.getAllOrders();
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(orders));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy đơn hàng: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy đơn hàng: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * ADMIN: Lấy chi tiết đơn hàng với lịch sử hoạt động
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponse<AdminOrderDetailResponse>> getOrderDetail(
            @PathVariable Long orderId,
            HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            log.info("ADMIN {} lấy chi tiết đơn hàng {}", staffId, orderId);
            
            Optional<AdminOrderDetailResponse> orderDetail = adminService.getOrderDetailById(orderId);
            
            if (orderDetail.isPresent()) {
                return ResponseEntity.ok()
                    .body(ApiResponse.success(orderDetail.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Không tìm thấy đơn hàng với ID: " + orderId));
            }
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy chi tiết đơn hàng: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy chi tiết đơn hàng: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * ADMIN: Phê duyệt đơn hàng (chuyển từ PENDING sang PAID)
     */
    @PostMapping("/orders/{orderId}/approve")
    public ResponseEntity<ApiResponse<AdminOrderDetailResponse>> approveOrder(
            @PathVariable Long orderId,
            @RequestBody(required = false) ApproveOrderRequest request,
            HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long staffId = sessionManager.getStaffId(session);
            
            log.info("ADMIN {} phê duyệt đơn hàng {}", staffId, orderId);
            
            String notes = (request != null && request.getNotes() != null) ? request.getNotes() : null;
            AdminOrderDetailResponse approvedOrder = adminService.approveOrder(orderId, staffId, notes);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(approvedOrder, "Đơn hàng đã được phê duyệt thành công"));
                
        } catch (RuntimeException e) {
            log.error("Lỗi phê duyệt đơn hàng: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi phê duyệt đơn hàng: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }
}
