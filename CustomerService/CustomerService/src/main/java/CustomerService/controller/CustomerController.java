package CustomerService.controller;

import CustomerService.dto.ApiResponse;
import CustomerService.dto.CustomerLoginRequest;
import CustomerService.dto.CustomerRegisterRequest;
import CustomerService.dto.CustomerResponse;
import CustomerService.dto.CustomerTicketCreateRequest;
import CustomerService.dto.TicketResponse;
import CustomerService.exception.AuthenticationException;
import CustomerService.exception.UserNotFoundException;
import CustomerService.entity.TicketReply;
import CustomerService.dto.TicketReplyResponse;
import CustomerService.service.AuthenticationService;
import CustomerService.service.CustomerService;
import CustomerService.service.SessionManager;
import CustomerService.service.TicketReplyService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;
    private final AuthenticationService authenticationService;
    private final SessionManager sessionManager;
    private final TicketReplyService ticketReplyService;

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
     * Tạo ticket mới
     */
    @PostMapping("/tickets")
    public ResponseEntity<ApiResponse<Map<String, Object>>> createTicket(
            @Valid @RequestBody CustomerTicketCreateRequest request,
            HttpSession session) {
        try {
            if (!sessionManager.isCustomerLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long customerId = sessionManager.getCustomerId(session);
            
            log.info("Nhận yêu cầu tạo ticket từ customer {}", customerId);
            
            TicketResponse ticket = customerService.createTicket(customerId, request);

            Map<String, Object> response = Map.of(
                "message", "Ticket created successfully",
                "ticketId", ticket.getTicketId(),
                "status", "OPEN"
            );
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Ticket created successfully"));
                
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
     * Lấy danh sách ticket của customer hiện tại (có phân trang và tổng số)
     */
    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMyTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session) {
        try {
            if (!sessionManager.isCustomerLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long customerId = sessionManager.getCustomerId(session);
            
            log.info("Lấy danh sách ticket của customer {} (page: {}, size: {})", customerId, page, size);
            
            Map<String, Object> result = customerService.getTicketsByCustomerIdWithPaginationAndTotal(customerId, page, size);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(result));
                
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy danh sách ticket: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * Tìm kiếm ticket của customer theo tiêu đề
     */
    @GetMapping("/tickets/search")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> searchTickets(
            @RequestParam String query,
            HttpSession session) {
        try {
            if (!sessionManager.isCustomerLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long customerId = sessionManager.getCustomerId(session);
            
            log.info("Tìm kiếm ticket của customer {} với query: {}", customerId, query);
            
            List<TicketResponse> tickets = customerService.getTicketsByCustomerId(customerId);
            
            // Filter tickets by subject containing query (case insensitive)
            List<TicketResponse> filteredTickets = tickets.stream()
                .filter(ticket -> ticket.getSubject().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(filteredTickets));
                
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi tìm kiếm ticket: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * Lấy 5 ticket gần nhất của customer (cho dashboard)
     */
    @GetMapping("/tickets/recent")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getRecentTickets(HttpSession session) {
        try {
            if (!sessionManager.isCustomerLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long customerId = sessionManager.getCustomerId(session);
            
            log.info("Lấy 5 ticket gần nhất của customer {}", customerId);
            
            List<TicketResponse> tickets = customerService.getRecentTicketsByCustomerId(customerId, 5);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(tickets));
                
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy ticket gần đây: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * Lấy tất cả ticket của customer (cho thống kê dashboard)
     */
    @GetMapping("/tickets/all")
    public ResponseEntity<ApiResponse<List<TicketResponse>>> getAllTickets(HttpSession session) {
        try {
            if (!sessionManager.isCustomerLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long customerId = sessionManager.getCustomerId(session);
            
            log.info("Lấy tất cả ticket của customer {} cho thống kê", customerId);
            
            List<TicketResponse> tickets = customerService.getTicketsByCustomerId(customerId);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(tickets));
                
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy tất cả ticket: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * Cập nhật thông tin profile của customer
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateProfile(
            @RequestBody Map<String, Object> updateData,
            HttpSession session) {
        try {
            if (!sessionManager.isCustomerLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long customerId = sessionManager.getCustomerId(session);
            
            log.info("Cập nhật profile cho customer {} với data: {}", customerId, updateData);
            
            CustomerResponse updatedCustomer = customerService.updateProfile(customerId, updateData);
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(updatedCustomer, "Cập nhật thông tin thành công"));
                
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi cập nhật profile: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }
    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<ApiResponse<TicketResponse>> getTicketById(
            @PathVariable Long ticketId,
            HttpSession session) {
        try {
            if (!sessionManager.isCustomerLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long customerId = sessionManager.getCustomerId(session);
            
            log.info("Lấy thông tin ticket {} của customer {}", ticketId, customerId);
            
            Optional<TicketResponse> ticketOpt = customerService.getTicketById(ticketId);
            if (ticketOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không tìm thấy ticket với ID: " + ticketId));
            }
            
            TicketResponse ticket = ticketOpt.get();
            
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

    /**
     * Xóa ticket của customer
     */
    @DeleteMapping("/tickets/{ticketId}")
    public ResponseEntity<ApiResponse<String>> deleteTicket(
            @PathVariable Long ticketId,
            HttpSession session) {
        try {
            if (!sessionManager.isCustomerLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long customerId = sessionManager.getCustomerId(session);
            
            log.info("Nhận yêu cầu xóa ticket {} từ customer {}", ticketId, customerId);
            
            boolean deleted = customerService.deleteTicket(ticketId, customerId);
            
            if (deleted) {
                return ResponseEntity.ok()
                    .body(ApiResponse.success("Xóa ticket thành công", null));
            } else {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không thể xóa ticket. Ticket không tồn tại hoặc không thuộc về bạn"));
            }
                
        } catch (RuntimeException e) {
            log.error("Lỗi xóa ticket: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi xóa ticket: ", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * CUSTOMER: Lấy danh sách replies của một ticket
     */
    @GetMapping("/tickets/{ticketId}/replies")
    public ResponseEntity<ApiResponse<List<TicketReplyResponse>>> getTicketReplies(
            @PathVariable Long ticketId,
            HttpSession session) {
        try {
            if (!sessionManager.isCustomerLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long customerId = sessionManager.getCustomerId(session);
            
            log.info("CUSTOMER {} lấy danh sách replies của ticket {}", customerId, ticketId);
            
            // Kiểm tra ticket có thuộc về customer này không
            Optional<TicketResponse> ticketOpt = customerService.getTicketById(ticketId);
            if (ticketOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Không tìm thấy ticket với ID: " + ticketId));
            }
            
            TicketResponse ticket = ticketOpt.get();
            if (!ticket.getCustomerId().equals(customerId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Bạn không có quyền xem ticket này"));
            }
            
            List<TicketReply> replies = ticketReplyService.getRepliesByTicketId(ticketId);
            List<TicketReplyResponse> replyResponses = replies.stream()
                .map(TicketReplyResponse::fromEntity)
                .toList();
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(replyResponses));
                
        } catch (RuntimeException e) {
            log.error("Lỗi lấy replies của ticket: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi lấy replies của ticket: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }

    /**
     * CUSTOMER: Gửi phản hồi cho ticket
     */
    @PostMapping("/tickets/{ticketId}/reply")
    public ResponseEntity<ApiResponse<TicketReplyResponse>> replyToTicket(
            @PathVariable Long ticketId,
            @RequestBody Map<String, String> request,
            HttpSession session) {
        try {
            if (!sessionManager.isCustomerLoggedIn(session)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
            }
            
            Long customerId = sessionManager.getCustomerId(session);
            String message = request.get("message");
            String imageURL = request.get("imageURL");
            
            if (message == null || message.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Message không được để trống"));
            }
            
            log.info("CUSTOMER {} phản hồi ticket {}", customerId, ticketId);
            
            TicketReply reply = ticketReplyService.createReply(
                ticketId,
                TicketReply.SenderType.CUSTOMER,
                customerId,
                message,
                imageURL
            );
            
            return ResponseEntity.ok()
                .body(ApiResponse.success(TicketReplyResponse.fromEntity(reply), "Phản hồi đã được gửi thành công"));
                
        } catch (RuntimeException e) {
            log.error("Lỗi phản hồi ticket: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi phản hồi ticket: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Có lỗi xảy ra, vui lòng thử lại sau"));
        }
    }
}
