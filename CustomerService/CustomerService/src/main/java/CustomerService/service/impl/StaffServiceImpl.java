package CustomerService.service.impl;

import CustomerService.dto.StaffLoginRequest;
import CustomerService.dto.StaffResponse;
import CustomerService.entity.Staff;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.StaffRepository;
import CustomerService.service.BaseUserService;
import CustomerService.service.PasswordValidator;
import CustomerService.service.StaffService;
import CustomerService.service.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional
public class StaffServiceImpl extends BaseUserService implements StaffService {
    
    public StaffServiceImpl(CustomerRepository customerRepository, 
                       StaffRepository staffRepository,
                       PasswordValidator passwordValidator,
                       UserConverter userConverter) {
        super(customerRepository, staffRepository, passwordValidator, userConverter);
    }

    /**
     * Đăng nhập staff
     * @deprecated Sử dụng AuthenticationService thay thế
     */
    @Override
    @Deprecated
    public StaffResponse login(StaffLoginRequest request) {
        log.info("Bắt đầu đăng nhập staff với email/username: {}", request.getEmailOrUsername());

        // Tìm staff theo email hoặc username
        Staff staff = findStaffByEmailOrUsername(request.getEmailOrUsername())
            .orElseThrow(() -> new RuntimeException("Email/Username hoặc mật khẩu không đúng"));

        // Xác thực mật khẩu
        validateStaffPassword(request.getPassword(), staff);

        log.info("Đăng nhập thành công staff với ID: {}", staff.getStaffId());
        return convertToStaffResponse(staff);
    }

    /**
     * Tìm staff theo ID
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<StaffResponse> findById(Long staffId) {
        return staffRepository.findByIdWithRole(staffId)
            .map(this::convertToStaffResponse);
    }

    /**
     * Tìm staff theo email hoặc username
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<StaffResponse> findByEmailOrUsername(String emailOrUsername) {
        return staffRepository.findActiveByEmailOrUsername(emailOrUsername)
            .map(this::convertToStaffResponse);
    }
}
