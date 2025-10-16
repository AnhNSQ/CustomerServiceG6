package CustomerService.service;

import CustomerService.dto.StaffLoginRequest;
import CustomerService.dto.StaffResponse;

import java.util.Optional;

/**
 * Interface cho dịch vụ quản lý Staff
 * Tuân thủ Dependency Inversion Principle (DIP)
 */
public interface StaffService {

    /**
     * Đăng nhập staff
     * @deprecated Sử dụng AuthenticationService thay thế
     */
    @Deprecated
    StaffResponse login(StaffLoginRequest request);

    /**
     * Tìm staff theo ID
     */
    Optional<StaffResponse> findById(Long staffId);

    /**
     * Tìm staff theo email hoặc username
     */
    Optional<StaffResponse> findByEmailOrUsername(String emailOrUsername);
}