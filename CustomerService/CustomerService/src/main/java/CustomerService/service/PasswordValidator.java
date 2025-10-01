package CustomerService.service;

/**
 * Interface cho việc xác thực mật khẩu
 * Tuân thủ Single Responsibility Principle (SRP)
 */
public interface PasswordValidator {
    
    /**
     * Xác thực mật khẩu
     * @param rawPassword Mật khẩu gốc từ request
     * @param storedPassword Mật khẩu đã lưu trong database
     * @return true nếu mật khẩu đúng, false nếu sai
     */
    boolean validatePassword(String rawPassword, String storedPassword);
    
    /**
     * Mã hóa mật khẩu (nếu cần)
     * @param rawPassword Mật khẩu gốc
     * @return Mật khẩu đã mã hóa
     */
    String encodePassword(String rawPassword);
}
