package CustomerService.service.impl;

import CustomerService.dto.CustomerRegisterRequest;
import CustomerService.dto.CustomerResponse;
import CustomerService.dto.CustomerTicketCreateRequest;
import CustomerService.dto.TicketResponse;
import CustomerService.dto.ChangePasswordRequest;
import CustomerService.entity.Customer;
import CustomerService.entity.Role;
import CustomerService.entity.StaffDepartment;
import CustomerService.entity.Ticket;
import CustomerService.entity.TicketAssign;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.RoleRepository;
import CustomerService.repository.StaffDepartmentRepository;
import CustomerService.repository.StaffRepository;
import CustomerService.repository.TicketRepository;
import CustomerService.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class CustomerServiceImpl extends BaseUserService implements CustomerService {

    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;
    private final StaffDepartmentRepository staffDepartmentRepository;
    private final TicketRepository ticketRepository;
    private final PasswordValidator passwordValidator;
    private final UserConverter userConverter;
    private final OrderValidationService orderValidationService;
    
    public CustomerServiceImpl(CustomerRepository customerRepository, 
                          StaffRepository staffRepository,
                          PasswordValidator passwordValidator,
                          UserConverter userConverter,
                          RoleRepository roleRepository,
                          StaffDepartmentRepository staffDepartmentRepository,
                          TicketRepository ticketRepository,
                          OrderValidationService orderValidationService) {
        super(customerRepository, staffRepository, passwordValidator, userConverter);
        this.customerRepository = customerRepository;
        this.roleRepository = roleRepository;
        this.passwordValidator = passwordValidator;
        this.userConverter = userConverter;
        this.staffDepartmentRepository = staffDepartmentRepository;
        this.ticketRepository = ticketRepository;
        this.orderValidationService = orderValidationService;
    }

    /**
     * Đăng ký tài khoản customer mới
     */
    @Override
    public CustomerResponse register(CustomerRegisterRequest request) {
        log.info("Bắt đầu đăng ký customer với email: {}", request.getEmail());

        // Kiểm tra email đã tồn tại
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Kiểm tra username đã tồn tại
        if (customerRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã được sử dụng");
        }

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordValidator.encodePassword(request.getPassword());

        // Tạo customer mới
        Customer customer = new Customer(
                request.getName(),
                request.getEmail(),
                request.getUsername(),
                encodedPassword, // Sử dụng mật khẩu đã được mã hóa
                request.getPhone(),
                null // Role sẽ được set sau
        );

        // Gán role CUSTOMER mặc định
        Role customerRole = roleRepository.findByRoleName(Role.RoleName.CUSTOMER)
                .orElseThrow(() -> new RuntimeException("Role CUSTOMER không tồn tại"));
        customer.setRole(customerRole);

        // Lưu customer
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Đăng ký thành công customer với ID: {}", savedCustomer.getCustomerId());

        return convertToCustomerResponse(savedCustomer);
    }

    /**
     * Tìm customer theo ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerResponse> findById(Long customerId) {
        return customerRepository.findByIdWithRole(customerId)
            .map(userConverter::convertToCustomerResponse);
    }

    /**
     * Tìm customer theo email hoặc username
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerResponse> findByEmailOrUsername(String emailOrUsername) {
        return customerRepository.findActiveByEmailOrUsername(emailOrUsername)
            .map(userConverter::convertToCustomerResponse);
    }

    /**
     * Kiểm tra email đã tồn tại
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    /**
     * Kiểm tra username đã tồn tại
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return customerRepository.existsByUsername(username);
    }

    /**
     * Cập nhật thông tin customer
     */
    @Override
    public CustomerResponse updateProfile(Long customerId, Map<String, Object> updateData) {
        log.info("Bắt đầu cập nhật profile cho customer ID: {}", customerId);

        // Tìm customer hiện tại
        Customer customer = customerRepository.findByIdWithRole(customerId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy customer với ID: " + customerId));

        // Cập nhật các trường được phép
        updateData.forEach((key, value) -> {
            switch (key) {
                case "name":
                    customer.setName((String) value);
                    break;
                case "phone":
                    customer.setPhone((String) value);
                    break;
                default:
                    log.warn("Trường {} không được phép cập nhật", key);
            }
        });

        // Lưu customer đã cập nhật
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Cập nhật profile thành công cho customer ID: {}", customerId);

        return userConverter.convertToCustomerResponse(updatedCustomer);
    }

    /**
     * Thay đổi mật khẩu customer (xác thực mật khẩu cũ và xác nhận mật khẩu mới)
     */
    @Override
    public void changePassword(Long customerId, ChangePasswordRequest request) {
        log.info("Customer {} yêu cầu thay đổi mật khẩu", customerId);

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy customer với ID: " + customerId));

        if (!passwordValidator.validatePassword(request.getOldPassword(), customer.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }

        String encoded = passwordValidator.encodePassword(request.getNewPassword());
        customer.setPassword(encoded);
        customerRepository.save(customer);
        log.info("Customer {} đã thay đổi mật khẩu thành công", customerId);
    }

    /**
     * Tạo ticket mới cho customer
     */
    @Override
    public TicketResponse createTicket(Long customerId, CustomerTicketCreateRequest request) {
        log.info("Bắt đầu tạo ticket cho customer {}", customerId);

        Customer customer = customerRepository.findByIdWithRole(customerId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy customer với ID: " + customerId));

        StaffDepartment staffDepartment = staffDepartmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng ban với ID: " + request.getDepartmentId()));

        if (!orderValidationService.hasOrders(customerId)) {
            throw new RuntimeException("Customer phải có ít nhất một đơn hàng để tạo ticket hỗ trợ");
        }

        Ticket ticket = new Ticket(
            customer,
            staffDepartment,
            request.getSubject(),
            request.getDescription(),
            Ticket.Priority.MEDIUM
        );

        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Tạo ticket thành công với ID: {}", savedTicket.getTicketId());
        
        return convertToTicketResponse(savedTicket);
    }

    /**
     * Lấy danh sách ticket của customer với phân trang
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTicketsByCustomerIdWithPaginationAndTotal(Long customerId, int page, int size) {
        log.info("Lấy danh sách ticket của customer {} với phân trang", customerId);
        
        List<Ticket> tickets = ticketRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        
        int total = tickets.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        
        List<TicketResponse> ticketResponses = tickets.subList(start, end)
            .stream()
            .map(this::convertToTicketResponse)
            .collect(Collectors.toList());
        
        return Map.of(
            "tickets", ticketResponses,
            "total", total,
            "page", page,
            "size", size
        );
    }

    /**
     * Lấy ticket gần đây của customer
     */
    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getRecentTicketsByCustomerId(Long customerId, int limit) {
        log.info("Lấy {} ticket gần đây của customer {}", limit, customerId);
        
        List<Ticket> tickets = ticketRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        
        return tickets.stream()
            .limit(limit)
            .map(this::convertToTicketResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả ticket của customer
     */
    @Override
    @Transactional(readOnly = true)
    public List<TicketResponse> getTicketsByCustomerId(Long customerId) {
        log.info("Lấy danh sách ticket của customer {}", customerId);
        
        List<Ticket> tickets = ticketRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        
        return tickets.stream()
            .map(this::convertToTicketResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy ticket theo ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TicketResponse> getTicketById(Long ticketId) {
        log.info("Lấy ticket với ID {}", ticketId);
        
        return ticketRepository.findByIdWithCustomer(ticketId)
            .map(this::convertToTicketResponse);
    }

    /**
     * Xóa ticket
     */
    @Override
    @Transactional
    public boolean deleteTicket(Long ticketId, Long customerId) {
        log.info("Xóa ticket {} của customer {}", ticketId, customerId);
        
        Ticket ticket = ticketRepository.findByIdWithCustomer(ticketId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy ticket với ID: " + ticketId));
        
        if (!ticket.getCustomer().getCustomerId().equals(customerId)) {
            throw new RuntimeException("Bạn không có quyền xóa ticket này");
        }
        
        ticketRepository.delete(ticket);
        log.info("Xóa ticket {} thành công", ticketId);
        return true;
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