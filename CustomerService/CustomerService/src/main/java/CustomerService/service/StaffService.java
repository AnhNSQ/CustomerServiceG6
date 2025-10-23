package CustomerService.service;

import CustomerService.dto.StaffLoginRequest;
import CustomerService.dto.StaffResponse;
import CustomerService.dto.TicketResponse;

import java.util.List;
import java.util.Optional;

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
     * Kiểm tra staff có phải LEADER không
     */
    boolean isLeader(Long staffId);
    
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
     * STAFF: Lấy danh sách ticket được phân công
     */
    List<TicketResponse> getTicketsAssignedToStaff(Long staffId);
}
