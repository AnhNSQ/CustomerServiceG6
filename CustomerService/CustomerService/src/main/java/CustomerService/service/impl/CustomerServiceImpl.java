package CustomerService.service.impl;

import CustomerService.dto.CustomerLoginRequest;
import CustomerService.dto.CustomerRegisterRequest;
import CustomerService.dto.CustomerResponse;
import CustomerService.entity.Customer;
import CustomerService.entity.Role;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.RoleRepository;
import CustomerService.repository.StaffRepository;
import CustomerService.service.BaseUserService;
import CustomerService.service.CustomerService;
import CustomerService.service.PasswordValidator;
import CustomerService.service.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class CustomerServiceImpl extends BaseUserService implements CustomerService {

    private final RoleRepository roleRepository;
    
    public CustomerServiceImpl(CustomerRepository customerRepository, 
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
     * Đăng nhập customer
     * @deprecated Sử dụng AuthenticationService thay thế
     */
    @Override
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
    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerResponse> findById(Long customerId) {
        return customerRepository.findByIdWithRole(customerId)
            .map(this::convertToCustomerResponse);
    }

    /**
     * Tìm customer theo email hoặc username
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerResponse> findByEmailOrUsername(String emailOrUsername) {
        return customerRepository.findActiveByEmailOrUsername(emailOrUsername)
            .map(this::convertToCustomerResponse);
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
        if (updateData.containsKey("name")) {
            String name = (String) updateData.get("name");
            if (name != null && !name.trim().isEmpty()) {
                customer.setName(name.trim());
                log.info("Cập nhật name: {}", name);
            }
        }

        if (updateData.containsKey("email")) {
            String email = (String) updateData.get("email");
            if (email != null && !email.trim().isEmpty()) {
                // Kiểm tra email đã tồn tại chưa (trừ email hiện tại)
                if (!email.equals(customer.getEmail()) && customerRepository.existsByEmail(email)) {
                    throw new RuntimeException("Email đã được sử dụng");
                }
                customer.setEmail(email.trim());
                log.info("Cập nhật email: {}", email);
            }
        }

        if (updateData.containsKey("username")) {
            String username = (String) updateData.get("username");
            if (username != null && !username.trim().isEmpty()) {
                // Kiểm tra username đã tồn tại chưa (trừ username hiện tại)
                if (!username.equals(customer.getUsername()) && customerRepository.existsByUsername(username)) {
                    throw new RuntimeException("Username đã được sử dụng");
                }
                customer.setUsername(username.trim());
                log.info("Cập nhật username: {}", username);
            }
        }

        if (updateData.containsKey("phone")) {
            String phone = (String) updateData.get("phone");
            customer.setPhone(phone != null && !phone.trim().isEmpty() ? phone.trim() : null);
            log.info("Cập nhật phone: {}", phone);
        }

        // Lưu customer đã cập nhật
        Customer updatedCustomer = customerRepository.save(customer);
        log.info("Cập nhật profile thành công cho customer ID: {}", customerId);

        return convertToCustomerResponse(updatedCustomer);
    }
}
