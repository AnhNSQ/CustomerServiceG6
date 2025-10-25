package CustomerService.service;

import CustomerService.dto.CustomerResponse;
import CustomerService.dto.StaffResponse;
import CustomerService.entity.Customer;
import CustomerService.entity.Role;
import CustomerService.entity.Staff;

import java.util.Set;

/**
 * Interface cho việc chuyển đổi entity thành DTO
 * Tuân thủ Single Responsibility Principle (SRP)
 */
public interface UserConverter {
    
    /**
     * Chuyển đổi Customer entity thành CustomerResponse DTO
     */
    CustomerResponse convertToCustomerResponse(Customer customer);
    
    /**
     * Chuyển đổi Staff entity thành StaffResponse DTO
     */
    StaffResponse convertToStaffResponse(Staff staff);
    
    /**
     * Trích xuất tên role từ Role entity
     */
    Set<String> extractRoleNames(Role role);
}
