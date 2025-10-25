package CustomerService.service;

import CustomerService.dto.StaffLoginRequest;
import CustomerService.dto.StaffResponse;
import CustomerService.dto.TicketDashboardStats;
import CustomerService.dto.TicketResponse;

import java.util.List;
import java.util.Optional;

/**
 * Interface cho StaffService - xử lý nghiệp vụ của STAFF role
 * Tuân thủ Interface Segregation Principle (ISP)
 */
public interface StaffService {
    
    /**
     * Đăng nhập staff
     * @deprecated Sử dụng AuthenticationService thay thế
     */
    @Deprecated
    StaffResponse login(StaffLoginRequest request);
    
    /**
     * Tìm staff theo ID
     */
    Optional<StaffResponse> findById(Long staffId);
    
    /**
     * Tìm staff theo email hoặc username
     */
    Optional<StaffResponse> findByEmailOrUsername(String emailOrUsername);
    
    /**
     * STAFF: Lấy danh sách ticket được phân công
     */
    List<TicketResponse> getTicketsAssignedToStaff(Long staffId);
    
    /**
     * STAFF: Lấy thống kê ticket được phân công
     */
    TicketDashboardStats getAssignedTicketStats(Long staffId);
}