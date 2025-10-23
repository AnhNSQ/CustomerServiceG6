package CustomerService.service;

import CustomerService.dto.StaffLoginRequest;
import CustomerService.dto.StaffResponse;
import CustomerService.dto.TicketResponse;
import CustomerService.entity.Staff;
import CustomerService.entity.Ticket;
import CustomerService.entity.TicketAssign;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.StaffRepository;
import CustomerService.repository.TicketRepository;
import CustomerService.repository.TicketAssignRepository;
import CustomerService.service.PasswordValidator;
import CustomerService.service.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class StaffService extends BaseUserService {
    
    private final TicketRepository ticketRepository;
    private final TicketAssignRepository ticketAssignRepository;
    
    public StaffService(CustomerRepository customerRepository, 
                       StaffRepository staffRepository,
                       PasswordValidator passwordValidator,
                       UserConverter userConverter,
                       TicketRepository ticketRepository,
                       TicketAssignRepository ticketAssignRepository) {
        super(customerRepository, staffRepository, passwordValidator, userConverter);
        this.ticketRepository = ticketRepository;
        this.ticketAssignRepository = ticketAssignRepository;
    }

    /**
     * Đăng nhập staff
     * @deprecated Sử dụng AuthenticationService thay thế
     */
    @Deprecated
    public StaffResponse login(StaffLoginRequest request) {
        log.info("Bắt đầu đăng nhập staff với email/username: {}", request.getEmailOrUsername());

        // Tìm staff theo email hoặc username
        Staff staff = findStaffByEmailOrUsername(request.getEmailOrUsername())
            .orElseThrow(() -> new RuntimeException("Email/Username hoặc mật khẩu không đúng"));

        // Xác thực mật khẩu
        validateStaffPassword(request.getPassword(), staff);

        log.info("Đăng nhập thành công staff với ID: {}", staff.getStaffId());
        return convertToStaffResponse(staff);
    }

    /**
     * Tìm staff theo ID
     */
    @Transactional(readOnly = true)
    public Optional<StaffResponse> findById(Long staffId) {
        return staffRepository.findByIdWithRole(staffId)
            .map(this::convertToStaffResponse);
    }

    /**
     * Tìm staff theo email hoặc username
     */
    @Transactional(readOnly = true)
    public Optional<StaffResponse> findByEmailOrUsername(String emailOrUsername) {
        return staffRepository.findActiveByEmailOrUsername(emailOrUsername)
            .map(this::convertToStaffResponse);
    }

    /**
     * Kiểm tra staff có phải LEADER không
     */
    @Transactional(readOnly = true)
    public boolean isLeader(Long staffId) {
        Staff staff = staffRepository.findByIdWithRole(staffId)
            .orElseThrow(() -> new RuntimeException("Staff not found with ID: " + staffId));
        
        return "LEADER".equals(staff.getRole().getRoleName());
    }

    /**
     * LEADER: Lấy danh sách ticket của phòng ban
     */
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByLeaderDepartment(Long leaderId) {
        log.info("Lấy danh sách ticket của phòng ban cho LEADER {}", leaderId);
        
        // Lấy thông tin LEADER
        Staff leader = staffRepository.findByIdWithRole(leaderId)
            .orElseThrow(() -> new RuntimeException("Leader not found with ID: " + leaderId));
        
        if (!"LEADER".equals(leader.getRole().getRoleName())) {
            throw new RuntimeException("Staff is not a LEADER");
        }
        
        // Lấy ticket của phòng ban
        List<Ticket> tickets = ticketRepository.findByStaffDepartmentIdOrderByCreatedAtDesc(
            leader.getStaffDepartment().getStaffDepartmentId()
        );
        
        return tickets.stream()
            .map(this::convertToTicketResponse)
            .collect(Collectors.toList());
    }

    /**
     * LEADER: Lấy danh sách nhân viên trong phòng ban
     */
    @Transactional(readOnly = true)
    public List<StaffResponse> getStaffByLeaderDepartment(Long leaderId) {
        log.info("Lấy danh sách nhân viên trong phòng ban cho LEADER {}", leaderId);
        
        // Lấy thông tin LEADER
        Staff leader = staffRepository.findByIdWithRole(leaderId)
            .orElseThrow(() -> new RuntimeException("Leader not found with ID: " + leaderId));
        
        if (!"LEADER".equals(leader.getRole().getRoleName())) {
            throw new RuntimeException("Staff is not a LEADER");
        }
        
        // Lấy nhân viên trong cùng phòng ban (trừ LEADER)
        List<Staff> staffList = staffRepository.findByStaffDepartmentIdAndRoleNameNot(
            leader.getStaffDepartment().getStaffDepartmentId(),
            "LEADER"
        );
        
        return staffList.stream()
            .map(this::convertToStaffResponse)
            .collect(Collectors.toList());
    }

    /**
     * LEADER: Phân công ticket cho nhân viên
     */
    @Transactional
    public boolean assignTicketToStaff(Long ticketId, Long staffId, Long leaderId, String note) {
        log.info("LEADER {} phân công ticket {} cho staff {}", leaderId, ticketId, staffId);
        
        // Kiểm tra LEADER
        Staff leader = staffRepository.findByIdWithRole(leaderId)
            .orElseThrow(() -> new RuntimeException("Leader not found with ID: " + leaderId));
        
        if (!"LEADER".equals(leader.getRole().getRoleName())) {
            throw new RuntimeException("Staff is not a LEADER");
        }
        
        // Kiểm tra ticket
        Ticket ticket = ticketRepository.findByIdWithCustomer(ticketId)
            .orElseThrow(() -> new RuntimeException("Ticket not found with ID: " + ticketId));
        
        // Kiểm tra ticket có thuộc phòng ban của LEADER không
        if (!ticket.getStaffDepartment().getStaffDepartmentId().equals(leader.getStaffDepartment().getStaffDepartmentId())) {
            throw new RuntimeException("Ticket does not belong to leader's department");
        }
        
        // Kiểm tra staff
        Staff staff = staffRepository.findByIdWithRole(staffId)
            .orElseThrow(() -> new RuntimeException("Staff not found with ID: " + staffId));
        
        // Kiểm tra staff có cùng phòng ban với LEADER không
        if (!staff.getStaffDepartment().getStaffDepartmentId().equals(leader.getStaffDepartment().getStaffDepartmentId())) {
            throw new RuntimeException("Staff does not belong to leader's department");
        }
        
        // Kiểm tra staff có phải FINANCIAL_STAFF hoặc TECHNICAL_SUPPORT không
        String staffRole = staff.getRole().getRoleName();
        if (!"FINANCIAL_STAFF".equals(staffRole) && !"TECHNICAL_SUPPORT".equals(staffRole)) {
            throw new RuntimeException("Staff must be FINANCIAL_STAFF or TECHNICAL_SUPPORT");
        }
        
        // Tạo ticket assignment
        TicketAssign assignment = new TicketAssign();
        assignment.setTicket(ticket);
        assignment.setAssignedTo(staff);
        assignment.setAssignedBy(leader);
        assignment.setAssignedAt(LocalDateTime.now());
        
        // Set role needed based on staff role
        if ("FINANCIAL_STAFF".equals(staffRole)) {
            assignment.setRoleNeeded(TicketAssign.RoleNeeded.FINANCIAL_STAFF);
        } else if ("TECHNICAL_SUPPORT".equals(staffRole)) {
            assignment.setRoleNeeded(TicketAssign.RoleNeeded.TECHNICAL_SUPPORT);
        }
        
        ticketAssignRepository.save(assignment);
        
        // Cập nhật status ticket
        ticket.setStatus(Ticket.Status.ASSIGNED);
        ticketRepository.save(ticket);
        
        log.info("Phân công ticket {} cho staff {} thành công", ticketId, staffId);
        return true;
    }

    /**
     * STAFF: Lấy danh sách ticket được phân công
     */
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsAssignedToStaff(Long staffId) {
        log.info("Lấy danh sách ticket được phân công cho staff {}", staffId);
        
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

