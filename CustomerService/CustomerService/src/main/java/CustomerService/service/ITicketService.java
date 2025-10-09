package CustomerService.service;

import CustomerService.dto.TicketCreateRequest;
import CustomerService.dto.TicketResponse;

import java.util.List;
import java.util.Optional;

/**
 * Interface cho dịch vụ quản lý ticket của customer
 * Tuân thủ Interface Segregation Principle (ISP) và Dependency Inversion Principle (DIP)
 */
public interface ITicketService {
    
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
     * @return Danh sách ticket của customer
     */
    List<TicketResponse> getTicketsByCustomerId(Long customerId);
    
    /**
     * Lấy ticket theo ID
     * @param ticketId ID của ticket
     * @return TicketResponse nếu tìm thấy
     */
    Optional<TicketResponse> getTicketById(Long ticketId);
}
