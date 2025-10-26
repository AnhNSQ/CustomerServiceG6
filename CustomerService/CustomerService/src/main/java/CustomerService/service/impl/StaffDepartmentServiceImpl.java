package CustomerService.service.impl;

import CustomerService.entity.StaffDepartment;
import CustomerService.repository.StaffDepartmentRepository;
import CustomerService.service.StaffDepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation cho StaffDepartmentService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StaffDepartmentServiceImpl implements StaffDepartmentService {
    
    private final StaffDepartmentRepository staffDepartmentRepository;
    
    /**
     * Lấy tất cả phòng ban
     */
    @Override
    @Transactional(readOnly = true)
    public List<StaffDepartment> getAllDepartments() {
        log.info("Lấy tất cả phòng ban");
        return staffDepartmentRepository.findAll();
    }
}

