package CustomerService.service;

import CustomerService.dto.StaffResponse;
import CustomerService.dto.TicketDashboardStats;
import CustomerService.dto.TicketResponse;
import CustomerService.entity.StaffDepartment;

import java.util.List;
import java.util.Optional;

/**
 * Interface cho AdminService - xử lý nghiệp vụ của ADMIN role
 * Tuân thủ Interface Segregation Principle (ISP)
 */
public interface AdminService {
    
    /**
     * Tìm staff theo ID
     */
    Optional<StaffResponse> findById(Long staffId);
    
    /**
     * Tìm staff theo email hoặc username
     */
    Optional<StaffResponse> findByEmailOrUsername(String emailOrUsername);
    
    /**
     * ADMIN: Lấy tất cả ticket trong hệ thống
     */
    List<TicketResponse> getAllTickets();
    
    /**
     * ADMIN: Lấy thống kê tổng quan hệ thống
     */
    TicketDashboardStats getGlobalDashboardStats();
    
    /**
     * ADMIN: Lấy tất cả phòng ban
     */
    List<StaffDepartment> getAllDepartments();
    
    /**
     * ADMIN: Lấy tất cả staff trong hệ thống
     */
    List<StaffResponse> getAllStaff();
    
    /**
     * ADMIN: Lấy ticket theo ID (có thể xem tất cả)
     */
    Optional<TicketResponse> getTicketById(Long ticketId);
}
