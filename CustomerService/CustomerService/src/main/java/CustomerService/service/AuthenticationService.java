package CustomerService.service;

import CustomerService.dto.CustomerLoginRequest;
import CustomerService.dto.CustomerResponse;
import CustomerService.dto.StaffLoginRequest;
import CustomerService.dto.StaffResponse;

/**
 * Interface cho dịch vụ xác thực
 * Tuân thủ Dependency Inversion Principle (DIP)
 */
public interface AuthenticationService {
    
    /**
     * Đăng nhập customer
     */
    CustomerResponse authenticateCustomer(CustomerLoginRequest request);
    
    /**
     * Đăng nhập staff
     */
    StaffResponse authenticateStaff(StaffLoginRequest request);
}
