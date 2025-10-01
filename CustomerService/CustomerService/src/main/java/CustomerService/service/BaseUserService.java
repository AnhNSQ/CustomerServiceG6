package CustomerService.service;

import CustomerService.dto.CustomerResponse;
import CustomerService.dto.StaffResponse;
import CustomerService.entity.Customer;
import CustomerService.entity.Staff;
import CustomerService.exception.InvalidCredentialsException;
import CustomerService.exception.UserNotFoundException;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.StaffRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Abstract base class cho các service xử lý user
 * Tuân thủ Open/Closed Principle (OCP) - mở rộng được, đóng sửa đổi
 */
@Slf4j
public abstract class BaseUserService {
    
    protected final CustomerRepository customerRepository;
    protected final StaffRepository staffRepository;
    protected final PasswordValidator passwordValidator;
    protected final UserConverter userConverter;
    
    protected BaseUserService(CustomerRepository customerRepository, 
                             StaffRepository staffRepository,
                             PasswordValidator passwordValidator,
                             UserConverter userConverter) {
        this.customerRepository = customerRepository;
        this.staffRepository = staffRepository;
        this.passwordValidator = passwordValidator;
        this.userConverter = userConverter;
    }
    
    /**
     * Tìm customer theo email hoặc username
     */
    protected Optional<Customer> findCustomerByEmailOrUsername(String emailOrUsername) {
        return customerRepository.findActiveByEmailOrUsername(emailOrUsername);
    }
    
    /**
     * Tìm staff theo email hoặc username
     */
    protected Optional<Staff> findStaffByEmailOrUsername(String emailOrUsername) {
        return staffRepository.findActiveByEmailOrUsername(emailOrUsername);
    }
    
    /**
     * Xác thực mật khẩu cho customer
     */
    protected void validateCustomerPassword(String rawPassword, Customer customer) {
        if (!passwordValidator.validatePassword(rawPassword, customer.getPassword())) {
            throw new InvalidCredentialsException();
        }
    }
    
    /**
     * Xác thực mật khẩu cho staff
     */
    protected void validateStaffPassword(String rawPassword, Staff staff) {
        if (!passwordValidator.validatePassword(rawPassword, staff.getPassword())) {
            throw new InvalidCredentialsException();
        }
    }
    
    /**
     * Chuyển đổi Customer thành CustomerResponse
     */
    protected CustomerResponse convertToCustomerResponse(Customer customer) {
        return userConverter.convertToCustomerResponse(customer);
    }
    
    /**
     * Chuyển đổi Staff thành StaffResponse
     */
    protected StaffResponse convertToStaffResponse(Staff staff) {
        return userConverter.convertToStaffResponse(staff);
    }
    
    /**
     * Tìm customer theo ID và throw exception nếu không tìm thấy
     */
    protected Customer findCustomerByIdOrThrow(Long customerId) {
        return customerRepository.findByIdWithRole(customerId)
            .orElseThrow(() -> new UserNotFoundException("Không tìm thấy thông tin customer"));
    }
    
    /**
     * Tìm staff theo ID và throw exception nếu không tìm thấy
     */
    protected Staff findStaffByIdOrThrow(Long staffId) {
        return staffRepository.findByIdWithRole(staffId)
            .orElseThrow(() -> new UserNotFoundException("Không tìm thấy thông tin staff"));
    }
}
