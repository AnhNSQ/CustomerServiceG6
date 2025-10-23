package CustomerService.service.impl;

import CustomerService.dto.CustomerLoginRequest;
import CustomerService.dto.CustomerResponse;
import CustomerService.dto.StaffLoginRequest;
import CustomerService.dto.StaffResponse;
import CustomerService.entity.Customer;
import CustomerService.entity.Staff;
import CustomerService.exception.InvalidCredentialsException;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.StaffRepository;
import CustomerService.service.AuthenticationService;
import CustomerService.service.PasswordValidator;
import CustomerService.service.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation cho AuthenticationService
 * Tuân thủ Dependency Inversion Principle (DIP)
 */
@Service
@Slf4j
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {
    
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final PasswordValidator passwordValidator;
    private final UserConverter userConverter;
    
    public AuthenticationServiceImpl(CustomerRepository customerRepository, 
                                   StaffRepository staffRepository,
                                   PasswordValidator passwordValidator,
                                   UserConverter userConverter) {
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
        this.passwordValidator = passwordValidator;
        this.userConverter = userConverter;
    }

    /**
     * Đăng nhập customer
     */
    @Override
    @Transactional(readOnly = true)
    public CustomerResponse authenticateCustomer(CustomerLoginRequest request) {
        return loginCustomer(request);
    }

    /**
     * Đăng nhập staff
     */
    @Override
    @Transactional(readOnly = true)
    public StaffResponse authenticateStaff(StaffLoginRequest request) {
        return loginStaff(request);
    }

    /**
     * Đăng nhập customer (internal method)
     */
    @Transactional(readOnly = true)
    public CustomerResponse loginCustomer(CustomerLoginRequest request) {
        log.info("Bắt đầu đăng nhập customer với email/username: {}", request.getEmailOrUsername());

        // Tìm customer theo email hoặc username
        Customer customer = customerRepository.findActiveByEmailOrUsername(request.getEmailOrUsername())
            .orElseThrow(() -> new InvalidCredentialsException("Email/Username hoặc mật khẩu không đúng"));

        // Xác thực mật khẩu
        passwordValidator.validatePassword(request.getPassword(), customer.getPassword());

        log.info("Đăng nhập thành công customer với ID: {}", customer.getCustomerId());
        return userConverter.convertToCustomerResponse(customer);
    }

    /**
     * Đăng nhập staff (internal method)
     */
    @Transactional(readOnly = true)
    public StaffResponse loginStaff(StaffLoginRequest request) {
        log.info("Bắt đầu đăng nhập staff với email/username: {}", request.getEmailOrUsername());

        // Tìm staff theo email hoặc username
        Staff staff = staffRepository.findActiveByEmailOrUsername(request.getEmailOrUsername())
            .orElseThrow(() -> new InvalidCredentialsException("Email/Username hoặc mật khẩu không đúng"));

        // Xác thực mật khẩu
        passwordValidator.validatePassword(request.getPassword(), staff.getPassword());

        log.info("Đăng nhập thành công staff với ID: {}", staff.getStaffId());
        return userConverter.convertToStaffResponse(staff);
    }

    /**
     * Tìm customer theo email hoặc username (internal method)
     */
    @Transactional(readOnly = true)
    public Optional<Customer> findCustomerByEmailOrUsername(String emailOrUsername) {
        return customerRepository.findActiveByEmailOrUsername(emailOrUsername);
    }

    /**
     * Tìm staff theo email hoặc username (internal method)
     */
    @Transactional(readOnly = true)
    public Optional<Staff> findStaffByEmailOrUsername(String emailOrUsername) {
        return staffRepository.findActiveByEmailOrUsername(emailOrUsername);
    }
}