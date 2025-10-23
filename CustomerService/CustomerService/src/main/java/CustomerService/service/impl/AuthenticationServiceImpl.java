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
import CustomerService.service.BaseUserService;
import CustomerService.service.PasswordValidator;
import CustomerService.service.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation cho AuthenticationService
 * Tuân thủ Dependency Inversion Principle (DIP)
 */
@Service
@Slf4j
@Transactional
public class AuthenticationServiceImpl extends BaseUserService implements AuthenticationService {

    public AuthenticationServiceImpl(CustomerRepository customerRepository,
                                     StaffRepository staffRepository,
                                     PasswordValidator passwordValidator,
                                     UserConverter userConverter) {
        super(customerRepository, staffRepository, passwordValidator, userConverter);
    }

    @Override
    public CustomerResponse authenticateCustomer(CustomerLoginRequest request) {
        log.info("Bắt đầu xác thực customer với email/username: {}", request.getEmailOrUsername());

        // Tìm customer theo email hoặc username
        Customer customer = findCustomerByEmailOrUsername(request.getEmailOrUsername())
                .orElseThrow(InvalidCredentialsException::new);

        // Xác thực mật khẩu
        validateCustomerPassword(request.getPassword(), customer);

        log.info("Xác thực thành công customer với ID: {}", customer.getCustomerId());
        return convertToCustomerResponse(customer);
    }

    @Override
    public StaffResponse authenticateStaff(StaffLoginRequest request) {
        log.info("Bắt đầu xác thực staff với email/username: {}", request.getEmailOrUsername());

        // Tìm staff theo email hoặc username
        Staff staff = findStaffByEmailOrUsername(request.getEmailOrUsername())
                .orElseThrow(InvalidCredentialsException::new);

        // Xác thực mật khẩu
        validateStaffPassword(request.getPassword(), staff);

        log.info("Xác thực thành công staff với ID: {}", staff.getStaffId());
        return convertToStaffResponse(staff);
    }
}