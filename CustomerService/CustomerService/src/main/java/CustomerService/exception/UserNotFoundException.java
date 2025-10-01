package CustomerService.exception;

/**
 * Custom exception cho trường hợp không tìm thấy user
 * Tuân thủ Single Responsibility Principle (SRP)
 */
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
