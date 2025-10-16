package CustomerService.service;

import CustomerService.dto.TicketCreateRequest;
import CustomerService.dto.TicketDashboardStats;
import CustomerService.dto.TicketResponse;
import CustomerService.entity.StaffDepartment;

import java.util.List;
import java.util.Optional;

/**
 * Interface cho dịch vụ quản lý Ticket
 * Tuân thủ Dependency Inversion Principle (DIP)
 */
public interface TicketService {
    
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
     * @param customerId ID của customer
     * @param request Thông tin ticket cần tạo
     * @return TicketResponse
     */
    TicketResponse createTicket(Long customerId, TicketCreateRequest request);
    
    /**
     * Lấy tất cả ticket của customer
     * @param customerId ID của customer
     * @return Danh sách ticket
     */
    List<TicketResponse> getTicketsByCustomerId(Long customerId);
    
    /**
     * Lấy ticket theo ID
     * @param ticketId ID của ticket
     * @return TicketResponse
     */
    Optional<TicketResponse> getTicketById(Long ticketId);
    
    /**
     * Lấy tất cả departments (dành cho dropdown khi tạo ticket)
     * @return Danh sách departments
     */
    List<StaffDepartment> getAllDepartments();
}