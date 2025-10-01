package CustomerService.service.impl;

import CustomerService.service.PasswordValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Implementation đơn giản cho PasswordValidator
 * Không mã hóa mật khẩu theo yêu cầu hiện tại
 */
@Component
@Slf4j
public class SimplePasswordValidator implements PasswordValidator {
    
    @Override
    public boolean validatePassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            return false;
        }
        
        // So sánh trực tiếp (không mã hóa theo yêu cầu)
        boolean isValid = rawPassword.equals(storedPassword);
        
        if (!isValid) {
            log.warn("Password validation failed for user");
        }
        
        return isValid;
    }
    
    @Override
    public String encodePassword(String rawPassword) {
        // Trả về mật khẩu gốc (không mã hóa theo yêu cầu)
        return rawPassword;
    }
}
