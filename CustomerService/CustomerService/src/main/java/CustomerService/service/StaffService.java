package CustomerService.service;

import CustomerService.dto.StaffLoginRequest;
import CustomerService.dto.StaffResponse;
import CustomerService.entity.Staff;
import CustomerService.repository.CustomerRepository;
import CustomerService.repository.StaffRepository;
import CustomerService.service.PasswordValidator;
import CustomerService.service.UserConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional
public class StaffService extends BaseUserService {
    
    public StaffService(CustomerRepository customerRepository, 
                       StaffRepository staffRepository,
                       PasswordValidator passwordValidator,
                       UserConverter userConverter) {
        super(customerRepository, staffRepository, passwordValidator, userConverter);
    }

    /**
     * Đăng nhập staff
     * @deprecated Sử dụng AuthenticationService thay thế
     */
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
    @Transactional(readOnly = true)
    public Optional<StaffResponse> findById(Long staffId) {
        return staffRepository.findByIdWithRole(staffId)
            .map(this::convertToStaffResponse);
    }

    /**
     * Tìm staff theo email hoặc username
     */
    @Transactional(readOnly = true)
    public Optional<StaffResponse> findByEmailOrUsername(String emailOrUsername) {
        return staffRepository.findActiveByEmailOrUsername(emailOrUsername)
            .map(this::convertToStaffResponse);
    }

}

