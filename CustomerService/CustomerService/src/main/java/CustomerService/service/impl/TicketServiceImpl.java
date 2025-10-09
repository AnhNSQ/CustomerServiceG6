package CustomerService.service.impl;

import CustomerService.dto.TicketCreateRequest;
import CustomerService.dto.TicketResponse;
import CustomerService.entity.Customer;
import CustomerService.entity.Order;
import CustomerService.entity.Ticket;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.OrderRepository;
import CustomerService.repository.TicketRepository;
import CustomerService.service.ITicketService;
import CustomerService.service.OrderValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation của ITicketService cho customer
 * Tuân thủ Single Responsibility Principle (SRP) và Dependency Inversion Principle (DIP)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TicketServiceImpl implements ITicketService {
    
    private final TicketRepository ticketRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderValidationService orderValidationService;
    
    @Override
    public TicketResponse createTicket(Long customerId, TicketCreateRequest request) {
        log.info("Bắt đầu tạo ticket cho customer {} với order {}", customerId, request.getOrderId());

        Customer customer = customerRepository.findByIdWithRole(customerId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy customer với ID: " + customerId));

        Order order = orderRepository.findByIdWithCustomer(request.getOrderId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + request.getOrderId()));

        if (!order.getCustomer().getCustomerId().equals(customerId)) {
            throw new RuntimeException("Đơn hàng không thuộc về customer này");
        }

        Ticket ticket = new Ticket(
            customer,
            order,
            request.getSubject(),
            request.getDescription(),
            Ticket.Priority.MEDIUM
        );

        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Tạo ticket thành công với ID: {} cho order {}", savedTicket.getTicketId(), request.getOrderId());
        
        return convertToResponse(savedTicket);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByCustomerId(Long customerId) {
        log.info("Lấy danh sách ticket của customer {}", customerId);
        
        List<Ticket> tickets = ticketRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        
        return tickets.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<TicketResponse> getTicketById(Long ticketId) {
        log.info("Lấy ticket với ID {}", ticketId);
        
        return ticketRepository.findByIdWithCustomer(ticketId)
            .map(this::convertToResponse);
    }
    
    /**
     * Chuyển đổi Ticket entity thành TicketResponse DTO
     */
    private TicketResponse convertToResponse(Ticket ticket) {
        Long customerId = null;
        Long orderId = null;
        try {
            if (ticket.getCustomer() != null) {
                customerId = ticket.getCustomer().getCustomerId();
            }
            if (ticket.getOrder() != null) {
                orderId = ticket.getOrder().getOrderId();
            }
        } catch (Exception e) {
            log.warn("Ticket {} has no associated customer/order or failed to load: {}", ticket.getTicketId(), e.getMessage());
        }
        return new TicketResponse(
            ticket.getTicketId(),
            ticket.getSubject(),
            ticket.getDescription(),
            ticket.getPriority() != null ? ticket.getPriority().name() : null,
            ticket.getStatus() != null ? ticket.getStatus().name() : null,
            ticket.getCreatedAt(),
            customerId,
            orderId
        );
    }
}
