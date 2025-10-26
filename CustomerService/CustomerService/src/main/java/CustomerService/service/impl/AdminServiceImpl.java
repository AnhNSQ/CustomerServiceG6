package CustomerService.service.impl;

import CustomerService.dto.StaffResponse;
import CustomerService.dto.TicketDashboardStats;
import CustomerService.dto.TicketResponse;
import CustomerService.entity.Staff;
import CustomerService.entity.StaffDepartment;
import CustomerService.entity.Ticket;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.StaffRepository;
import CustomerService.repository.StaffDepartmentRepository;
import CustomerService.repository.TicketRepository;
import CustomerService.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation cho AdminService - xử lý nghiệp vụ của ADMIN role
 * Tuân thủ Single Responsibility Principle (SRP) - chỉ xử lý nghiệp vụ ADMIN
 */
@Service
@Slf4j
@Transactional
public class AdminServiceImpl extends BaseUserService implements AdminService {
    
    private final StaffRepository staffRepository;
    private final TicketRepository ticketRepository;
    private final StaffDepartmentRepository staffDepartmentRepository;
    private final UserConverter userConverter;
    
    public AdminServiceImpl(CustomerRepository customerRepository,
                           StaffRepository staffRepository,
                           PasswordValidator passwordValidator,
                           UserConverter userConverter,
                           TicketRepository ticketRepository,
                           StaffDepartmentRepository staffDepartmentRepository) {
        super(customerRepository, staffRepository, passwordValidator, userConverter);
        this.staffRepository = staffRepository;
        this.ticketRepository = ticketRepository;
        this.staffDepartmentRepository = staffDepartmentRepository;
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
     * ADMIN: Lấy tất cả ticket trong hệ thống
     */
    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets() {
        log.info("ADMIN lấy tất cả ticket trong hệ thống");
        
        List<Ticket> tickets = ticketRepository.findAllWithCustomerOrderByCreatedAtDesc();
        
        return tickets.stream()
            .map(this::convertToTicketResponse)
            .collect(Collectors.toList());
    }

    /**
     * ADMIN: Lấy thống kê tổng quan hệ thống
     */
    @Override
    @Transactional(readOnly = true)
    public TicketDashboardStats getGlobalDashboardStats() {
        log.info("ADMIN lấy thống kê tổng quan hệ thống");
        
        long total = ticketRepository.count();
        long pending = ticketRepository.countByStatus(Ticket.Status.OPEN);
        long resolved = ticketRepository.countByStatus(Ticket.Status.RESOLVED);
        long urgent = ticketRepository.countByPriorityAndStatus(Ticket.Priority.HIGH, Ticket.Status.OPEN);
        
        return new TicketDashboardStats(total, pending, resolved, urgent);
    }

    /**
     * ADMIN: Lấy tất cả phòng ban
     */
    @Override
    @Transactional(readOnly = true)
    public List<StaffDepartment> getAllDepartments() {
        log.info("ADMIN lấy tất cả phòng ban");
        return staffDepartmentRepository.findAll();
    }

    /**
     * ADMIN: Lấy tất cả staff trong hệ thống
     */
    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getAllStaff() {
        log.info("ADMIN lấy tất cả staff trong hệ thống");
        
        List<Staff> staffList = staffRepository.findAll();
        
        return staffList.stream()
            .map(userConverter::convertToStaffResponse)
            .collect(Collectors.toList());
    }

    /**
     * ADMIN: Lấy ticket theo ID (có thể xem tất cả)
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TicketResponse> getTicketById(Long ticketId) {
        log.info("ADMIN lấy ticket với ID {}", ticketId);
        
        return ticketRepository.findByIdWithCustomer(ticketId)
            .map(this::convertToTicketResponse);
    }

    /**
     * Convert Ticket entity to TicketResponse DTO
     */
    private TicketResponse convertToTicketResponse(Ticket ticket) {
        Long customerId = null;
        Long staffDepartmentId = null;
        String staffDepartmentName = null;
        
        try {
            if (ticket.getCustomer() != null) {
                customerId = ticket.getCustomer().getCustomerId();
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
            staffDepartmentId,
            staffDepartmentName
        );
    }
}
