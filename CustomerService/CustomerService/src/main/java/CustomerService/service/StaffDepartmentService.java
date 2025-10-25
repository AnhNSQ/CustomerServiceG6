package CustomerService.service;

import CustomerService.entity.StaffDepartment;

import java.util.List;

/**
 * Service cho StaffDepartment - xử lý nghiệp vụ phòng ban
 */
public interface StaffDepartmentService {
    
    /**
     * Lấy tất cả phòng ban
     */
    List<StaffDepartment> getAllDepartments();
}

