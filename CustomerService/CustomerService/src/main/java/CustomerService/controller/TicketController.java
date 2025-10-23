package CustomerService.controller;

import CustomerService.dto.ApiResponse;
import CustomerService.dto.CustomerTicketCreateRequest;
import CustomerService.dto.TicketResponse;
import CustomerService.service.TicketService;
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
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TicketController {
    
    private final TicketService ticketService;
    private final SessionManager sessionManager;
    
    /**
     * Tạo ticket mới
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TicketResponse>> createTicket(
            @Valid @RequestBody CustomerTicketCreateRequest request,
            HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Chưa đăng nhập"));
            }
            
            log.info("Nhận yêu cầu tạo ticket từ customer {}", customerId);
            
            TicketResponse ticket = ticketService.createTicket(customerId, request);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ticket, "Tạo ticket thành công"));
                
        } catch (RuntimeException e) {
            log.error("Lỗi tạo ticket: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi tạo ticket: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }
    
    /**
     * Lấy danh sách ticket của customer hiện tại
     */
    @GetMapping("/my-tickets")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getMyTickets(HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Chưa đăng nhập"));
            }
            
            log.info("Lấy danh sách ticket của customer {}", customerId);
            
            List<TicketResponse> tickets = ticketService.getTicketsByCustomerId(customerId);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(tickets));
                
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy danh sách ticket: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * Lấy tất cả ticket (chỉ dành cho staff/admin)
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getAllTicketsForStaff(HttpSession session) {
        try {
            if (!sessionManager.isStaffLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Chưa đăng nhập"));
            }

            log.info("Staff yêu cầu lấy tất cả ticket");
            List<TicketResponse> tickets = ticketService.getAllTickets();
            return ResponseEntity.ok(ApiResponse.success(tickets));
        } catch (Exception e) {
            log.error("Lỗi khi lấy tất cả ticket: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }
    
    /**
     * Lấy thông tin ticket theo ID
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketById(
            @PathVariable Long ticketId,
            HttpSession session) {
        try {
            Long customerId = (Long) session.getAttribute("customerId");
            
            if (customerId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Chưa đăng nhập"));
            }
            
            log.info("Lấy thông tin ticket {} của customer {}", ticketId, customerId);
            
            TicketResponse ticket = ticketService.getTicketById(ticketId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ticket với ID: " + ticketId));
            
            // Kiểm tra ticket có thuộc về customer này không
            if (!ticket.getCustomerId().equals(customerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bạn không có quyền xem ticket này"));
            }
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(ticket));
                
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
    
}
