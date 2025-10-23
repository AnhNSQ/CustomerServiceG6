package CustomerService.service.impl;

import CustomerService.service.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Implementation của PasswordValidator sử dụng BCrypt
 * Cung cấp mã hóa và xác thực mật khẩu an toàn
 */
@Component
@Primary
@Slf4j
@RequiredArgsConstructor
public class BcryptPasswordValidator implements PasswordValidator {
    
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public boolean validatePassword(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null) {
            log.warn("Password validation failed: null password provided");
            return false;
        }
        
        try {
            // Sử dụng BCrypt để so sánh mật khẩu
            boolean isValid = passwordEncoder.matches(rawPassword, storedPassword);
            
            if (!isValid) {
                log.warn("Password validation failed: incorrect password");
            } else {
                log.debug("Password validation successful");
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("Error during password validation", e);
            return false;
        }
    }
    
    @Override
    public String encodePassword(String rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("Raw password cannot be null");
        }
        
        try {
            // Mã hóa mật khẩu sử dụng BCrypt
            String encodedPassword = passwordEncoder.encode(rawPassword);
            log.debug("Password encoded successfully");
            return encodedPassword;
        } catch (Exception e) {
            log.error("Error during password encoding", e);
            throw new RuntimeException("Failed to encode password", e);
        }
    }
}
