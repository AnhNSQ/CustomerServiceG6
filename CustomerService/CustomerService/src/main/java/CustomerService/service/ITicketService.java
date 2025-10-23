package CustomerService.service;

import CustomerService.dto.CustomerTicketCreateRequest;
import CustomerService.dto.TicketDashboardStats;
import CustomerService.dto.TicketResponse;
import CustomerService.entity.StaffDepartment;

import java.util.List;
import java.util.Optional;

public interface ITicketService {
    
    /**
     * Lấy tất cả ticket (dành cho staff/admin)
     */
    List<TicketResponse> getAllTickets();
    
    /**
     * Lấy thống kê cho dashboard staff
     */
    TicketDashboardStats getDashboardStats();
    
    /**
     * Lấy 5 ticket gần đây nhất
     */
    List<TicketResponse> getRecentTickets(int limit);
    
    /**
     * Tạo ticket mới cho customer
     */
    TicketResponse createTicket(Long customerId, CustomerTicketCreateRequest request);
    
    /**
     * Lấy tất cả ticket của customer
     */
    List<TicketResponse> getTicketsByCustomerId(Long customerId);
    
    /**
     * Lấy ticket theo ID
     */
    Optional<TicketResponse> getTicketById(Long ticketId);
    
    /**
     * Lấy tất cả departments (dành cho dropdown khi tạo ticket)
     */
    List<StaffDepartment> getAllDepartments();
}
