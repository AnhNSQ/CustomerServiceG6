package CustomerService.service;

import CustomerService.dto.CustomerLoginRequest;
import CustomerService.dto.CustomerRegisterRequest;
import CustomerService.dto.CustomerResponse;
import CustomerService.entity.Customer;
import CustomerService.entity.Role;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.RoleRepository;
import CustomerService.repository.StaffRepository;
import CustomerService.service.PasswordValidator;
import CustomerService.service.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional
public class CustomerService extends BaseUserService {

    private final RoleRepository roleRepository;
    
    public CustomerService(CustomerRepository customerRepository, 
                          StaffRepository staffRepository,
                          PasswordValidator passwordValidator,
                          UserConverter userConverter,
                          RoleRepository roleRepository) {
        super(customerRepository, staffRepository, passwordValidator, userConverter);
        this.roleRepository = roleRepository;
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

}
