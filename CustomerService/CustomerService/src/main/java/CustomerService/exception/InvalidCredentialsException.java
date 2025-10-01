package CustomerService.exception;

/**
 * Custom exception cho thông tin đăng nhập không hợp lệ
 * Tuân thủ Single Responsibility Principle (SRP)
 */
public class InvalidCredentialsException extends AuthenticationException {
    
    public InvalidCredentialsException() {
        super("Email/Username hoặc mật khẩu không đúng");
    }
    
    public InvalidCredentialsException(String message) {
        super(message);
    }
}
