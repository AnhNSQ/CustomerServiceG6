package CustomerService.service;

import CustomerService.dto.CustomerLoginRequest;
import CustomerService.dto.CustomerRegisterRequest;
import CustomerService.dto.CustomerResponse;
import CustomerService.dto.CustomerUpdateRequest;
import CustomerService.entity.Customer;
import CustomerService.entity.Role;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final RoleRepository roleRepository;

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

        return convertToResponse(savedCustomer);
    }

    /**
     * Đăng nhập customer
     */
    public CustomerResponse login(CustomerLoginRequest request) {
        log.info("Bắt đầu đăng nhập với email/username: {}", request.getEmailOrUsername());

        // Tìm customer theo email hoặc username
        Optional<Customer> customerOpt = customerRepository.findActiveByEmailOrUsername(request.getEmailOrUsername());

        if (customerOpt.isEmpty()) {
            throw new RuntimeException("Email/Username hoặc mật khẩu không đúng");
        }

        Customer customer = customerOpt.get();

        // Kiểm tra mật khẩu (không encode theo yêu cầu)
        if (!customer.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Email/Username hoặc mật khẩu không đúng");
        }

        log.info("Đăng nhập thành công customer với ID: {}", customer.getCustomerId());
        return convertToResponse(customer);
    }

    /**
     * Tìm customer theo ID
     */
    @Transactional(readOnly = true)
    public Optional<CustomerResponse> findById(Long customerId) {
        return customerRepository.findByIdWithRole(customerId)
            .map(this::convertToResponse);
    }

    /**
     * Tìm customer theo email hoặc username
     */
    @Transactional(readOnly = true)
    public Optional<CustomerResponse> findByEmailOrUsername(String emailOrUsername) {
        return customerRepository.findActiveByEmailOrUsername(emailOrUsername)
            .map(this::convertToResponse);
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
     * Cập nhật thông tin customer
     */
    public CustomerResponse updateProfile(Long customerId, CustomerUpdateRequest request) {
        log.info("Bắt đầu cập nhật thông tin customer với ID: {}", customerId);

        // Tìm customer hiện tại
        Customer existingCustomer = customerRepository.findByIdWithRole(customerId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin customer"));

        // Kiểm tra email đã tồn tại (trừ email hiện tại)
        if (!existingCustomer.getEmail().equals(request.getEmail()) && 
            customerRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng");
        }

        // Kiểm tra username đã tồn tại (trừ username hiện tại)
        if (!existingCustomer.getUsername().equals(request.getUsername()) && 
            customerRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username đã được sử dụng");
        }

        // Cập nhật thông tin
        existingCustomer.setName(request.getName());
        existingCustomer.setEmail(request.getEmail());
        existingCustomer.setUsername(request.getUsername());
        existingCustomer.setPhone(request.getPhone());

        // Lưu thay đổi
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        log.info("Cập nhật thành công thông tin customer với ID: {}", updatedCustomer.getCustomerId());

        return convertToResponse(updatedCustomer);
    }

    /**
     * Chuyển đổi Customer entity thành CustomerResponse DTO
     */
    private CustomerResponse convertToResponse(Customer customer) {
        Set<String> roleNames;
        if (customer.getRole() != null) {
            roleNames = Set.of(customer.getRole().getRoleName());
        } else {
            log.warn("Customer {} has null role", customer.getCustomerId());
            roleNames = Set.of("UNKNOWN");
        }

        return new CustomerResponse(
            customer.getCustomerId(),
            customer.getName(),
            customer.getEmail(),
            customer.getUsername(),
            customer.getPhone(),
            customer.getIsActive(),
            customer.getRegisterDate(),
            roleNames
        );
    }
}
