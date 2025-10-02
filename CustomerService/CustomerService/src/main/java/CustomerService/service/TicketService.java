package CustomerService.service;

import CustomerService.dto.TicketCreateRequest;
import CustomerService.dto.TicketDashboardStats;
import CustomerService.dto.TicketResponse;
import CustomerService.entity.Customer;
import CustomerService.entity.Ticket;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final OrderValidationService orderValidationService;
    
    /**
     * Lấy tất cả ticket (dành cho staff/admin)
     */
    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets() {
        log.info("Lấy tất cả ticket cho staff/admin");
        return ticketRepository.findAllWithCustomerOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy thống kê cho dashboard staff
     */
    @Transactional(readOnly = true)
    public TicketDashboardStats getDashboardStats() {
        long total = ticketRepository.count();
        long pending = ticketRepository.countByStatus(Ticket.Status.OPEN);
        long resolved = ticketRepository.countByStatus(Ticket.Status.RESOLVED);
        long urgent = ticketRepository.countByPriorityAndStatus(Ticket.Priority.HIGH, Ticket.Status.OPEN);
        return new TicketDashboardStats(total, pending, resolved, urgent);
    }

    /**
     * Lấy 5 ticket gần đây nhất
     */
    @Transactional(readOnly = true)
    public List<TicketResponse> getRecentTickets(int limit) {
        // hiện sử dụng top5 theo createdAt desc, tham số limit để mở rộng sau
        return ticketRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Tạo ticket mới cho customer
     * @param customerId ID của customer
     * @param request Thông tin ticket cần tạo
     * @return TicketResponse
     */
    public TicketResponse createTicket(Long customerId, TicketCreateRequest request) {
        log.info("Bắt đầu tạo ticket cho customer {}", customerId);

        Customer customer = customerRepository.findByIdWithRole(customerId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy customer với ID: " + customerId));

        if (!orderValidationService.hasOrders(customerId)) {
            throw new RuntimeException("Customer phải có ít nhất một đơn hàng để tạo ticket hỗ trợ");
        }

        Ticket ticket = new Ticket(
            customer,
            request.getSubject(),
            request.getDescription(),
            Ticket.Priority.MEDIUM
        );

        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Tạo ticket thành công với ID: {}", savedTicket.getTicketId());
        
        return convertToResponse(savedTicket);
    }
    
    /**
     * Lấy tất cả ticket của customer
     * @param customerId ID của customer
     * @return Danh sách ticket
     */
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByCustomerId(Long customerId) {
        log.info("Lấy danh sách ticket của customer {}", customerId);
        
        List<Ticket> tickets = ticketRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        
        return tickets.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Lấy ticket theo ID
     * @param ticketId ID của ticket
     * @return TicketResponse
     */
    @Transactional(readOnly = true)
    public Optional<TicketResponse> getTicketById(Long ticketId) {
        log.info("Lấy ticket với ID {}", ticketId);
        
        return ticketRepository.findByIdWithCustomer(ticketId)
            .map(this::convertToResponse);
    }
    

    private TicketResponse convertToResponse(Ticket ticket) {
        Long customerId = null;
        try {
            if (ticket.getCustomer() != null) {
                customerId = ticket.getCustomer().getCustomerId();
            }
        } catch (Exception e) {
            log.warn("Ticket {} has no associated customer or failed to load customer: {}", ticket.getTicketId(), e.getMessage());
        }
        return new TicketResponse(
            ticket.getTicketId(),
            ticket.getSubject(),
            ticket.getDescription(),
            ticket.getPriority() != null ? ticket.getPriority().name() : null,
            ticket.getStatus() != null ? ticket.getStatus().name() : null,
            ticket.getCreatedAt(),
                ticket.getCustomer().getCustomerId()
        );
    }
}
