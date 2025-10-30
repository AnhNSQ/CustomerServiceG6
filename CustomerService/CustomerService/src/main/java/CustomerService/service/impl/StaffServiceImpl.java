package CustomerService.service.impl;

import CustomerService.dto.StaffResponse;
import CustomerService.dto.TicketDashboardStats;
import CustomerService.dto.TicketResponse;
import CustomerService.entity.Ticket;
import CustomerService.entity.TicketAssign;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.StaffRepository;
import CustomerService.repository.TicketAssignRepository;
import CustomerService.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation cho StaffService - xử lý nghiệp vụ của STAFF role
 * Tuân thủ Single Responsibility Principle (SRP) - chỉ xử lý nghiệp vụ STAFF
 */
@Service
@Slf4j
@Transactional
public class StaffServiceImpl extends BaseUserService implements StaffService {
    
    private final StaffRepository staffRepository;
    private final TicketAssignRepository ticketAssignRepository;
    private final PasswordValidator passwordValidator;
    private final UserConverter userConverter;
    
    public StaffServiceImpl(CustomerRepository customerRepository,
                           StaffRepository staffRepository,
                           PasswordValidator passwordValidator,
                           UserConverter userConverter,
                           TicketAssignRepository ticketAssignRepository) {
        super(customerRepository, staffRepository, passwordValidator, userConverter);
        this.staffRepository = staffRepository;
        this.ticketAssignRepository = ticketAssignRepository;
        this.passwordValidator = passwordValidator;
        this.userConverter = userConverter;
    }

    /**
     * Tìm staff theo ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<StaffResponse> findById(Long staffId) {
        return staffRepository.findByIdWithRole(staffId)
            .map(userConverter::convertToStaffResponse);
    }

    /**
     * Tìm staff theo email hoặc username
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<StaffResponse> findByEmailOrUsername(String emailOrUsername) {
        return staffRepository.findActiveByEmailOrUsernameWithRole(emailOrUsername)
            .map(userConverter::convertToStaffResponse);
    }

    /**
     * STAFF: Lấy danh sách ticket được phân công
     */
    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsAssignedToStaff(Long staffId) {
        log.info("Lấy danh sách ticket được phân công cho staff {}", staffId);
        
        List<TicketAssign> assignments = ticketAssignRepository.findByAssignedToStaffIdOrderByAssignedAtDesc(staffId);
        
        return assignments.stream()
            .map(assignment -> convertToTicketResponse(assignment.getTicket()))
            .collect(Collectors.toList());
    }

    /**
     * STAFF: Lấy thống kê ticket được phân công
     */
    @Override
    @Transactional(readOnly = true)
    public TicketDashboardStats getAssignedTicketStats(Long staffId) {
        log.info("Lấy thống kê ticket được phân công cho staff {}", staffId);
        List<TicketAssign> assignments = ticketAssignRepository.findByAssignedToStaffIdOrderByAssignedAtDesc(staffId);

        long total = assignments.size();
        long processing = assignments.stream()
            .map(TicketAssign::getTicket)
            .filter(t -> t.getStatus() == Ticket.Status.IN_PROGRESS || t.getStatus() == Ticket.Status.ASSIGNED)
            .count();
        long closed = assignments.stream()
            .map(TicketAssign::getTicket)
            .filter(t -> t.getStatus() == Ticket.Status.CLOSED)
            .count();
        // urgent giữ nguyên nếu có nghiệp vụ riêng
        long urgent = assignments.stream()
            .map(TicketAssign::getTicket)
            .filter(t -> t.getPriority() == Ticket.Priority.HIGH && (t.getStatus() == Ticket.Status.IN_PROGRESS || t.getStatus() == Ticket.Status.ASSIGNED))
            .count();

        return new TicketDashboardStats(total, (int)processing, (int)closed, (int)urgent);
    }

    /**
     * Convert Ticket entity to TicketResponse DTO
     */
    private TicketResponse convertToTicketResponse(Ticket ticket) {
        Long customerId = null;
        String customerName = null;
        Long staffDepartmentId = null;
        String staffDepartmentName = null;
        
        try {
            if (ticket.getCustomer() != null) {
                customerId = ticket.getCustomer().getCustomerId();
                customerName = ticket.getCustomer().getName();
            }
        } catch (Exception e) {
            log.warn("Ticket {} has no associated customer or failed to load customer: {}", ticket.getTicketId(), e.getMessage());
        }
        
        try {
            if (ticket.getStaffDepartment() != null) {
                staffDepartmentId = ticket.getStaffDepartment().getStaffDepartmentId();
                staffDepartmentName = ticket.getStaffDepartment().getName();
            }
        } catch (Exception e) {
            log.warn("Ticket {} has no associated department or failed to load department: {}", ticket.getTicketId(), e.getMessage());
        }
        
        return new TicketResponse(
            ticket.getTicketId(),
            ticket.getSubject(),
            ticket.getDescription(),
            ticket.getPriority() != null ? ticket.getPriority().name() : null,
            ticket.getStatus() != null ? ticket.getStatus().name() : null,
            ticket.getCreatedAt(),
            customerId,
            customerName,
            staffDepartmentId,
            staffDepartmentName
        );
    }
}
