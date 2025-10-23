package CustomerService.service;

import CustomerService.dto.CustomerLoginRequest;
import CustomerService.dto.CustomerRegisterRequest;
import CustomerService.dto.CustomerResponse;
import CustomerService.dto.CustomerTicketCreateRequest;
import CustomerService.dto.TicketResponse;

import java.util.Map;
import java.util.Optional;

public interface ICustomerService {
    
    /**
     * Đăng ký tài khoản customer mới
     */
    CustomerResponse register(CustomerRegisterRequest request);
    
    /**
     * Đăng nhập customer
     * @deprecated Sử dụng AuthenticationService thay thế
     */
    @Deprecated
    CustomerResponse login(CustomerLoginRequest request);
    
    /**
     * Tìm customer theo ID
     */
    Optional<CustomerResponse> findById(Long customerId);
    
    /**
     * Tìm customer theo email hoặc username
     */
    Optional<CustomerResponse> findByEmailOrUsername(String emailOrUsername);
    
    /**
     * Kiểm tra email đã tồn tại
     */
    boolean existsByEmail(String email);
    
    /**
     * Kiểm tra username đã tồn tại
     */
    boolean existsByUsername(String username);
    
    /**
     * Cập nhật thông tin customer
     */
    CustomerResponse updateProfile(Long customerId, Map<String, Object> updateData);
    
    /**
     * Tạo ticket mới cho customer
     */
    TicketResponse createTicket(Long customerId, CustomerTicketCreateRequest request);
    
    /**
     * Lấy danh sách ticket của customer với phân trang
     */
    Map<String, Object> getTicketsByCustomerIdWithPaginationAndTotal(Long customerId, int page, int size);
    
    /**
     * Lấy ticket gần đây của customer
     */
    java.util.List<TicketResponse> getRecentTicketsByCustomerId(Long customerId, int limit);
    
    /**
     * Lấy tất cả ticket của customer
     */
    java.util.List<TicketResponse> getTicketsByCustomerId(Long customerId);
    
    /**
     * Lấy ticket theo ID
     */
    Optional<TicketResponse> getTicketById(Long ticketId);
    
    /**
     * Xóa ticket
     */
    boolean deleteTicket(Long ticketId, Long customerId);
}
