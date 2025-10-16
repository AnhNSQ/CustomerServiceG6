package CustomerService.service;

import CustomerService.dto.CustomerLoginRequest;
import CustomerService.dto.CustomerRegisterRequest;
import CustomerService.dto.CustomerResponse;

import java.util.Map;
import java.util.Optional;

/**
 * Interface cho dịch vụ quản lý Customer
 * Tuân thủ Dependency Inversion Principle (DIP)
 */
public interface CustomerService {

    /**
     * Đăng ký tài khoản customer mới
     */
    CustomerResponse register(CustomerRegisterRequest request);

    /**
     * Đăng nhập customer
     * @deprecated Sử dụng AuthenticationService thay thế
     */
    @Deprecated
    CustomerResponse login(CustomerLoginRequest request);

    /**
     * Tìm customer theo ID
     */
    Optional<CustomerResponse> findById(Long customerId);

    /**
     * Tìm customer theo email hoặc username
     */
    Optional<CustomerResponse> findByEmailOrUsername(String emailOrUsername);

    /**
     * Kiểm tra email đã tồn tại
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra username đã tồn tại
     */
    boolean existsByUsername(String username);

    /**
     * Cập nhật thông tin customer
     */
    CustomerResponse updateProfile(Long customerId, Map<String, Object> updateData);
}