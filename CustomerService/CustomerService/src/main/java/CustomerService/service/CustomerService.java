package CustomerService.service;

import CustomerService.dto.CustomerLoginRequest;
import CustomerService.dto.CustomerRegisterRequest;
import CustomerService.dto.CustomerResponse;
import CustomerService.dto.CustomerTicketCreateRequest;
import CustomerService.dto.TicketResponse;
import CustomerService.entity.Customer;
import CustomerService.entity.Role;
import CustomerService.entity.StaffDepartment;
import CustomerService.entity.Ticket;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.RoleRepository;
import CustomerService.repository.StaffDepartmentRepository;
import CustomerService.repository.StaffRepository;
import CustomerService.repository.TicketRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class CustomerService extends BaseUserService {

    private final RoleRepository roleRepository;
    private final StaffDepartmentRepository staffDepartmentRepository;
    private final TicketRepository ticketRepository;
    
    public CustomerService(CustomerRepository customerRepository, 
                          StaffRepository staffRepository,
                          PasswordValidator passwordValidator,
                          UserConverter userConverter,
                          RoleRepository roleRepository,
                          StaffDepartmentRepository staffDepartmentRepository,
                          TicketRepository ticketRepository) {
        super(customerRepository, staffRepository, passwordValidator, userConverter);
        this.roleRepository = roleRepository;
        this.staffDepartmentRepository = staffDepartmentRepository;
        this.ticketRepository = ticketRepository;
    }

    /**
     * Đăng ký tài khoản customer mới
     */
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

        // Tạo customer mới
        Customer customer = new Customer(
            request.getName(),
            request.getEmail(),
            request.getUsername(),
            request.getPassword(), // Không encode password theo yêu cầu
            request.getPhone(),
            null // Role sẽ được set sau
        );

        // Gán role CUSTOMER mặc định
        Role customerRole = roleRepository.findByRoleName("CUSTOMER")
            .orElseThrow(() -> new RuntimeException("Role CUSTOMER không tồn tại"));
        customer.setRole(customerRole);

        // Lưu customer
        Customer savedCustomer = customerRepository.save(customer);
        log.info("Đăng ký thành công customer với ID: {}", savedCustomer.getCustomerId());

        return convertToCustomerResponse(savedCustomer);
    }

    /**
     * Đăng nhập customer
     * @deprecated Sử dụng AuthenticationService thay thế
     */
    @Deprecated
    public CustomerResponse login(CustomerLoginRequest request) {
        log.info("Bắt đầu đăng nhập với email/username: {}", request.getEmailOrUsername());

        // Tìm customer theo email hoặc username
        Customer customer = findCustomerByEmailOrUsername(request.getEmailOrUsername())
            .orElseThrow(() -> new RuntimeException("Email/Username hoặc mật khẩu không đúng"));

        // Xác thực mật khẩu
        validateCustomerPassword(request.getPassword(), customer);

        log.info("Đăng nhập thành công customer với ID: {}", customer.getCustomerId());
        return convertToCustomerResponse(customer);
    }

    /**
     * Tìm customer theo ID
     */
    @Transactional(readOnly = true)
    public Optional<CustomerResponse> findById(Long customerId) {
        return customerRepository.findByIdWithRole(customerId)
            .map(this::convertToCustomerResponse);
    }

    /**
     * Tìm customer theo email hoặc username
     */
    @Transactional(readOnly = true)
    public Optional<CustomerResponse> findByEmailOrUsername(String emailOrUsername) {
        return customerRepository.findActiveByEmailOrUsername(emailOrUsername)
            .map(this::convertToCustomerResponse);
    }

    /**
     * Kiểm tra email đã tồn tại
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }

    /**
     * Kiểm tra username đã tồn tại
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return customerRepository.existsByUsername(username);
    }

    /**
     * Tạo ticket mới cho customer
     */
    public TicketResponse createTicket(Long customerId, CustomerTicketCreateRequest request) {
        log.info("Bắt đầu tạo ticket cho customer {} với department {}", customerId, request.getDepartmentId());

        // Tìm customer
        Customer customer = customerRepository.findByIdWithRole(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        // Tìm department theo ID từ request
        StaffDepartment department = staffDepartmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new RuntimeException("Department not found with ID: " + request.getDepartmentId()));

        // Tạo ticket mới
        Ticket ticket = new Ticket();
        ticket.setCustomer(customer);
        ticket.setStaffDepartment(department);
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setPriority(Ticket.Priority.MEDIUM);
        ticket.setStatus(Ticket.Status.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());

        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Tạo ticket thành công với ID: {} và gán cho department: {}", savedTicket.getTicketId(), department.getName());
        
        return convertToTicketResponse(savedTicket);
    }

    /**
     * Lấy danh sách ticket của customer với phân trang và tổng số
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTicketsByCustomerIdWithPaginationAndTotal(Long customerId, int page, int size) {
        log.info("Lấy danh sách ticket của customer {} với phân trang (page: {}, size: {})", customerId, page, size);

        List<Ticket> allTickets = ticketRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        
        // Tính tổng số ticket
        int totalTickets = allTickets.size();
        
        // Phân trang thủ công
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, allTickets.size());
        
        List<Ticket> paginatedTickets = List.of();
        if (startIndex < allTickets.size()) {
            paginatedTickets = allTickets.subList(startIndex, endIndex);
        }

        List<TicketResponse> ticketResponses = paginatedTickets.stream()
            .map(this::convertToTicketResponse)
            .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("tickets", ticketResponses);
        result.put("totalTickets", totalTickets);
        result.put("currentPage", page);
        result.put("pageSize", size);
        result.put("totalPages", (int) Math.ceil((double) totalTickets / size));

        return result;
    }

    /**
     * Lấy N ticket gần nhất của customer
     */
    @Transactional(readOnly = true)
    public List<TicketResponse> getRecentTicketsByCustomerId(Long customerId, int limit) {
        log.info("Lấy {} ticket gần nhất của customer {}", limit, customerId);

        List<Ticket> allTickets = ticketRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        
        List<Ticket> recentTickets = allTickets.stream()
            .limit(limit)
            .collect(Collectors.toList());

        return recentTickets.stream()
            .map(this::convertToTicketResponse)
            .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách ticket của customer
     */
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
    @Transactional(readOnly = true)
    public Optional<TicketResponse> getTicketById(Long ticketId) {
        log.info("Lấy ticket với ID {}", ticketId);
        
        return ticketRepository.findByIdWithCustomer(ticketId)
            .map(this::convertToTicketResponse);
    }

    /**
     * Tìm kiếm ticket theo tên cho customer (filter đơn giản)
     */
    @Transactional(readOnly = true)
    public List<TicketResponse> searchTicketsByCustomerId(Long customerId, String query) {
        log.info("Filter ticket với query '{}' cho customer {}", query, customerId);

        // Lấy tất cả ticket của customer
        List<Ticket> allTickets = ticketRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
        
        // Filter ở Java level (đơn giản hơn)
        List<Ticket> filteredTickets = allTickets.stream()
            .filter(ticket -> ticket.getSubject().toLowerCase().contains(query.toLowerCase()))
            .collect(Collectors.toList());

        return filteredTickets.stream()
            .map(this::convertToTicketResponse)
            .collect(Collectors.toList());
    }

    /**
     * Cập nhật thông tin profile của customer
     */
    @Transactional
    public CustomerResponse updateProfile(Long customerId, Map<String, Object> updateData) {
        log.info("Cập nhật profile cho customer {} với data: {}", customerId, updateData);

        Customer customer = customerRepository.findByIdWithRole(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + customerId));

        if (updateData.containsKey("name")) {
            String name = (String) updateData.get("name");
            if (name != null && !name.trim().isEmpty()) {
                customer.setName(name.trim());
            }
        }

        if (updateData.containsKey("email")) {
            String email = (String) updateData.get("email");
            if (email != null && !email.trim().isEmpty()) {
                Optional<Customer> existingCustomer = customerRepository.findByEmail(email.trim());
                if (existingCustomer.isPresent() && !existingCustomer.get().getCustomerId().equals(customerId)) {
                    throw new RuntimeException("Email đã được sử dụng bởi tài khoản khác");
                }
                customer.setEmail(email.trim());
            }
        }

        if (updateData.containsKey("username")) {
            String username = (String) updateData.get("username");
            if (username != null && !username.trim().isEmpty()) {
                Optional<Customer> existingCustomer = customerRepository.findByUsername(username.trim());
                if (existingCustomer.isPresent() && !existingCustomer.get().getCustomerId().equals(customerId)) {
                    throw new RuntimeException("Username đã được sử dụng bởi tài khoản khác");
                }
                customer.setUsername(username.trim());
            }
        }

        if (updateData.containsKey("phone")) {
            String phone = (String) updateData.get("phone");
            customer.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
        }

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Cập nhật profile thành công cho customer {}", customerId);
        
        return convertToCustomerResponse(savedCustomer);
    }

    /**
     * Xóa ticket của customer
     */
    @Transactional
    public boolean deleteTicket(Long ticketId, Long customerId) {
        log.info("Xóa ticket {} của customer {}", ticketId, customerId);
        
        // Tìm ticket và kiểm tra quyền sở hữu
        Optional<Ticket> ticketOpt = ticketRepository.findByIdWithCustomer(ticketId);
        if (ticketOpt.isEmpty()) {
            log.warn("Ticket {} không tồn tại", ticketId);
            return false;
        }
        
        Ticket ticket = ticketOpt.get();
        
        // Kiểm tra ticket có thuộc về customer này không
        if (!ticket.getCustomer().getCustomerId().equals(customerId)) {
            log.warn("Customer {} không có quyền xóa ticket {}", customerId, ticketId);
            return false;
        }
        
        // Xóa ticket
        ticketRepository.delete(ticket);
        log.info("Xóa ticket {} thành công", ticketId);
        
        return true;
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
