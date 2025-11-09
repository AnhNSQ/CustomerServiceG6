package CustomerService.service.impl;

import CustomerService.dto.StaffResponse;
import CustomerService.dto.TicketDashboardStats;
import CustomerService.dto.TicketResponse;
import CustomerService.entity.Role.RoleName;
import CustomerService.entity.Staff;
import CustomerService.entity.Ticket;
import CustomerService.entity.TicketAssign;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.StaffRepository;
import CustomerService.repository.TicketAssignRepository;
import CustomerService.repository.TicketRepository;
import CustomerService.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation cho LeaderService - xử lý nghiệp vụ của LEADER role
 * Tuân thủ Single Responsibility Principle (SRP) - chỉ xử lý nghiệp vụ LEADER
 */
@Service
@Slf4j
@Transactional
public class LeaderServiceImpl extends BaseUserService implements LeaderService {
    
    private final StaffRepository staffRepository;
    private final TicketRepository ticketRepository;
    private final TicketAssignRepository ticketAssignRepository;
    private final UserConverter userConverter;
    
    public LeaderServiceImpl(CustomerRepository customerRepository,
                           StaffRepository staffRepository,
                           PasswordValidator passwordValidator,
                           UserConverter userConverter,
                           TicketRepository ticketRepository,
                           TicketAssignRepository ticketAssignRepository) {
        super(customerRepository, staffRepository, passwordValidator, userConverter);
        this.staffRepository = staffRepository;
        this.ticketRepository = ticketRepository;
        this.ticketAssignRepository = ticketAssignRepository;
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
     * LEADER: Lấy danh sách ticket của phòng ban
     */
    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByLeaderDepartment(Long leaderId) {
        log.info("Lấy danh sách ticket của phòng ban cho LEADER {}", leaderId);

        Staff leader = staffRepository.findByIdWithRoleAndDepartment(leaderId)
            .orElseThrow(() -> new RuntimeException("Leader not found with ID: " + leaderId));
        
        if (!RoleName.LEAD.equals(leader.getRole().getRoleName())) {
            throw new RuntimeException("Staff is not a LEADER");
        }
        
        List<Ticket> tickets = ticketRepository.findByStaffDepartmentIdOrderByCreatedAtDesc(
            leader.getStaffDepartment().getStaffDepartmentId()
        );
        
        return tickets.stream()
            .map(this::convertToTicketResponse)
            .collect(Collectors.toList());
    }

    /**
     * LEADER: Lấy danh sách ticket OPEN của phòng ban
     */
    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getOpenTicketsByLeaderDepartment(Long leaderId) {
        log.info("Lấy danh sách ticket OPEN của phòng ban cho LEADER {}", leaderId);

        Staff leader = staffRepository.findByIdWithRoleAndDepartment(leaderId)
            .orElseThrow(() -> new RuntimeException("Leader not found with ID: " + leaderId));
        
        if (!RoleName.LEAD.equals(leader.getRole().getRoleName())) {
            throw new RuntimeException("Staff is not a LEADER");
        }

        List<Ticket> departmentTickets = ticketRepository.findByStaffDepartmentIdOrderByCreatedAtDesc(
            leader.getStaffDepartment().getStaffDepartmentId()
        );

        List<Ticket> openTickets = departmentTickets.stream()
            .filter(ticket -> ticket.getStatus() == Ticket.Status.OPEN)
            .collect(Collectors.toList());
        
        return openTickets.stream()
            .map(this::convertToTicketResponse)
            .collect(Collectors.toList());
    }

    /**
     * LEADER: Lấy danh sách nhân viên trong phòng ban
     */
    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getStaffByLeaderDepartment(Long leaderId) {
        log.info("Lấy danh sách nhân viên trong phòng ban cho LEADER {}", leaderId);

        Staff leader = staffRepository.findByIdWithRoleAndDepartment(leaderId)
            .orElseThrow(() -> new RuntimeException("Leader not found with ID: " + leaderId));
        
        if (!RoleName.LEAD.equals(leader.getRole().getRoleName())) {
            throw new RuntimeException("Staff is not a LEADER");
        }

        List<Staff> staffList = staffRepository.findByStaffDepartmentIdAndRoleNameNot(
            leader.getStaffDepartment().getStaffDepartmentId(),
            RoleName.LEAD
        );
        
        return staffList.stream()
            .map(userConverter::convertToStaffResponse)
            .collect(Collectors.toList());
    }

    /**
     * LEADER: Phân công ticket cho nhân viên
     */
    @Override
    @Transactional
    public boolean assignTicketToStaff(Long ticketId, Long staffId, Long leaderId) {
        log.info("LEADER {} phân công ticket {} cho staff {}", leaderId, ticketId, staffId);

        Staff leader = staffRepository.findByIdWithRoleAndDepartment(leaderId)
            .orElseThrow(() -> new RuntimeException("Leader not found with ID: " + leaderId));
        
        if (!RoleName.LEAD.equals(leader.getRole().getRoleName())) {
            throw new RuntimeException("Staff is not a LEADER");
        }

        Ticket ticket = ticketRepository.findByIdWithCustomer(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));

        if (!ticket.getStaffDepartment().getStaffDepartmentId().equals(leader.getStaffDepartment().getStaffDepartmentId())) {
            throw new RuntimeException("Ticket does not belong to leader's department");
        }

        if (ticket.getStatus() != Ticket.Status.OPEN) {
            throw new RuntimeException("Chỉ có thể phân công ticket có trạng thái OPEN");
        }

        Staff staff = staffRepository.findByIdWithRoleAndDepartment(staffId)
            .orElseThrow(() -> new RuntimeException("Staff not found with ID: " + staffId));

        if (!staff.getStaffDepartment().getStaffDepartmentId().equals(leader.getStaffDepartment().getStaffDepartmentId())) {
            throw new RuntimeException("Staff does not belong to leader's department");
        }

        TicketAssign assignment = new TicketAssign();
        assignment.setTicket(ticket);
        assignment.setAssignedTo(staff);
        assignment.setAssignedBy(leader);
        assignment.setAssignedAt(LocalDateTime.now());

        String departmentName = staff.getStaffDepartment().getName().toUpperCase();

        if (departmentName.contains("FINANCE") || departmentName.contains("TÀI CHÍNH")) {
            assignment.setRoleNeeded(TicketAssign.RoleNeeded.FINANCIAL_STAFF);
        } else if (departmentName.contains("TECH") || departmentName.contains("KỸ THUẬT")) {
            assignment.setRoleNeeded(TicketAssign.RoleNeeded.TECHNICAL_SUPPORT);
        } else {
            assignment.setRoleNeeded(TicketAssign.RoleNeeded.TECHNICAL_SUPPORT);
        }

        ticketAssignRepository.save(assignment);
        
        // Cập nhật status ticket: OPEN → IN_PROGRESS
        ticket.setStatus(Ticket.Status.IN_PROGRESS);
        ticketRepository.save(ticket);
        
        log.info("Phân công ticket {} cho staff {} thành công", ticketId, staffId);
        return true;
    }

    /**
     * LEADER: Lấy thống kê dashboard của phòng ban
     */
    @Override
    @Transactional(readOnly = true)
    public TicketDashboardStats getLeaderDashboardStats(Long leaderId) {
        log.info("Lấy thống kê dashboard cho LEADER {}", leaderId);

        Staff leader = staffRepository.findByIdWithRoleAndDepartment(leaderId)
            .orElseThrow(() -> new RuntimeException("Leader not found with ID: " + leaderId));
        
        if (!RoleName.LEAD.equals(leader.getRole().getRoleName())) {
            throw new RuntimeException("Staff is not a LEADER");
        }

        Long departmentId = leader.getStaffDepartment().getStaffDepartmentId();
        
        List<Ticket> departmentTickets = ticketRepository.findByStaffDepartmentIdOrderByCreatedAtDesc(departmentId);
        
        long total = departmentTickets.size();
        long pending = departmentTickets.stream()
            .mapToLong(ticket -> ticket.getStatus() == Ticket.Status.OPEN ? 1 : 0)
            .sum();
        long resolved = departmentTickets.stream()
            .mapToLong(ticket -> ticket.getStatus() == Ticket.Status.CLOSED ? 1 : 0)
            .sum();
        long urgent = departmentTickets.stream()
            .mapToLong(ticket -> 
                ticket.getPriority() == Ticket.Priority.HIGH && 
                ticket.getStatus() == Ticket.Status.OPEN ? 1 : 0)
            .sum();
            
        return new TicketDashboardStats(total, pending, resolved, urgent);
    }

    /**
     * LEADER: Lấy danh sách ticket được phân công cho một nhân viên
     */
    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsAssignedToStaff(Long staffId, Long leaderId) {
        log.info("LEADER {} lấy danh sách ticket được phân công cho staff {}", leaderId, staffId);

        Staff leader = staffRepository.findByIdWithRoleAndDepartment(leaderId)
            .orElseThrow(() -> new RuntimeException("Leader not found with ID: " + leaderId));
        
        if (!RoleName.LEAD.equals(leader.getRole().getRoleName())) {
            throw new RuntimeException("Staff is not a LEADER");
        }

        Staff staff = staffRepository.findByIdWithRoleAndDepartment(staffId)
            .orElseThrow(() -> new RuntimeException("Staff not found with ID: " + staffId));

        if (!staff.getStaffDepartment().getStaffDepartmentId().equals(leader.getStaffDepartment().getStaffDepartmentId())) {
            throw new RuntimeException("Staff does not belong to leader's department");
        }

        List<TicketAssign> assignments = ticketAssignRepository.findByAssignedToStaffIdOrderByAssignedAtDesc(staffId);
        
        return assignments.stream()
            .map(assignment -> convertToTicketResponse(assignment.getTicket()))
            .collect(Collectors.toList());
    }

    /**
     * Convert Ticket entity to TicketResponse DTO
     */
    private TicketResponse convertToTicketResponse(Ticket ticket) {
        Long customerId = null;
        String customerName = null;
        Long staffDepartmentId = null;
        String staffDepartmentName = null;
        Long assignedToStaffId = null;
        String assignedToStaffName = null;
        
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
        
        // Lấy thông tin nhân viên được assign
        try {
            if (ticket.getTicketAssignments() != null && !ticket.getTicketAssignments().isEmpty()) {
                // Lấy assignment mới nhất (giả sử list đã sắp xếp theo thời gian)
                TicketAssign latestAssignment = ticket.getTicketAssignments().stream()
                    .max((a1, a2) -> a1.getAssignedAt().compareTo(a2.getAssignedAt()))
                    .orElse(ticket.getTicketAssignments().get(0));
                    
                if (latestAssignment != null && latestAssignment.getAssignedTo() != null) {
                    assignedToStaffId = latestAssignment.getAssignedTo().getStaffId();
                    assignedToStaffName = latestAssignment.getAssignedTo().getName();
                }
            }
        } catch (Exception e) {
            log.warn("Ticket {} has no assignment info: {}", ticket.getTicketId(), e.getMessage());
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
            staffDepartmentName,
            assignedToStaffId,
            assignedToStaffName
        );
    }
}
