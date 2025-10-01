package CustomerService.exception;

/**
 * Custom exception cho lỗi xác thực
 * Tuân thủ Single Responsibility Principle (SRP)
 */
public class AuthenticationException extends RuntimeException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
