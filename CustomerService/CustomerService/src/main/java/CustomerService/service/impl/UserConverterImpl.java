package CustomerService.service.impl;

import CustomerService.dto.CustomerResponse;
import CustomerService.dto.StaffResponse;
import CustomerService.entity.Customer;
import CustomerService.entity.Role;
import CustomerService.entity.Staff;
import CustomerService.service.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Implementation cho UserConverter
 */
@Component
@Slf4j
public class UserConverterImpl implements UserConverter {
    
    @Override
    public CustomerResponse convertToCustomerResponse(Customer customer) {
        Set<String> roleNames = extractRoleNames(customer.getRole());
        
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
    
    @Override
    public StaffResponse convertToStaffResponse(Staff staff) {
        Set<String> roleNames = extractRoleNames(staff.getRole());
        
        String departmentName = null;
        if (staff.getStaffDepartment() != null) {
            departmentName = staff.getStaffDepartment().getName();
        }
        
        return new StaffResponse(
            staff.getStaffId(),
            staff.getName(),
            staff.getEmail(),
            staff.getUsername(),
            staff.getPhone(),
            staff.getIsActive(),
            staff.getRegisterDate(),
            roleNames,
            departmentName
        );
    }
    
    @Override
    public Set<String> extractRoleNames(Role role) {
        if (role != null && role.getRoleName() != null) {
            return Set.of(role.getRoleName().name());
        }
        
        log.warn("Role is null or roleName is null");
        return Set.of("UNKNOWN");
    }
}
