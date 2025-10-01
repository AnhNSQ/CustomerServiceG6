package CustomerService.service.impl;

import CustomerService.dto.CustomerResponse;
import CustomerService.dto.StaffResponse;
import CustomerService.entity.Customer;
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
        
        return new StaffResponse(
            staff.getStaffId(),
            staff.getName(),
            staff.getEmail(),
            staff.getUsername(),
            staff.getPhone(),
            staff.getIsActive(),
            staff.getRegisterDate(),
            roleNames
        );
    }
    
    private Set<String> extractRoleNames(Object role) {
        if (role != null) {
            try {
                // Sử dụng reflection để lấy roleName
                String roleName = (String) role.getClass().getMethod("getRoleName").invoke(role);
                return Set.of(roleName);
            } catch (Exception e) {
                log.warn("Error extracting role name: ", e);
            }
        }
        
        log.warn("Role is null or cannot extract role name");
        return Set.of("UNKNOWN");
    }
}
