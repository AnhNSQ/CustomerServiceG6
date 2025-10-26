package CustomerService.service;

import CustomerService.dto.StaffResponse;
import CustomerService.dto.TicketDashboardStats;
import CustomerService.dto.TicketResponse;

import java.util.List;
import java.util.Optional;

/**
 * Interface cho LeaderService - xử lý nghiệp vụ của LEADER role
 * Tuân thủ Interface Segregation Principle (ISP)
 */
public interface LeaderService {
    
    /**
     * Tìm staff theo ID
     */
    Optional<StaffResponse> findById(Long staffId);
    
    /**
     * Tìm staff theo email hoặc username
     */
    Optional<StaffResponse> findByEmailOrUsername(String emailOrUsername);
    
    /**
     * LEADER: Lấy danh sách ticket của phòng ban
     */
    List<TicketResponse> getTicketsByLeaderDepartment(Long leaderId);
    
    /**
     * LEADER: Lấy danh sách nhân viên trong phòng ban
     */
    List<StaffResponse> getStaffByLeaderDepartment(Long leaderId);
    
    /**
     * LEADER: Phân công ticket cho nhân viên
     */
    boolean assignTicketToStaff(Long ticketId, Long staffId, Long leaderId, String note);
    
    /**
     * LEADER: Lấy thống kê dashboard của phòng ban
     */
    TicketDashboardStats getLeaderDashboardStats(Long leaderId);
}
