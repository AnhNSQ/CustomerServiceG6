package CustomerService.service.impl;

import CustomerService.dto.*;
import CustomerService.entity.*;
import CustomerService.repository.*;
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
    private final RoleRepository roleRepository;
    private final UserConverter userConverter;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderHistoryRepository orderHistoryRepository;
    
    public AdminServiceImpl(CustomerRepository customerRepository,
                           StaffRepository staffRepository,
                           PasswordValidator passwordValidator,
                           UserConverter userConverter,
                           TicketRepository ticketRepository,
                           StaffDepartmentRepository staffDepartmentRepository,
                           RoleRepository roleRepository,
                           OrderRepository orderRepository,
                           OrderDetailRepository orderDetailRepository,
                           OrderHistoryRepository orderHistoryRepository) {
        super(customerRepository, staffRepository, passwordValidator, userConverter);
        this.staffRepository = staffRepository;
        this.ticketRepository = ticketRepository;
        this.staffDepartmentRepository = staffDepartmentRepository;
        this.roleRepository = roleRepository;
        this.userConverter = userConverter;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.orderHistoryRepository = orderHistoryRepository;
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
     * ADMIN: Lấy tất cả customer trong hệ thống
     */
    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        log.info("ADMIN lấy tất cả customer trong hệ thống");
        
        List<Customer> customers = customerRepository.findAll();
        
        return customers.stream()
            .map(userConverter::convertToCustomerResponse)
            .collect(Collectors.toList());
    }

    /**
     * ADMIN: Lấy tất cả staff (role STAFF) trong hệ thống
     */
    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getAllStaffMembers() {
        log.info("ADMIN lấy tất cả staff (role STAFF) trong hệ thống");
        
        // Load all staff with role and department
        List<Staff> staffList = staffRepository.findAll();
        
        return staffList.stream()
            .filter(staff -> {
                try {
                    // Ensure role and department are loaded
                    if (staff.getRole() != null) {
                        staff.getRole().getRoleName(); // Trigger lazy loading if needed
                    }
                    if (staff.getStaffDepartment() != null) {
                        staff.getStaffDepartment().getName(); // Trigger lazy loading if needed
                    }
                    return staff.getRole() != null && 
                           staff.getRole().getRoleName() == Role.RoleName.STAFF;
                } catch (Exception e) {
                    log.warn("Error accessing role for staff {}: {}", staff.getStaffId(), e.getMessage());
                    return false;
                }
            })
            .map(userConverter::convertToStaffResponse)
            .collect(Collectors.toList());
    }

    /**
     * ADMIN: Lấy tất cả lead (role LEAD) trong hệ thống
     */
    @Override
    @Transactional(readOnly = true)
    public List<StaffResponse> getAllLeads() {
        log.info("ADMIN lấy tất cả lead (role LEAD) trong hệ thống");
        
        // Load all staff with role and department
        List<Staff> staffList = staffRepository.findAll();
        
        return staffList.stream()
            .filter(staff -> {
                try {
                    // Ensure role and department are loaded
                    if (staff.getRole() != null) {
                        staff.getRole().getRoleName(); // Trigger lazy loading if needed
                    }
                    if (staff.getStaffDepartment() != null) {
                        staff.getStaffDepartment().getName(); // Trigger lazy loading if needed
                    }
                    return staff.getRole() != null && 
                           staff.getRole().getRoleName() == Role.RoleName.LEAD;
                } catch (Exception e) {
                    log.warn("Error accessing role for staff {}: {}", staff.getStaffId(), e.getMessage());
                    return false;
                }
            })
            .map(userConverter::convertToStaffResponse)
            .collect(Collectors.toList());
    }

    /**
     * ADMIN: Tạo tài khoản staff hoặc lead mới
     */
    @Override
    public StaffResponse createStaff(StaffCreateRequest request) {
        log.info("ADMIN tạo tài khoản staff/lead với email: {}", request.getEmail());

        // Kiểm tra email đã tồn tại
        if (staffRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Kiểm tra username đã tồn tại
        if (staffRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã được sử dụng");
        }

        // Validate role name
        Role.RoleName roleName;
        try {
            roleName = Role.RoleName.valueOf(request.getRoleName().toUpperCase());
            if (roleName != Role.RoleName.STAFF && roleName != Role.RoleName.LEAD) {
                throw new RuntimeException("Vai trò phải là STAFF hoặc LEAD");
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Vai trò không hợp lệ: " + request.getRoleName());
        }

        // Tìm role
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new RuntimeException("Role " + roleName + " không tồn tại"));

        // Tìm department
        StaffDepartment department = staffDepartmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Phòng ban không tồn tại"));

        // Mã hóa mật khẩu trước khi lưu
        String encodedPassword = passwordValidator.encodePassword(request.getPassword());

        // Tạo staff mới
        Staff staff = new Staff(
                request.getName(),
                request.getEmail(),
                request.getUsername(),
                encodedPassword,
                request.getPhone(),
                role,
                department
        );

        // Lưu staff
        Staff savedStaff = staffRepository.save(staff);
        log.info("Đăng ký thành công staff với ID: {}", savedStaff.getStaffId());

        return userConverter.convertToStaffResponse(savedStaff);
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

    /**
     * ADMIN: Lấy tất cả đơn hàng trong hệ thống
     */
    @Override
    @Transactional(readOnly = true)
    public List<AdminOrderResponse> getAllOrders() {
        log.info("ADMIN lấy tất cả đơn hàng trong hệ thống");
        
        List<Order> orders = orderRepository.findAllWithCustomerOrderByOrderDateDesc();
        
        return orders.stream()
            .map(order -> {
                AdminOrderResponse response = new AdminOrderResponse();
                response.setOrderId(order.getOrderId());
                response.setCustomerId(order.getCustomer().getCustomerId());
                response.setCustomerName(order.getCustomer().getName());
                response.setCustomerEmail(order.getCustomer().getEmail());
                response.setTotalAmount(order.getTotalAmount());
                response.setOrderStatus(order.getOrderStatus().name());
                response.setShippingStatus(order.getShippingStatus().name());
                response.setCreatedAt(order.getOrderDate());
                response.setPaymentMethod(order.getPaymentMethod());
                response.setDeliveryMethod(order.getShippingMethod());
                return response;
            })
            .collect(Collectors.toList());
    }

    /**
     * ADMIN: Lấy chi tiết đơn hàng với lịch sử hoạt động
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<AdminOrderDetailResponse> getOrderDetailById(Long orderId) {
        log.info("ADMIN lấy chi tiết đơn hàng với ID {}", orderId);
        
        return orderRepository.findByIdWithCustomer(orderId)
            .map(order -> {
                AdminOrderDetailResponse response = new AdminOrderDetailResponse();
                
                // Basic order info
                response.setOrderId(order.getOrderId());
                response.setCustomerId(order.getCustomer().getCustomerId());
                response.setCustomerName(order.getCustomer().getName());
                response.setCustomerEmail(order.getCustomer().getEmail());
                response.setCustomerPhone(order.getCustomer().getPhone());
                response.setOrderDate(order.getOrderDate());
                response.setTotalAmount(order.getTotalAmount());
                response.setShippingCost(order.getCostEstimate());
                response.setShippingMethod(order.getShippingMethod());
                response.setEstimatedTime(order.getEstimatedTime());
                response.setOrderStatus(order.getOrderStatus().name());
                response.setShippingStatus(order.getShippingStatus().name());
                response.setPaymentMethod(order.getPaymentMethod());
                response.setShippingAddress(order.getShippingAddress());
                response.setRecipientName(order.getRecipientName());
                response.setRecipientPhone(order.getRecipientPhone());
                response.setNotes(order.getNotes());
                
                // Order details
                List<OrderDetail> orderDetails = orderDetailRepository.findByOrderOrderId(orderId);
                List<OrderDetailResponse> detailResponses = orderDetails.stream()
                    .map(detail -> {
                        OrderDetailResponse detailResponse = new OrderDetailResponse();
                        detailResponse.setOrderDetailId(detail.getOrderDetailId());
                        detailResponse.setProductId(detail.getProduct().getProductId());
                        detailResponse.setProductName(detail.getProduct().getName());
                        detailResponse.setProductDescription(detail.getProduct().getDescription());
                        detailResponse.setUnitPrice(detail.getUnitPrice());
                        detailResponse.setQuantity(detail.getQuantity());
                        detailResponse.setSubTotal(detail.getSubTotal());
                        return detailResponse;
                    })
                    .collect(Collectors.toList());
                response.setOrderDetails(detailResponses);
                
                // Activity history
                List<OrderHistory> histories = orderHistoryRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
                List<OrderHistoryResponse> historyResponses = histories.stream()
                    .map(history -> {
                        OrderHistoryResponse historyResponse = new OrderHistoryResponse();
                        historyResponse.setHistoryId(history.getHistoryId());
                        historyResponse.setAction(history.getAction());
                        historyResponse.setOldStatus(history.getOldStatus());
                        historyResponse.setNewStatus(history.getNewStatus());
                        historyResponse.setDescription(history.getDescription());
                        historyResponse.setPerformedByStaffId(history.getPerformedByStaff() != null ? history.getPerformedByStaff().getStaffId() : null);
                        historyResponse.setPerformedByStaffName(history.getPerformedByStaff() != null ? history.getPerformedByStaff().getName() : null);
                        historyResponse.setPerformedByCustomerId(history.getPerformedByCustomerId());
                        historyResponse.setCreatedAt(history.getCreatedAt());
                        return historyResponse;
                    })
                    .collect(Collectors.toList());
                response.setActivityHistory(historyResponses);
                
                return response;
            });
    }

    /**
     * ADMIN: Phê duyệt đơn hàng (chuyển từ PENDING sang PAID)
     */
    @Override
    public AdminOrderDetailResponse approveOrder(Long orderId, Long staffId, String notes) {
        log.info("ADMIN {} phê duyệt đơn hàng {}", staffId, orderId);
        
        Order order = orderRepository.findByIdWithCustomer(orderId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + orderId));
        
        if (order.getOrderStatus() != Order.OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể phê duyệt đơn hàng ở trạng thái PENDING. Trạng thái hiện tại: " + order.getOrderStatus());
        }
        
        Staff staff = staffRepository.findById(staffId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy staff với ID: " + staffId));
        
        // Lưu trạng thái cũ
        String oldStatus = order.getOrderStatus().name();
        
        // Cập nhật trạng thái
        order.setOrderStatus(Order.OrderStatus.PAID);
        Order savedOrder = orderRepository.save(order);
        
        // Tạo lịch sử
        String description = notes != null && !notes.trim().isEmpty() 
            ? "Đơn hàng được phê duyệt bởi " + staff.getName() + ". Ghi chú: " + notes
            : "Đơn hàng được phê duyệt bởi " + staff.getName();
        
        OrderHistory history = new OrderHistory(
            savedOrder,
            "APPROVED",
            oldStatus,
            Order.OrderStatus.PAID.name(),
            description,
            staff
        );
        orderHistoryRepository.save(history);
        
        log.info("Đơn hàng {} đã được phê duyệt thành công", orderId);
        
        // Return updated order detail
        return getOrderDetailById(orderId)
            .orElseThrow(() -> new RuntimeException("Không thể lấy chi tiết đơn hàng sau khi phê duyệt"));
    }
}
